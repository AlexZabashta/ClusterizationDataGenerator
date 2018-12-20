package clusterization.vect;

import java.util.List;
import java.util.Random;
import java.util.function.ToDoubleFunction;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;

import clusterization.Dataset;
import clusterization.MetaFeaturesExtractor;
import clusterization.direct.RelationsGenerator;
import utils.MatrixUtils;
import utils.StatUtils;

public class GMMProblem implements DoubleProblem {

    private static final long serialVersionUID = 1L;
    final ToDoubleFunction<Dataset> errorFunction;
    final List<Dataset> datasets;
    final Random random = new Random();
    final int numFeatures, numObjects;
    public final MetaFeaturesExtractor extractor;

    public GMMProblem(int numObjects, int numFeatures, ToDoubleFunction<Dataset> errorFunction, List<Dataset> datasets, MetaFeaturesExtractor extractor) {
        this.numObjects = numObjects;
        this.numFeatures = numFeatures;
        this.errorFunction = errorFunction;
        this.datasets = datasets;
        this.extractor = extractor;
    }

    final static double lowerBound = -10;
    final static double upperBound = +10;

    @Override
    public int getNumberOfVariables() {
        return numFeatures * (numFeatures + 1) / 2;
    }

    @Override
    public int getNumberOfObjectives() {
        return 1;
    }

    @Override
    public int getNumberOfConstraints() {
        return 0;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName() + (datasets == null);
    }

    public DoubleSolution build(Dataset dataset) {
        DoubleSolution solution = new DefaultDoubleSolution(this);

        double[][] data = dataset.data();
        data = RelationsGenerator.changeNumFeatures(data, dataset.numFeatures, numFeatures, random);
        double[][] cov = StatUtils.covarianceMatrix(data.length, numFeatures, data);

        for (int i = 0; i < numFeatures; i++) {
            cov[i][i] += 1e-3;
        }

        try {
            double[][] sqrt = MatrixUtils.sqrt(numFeatures, cov);
            for (int index = 0, i = 0; i < numFeatures; i++) {
                for (int j = 0; j <= i; j++) {
                    solution.setVariableValue(index++, sqrt[i][j]);
                }
            }

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return solution;
    }

    public Dataset build(DoubleSolution solution) {
        double[][] sqrt = new double[numFeatures][numFeatures];
        for (int index = 0, i = 0; i < numFeatures; i++) {
            for (int j = 0; j <= i; j++) {
                sqrt[i][j] = solution.getVariableValue(index++);
            }
        }

        double[][] rand = new double[numObjects][numFeatures];

        for (int i = 0; i < numObjects; i++) {
            for (int j = 0; j < numFeatures; j++) {
                rand[i][j] = random.nextGaussian();
            }
        }
        double[][] data = MatrixUtils.mul(numObjects, numFeatures, numFeatures, rand, sqrt);

        return new Dataset(data, extractor);
    }

    @Override
    public void evaluate(DoubleSolution solution) {
        double avg = 0;
        for (int rep = 0; rep < 5; rep++) {
            avg += errorFunction.applyAsDouble(build(solution));
        }
        solution.setObjective(0, avg / 5);
    }

    @Override
    public DoubleSolution createSolution() {
        if (datasets == null || datasets.isEmpty()) {
            return new DefaultDoubleSolution(this);
        } else {
            return build(datasets.get(random.nextInt(datasets.size())));
        }
    }

    @Override
    public Double getLowerBound(int index) {
        return lowerBound;
    }

    @Override
    public Double getUpperBound(int index) {
        return upperBound;
    }

}
