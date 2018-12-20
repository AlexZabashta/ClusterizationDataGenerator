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
import utils.EndSearch;

public class SimpleProblem implements DoubleProblem {

    private static final long serialVersionUID = 1L;
    final ToDoubleFunction<Dataset> errorFunction;
    final List<Dataset> datasets;
    final Random random = new Random();
    final int numFeatures, numObjects;
    public final MetaFeaturesExtractor extractor;

    public SimpleProblem(int numObjects, int numFeatures, ToDoubleFunction<Dataset> errorFunction, List<Dataset> datasets, MetaFeaturesExtractor extractor) {
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
        return numFeatures * numObjects;
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
        data = RelationsGenerator.changeNumObjects(data, numObjects, dataset.numFeatures, random);
        data = RelationsGenerator.changeNumFeatures(data, dataset.numFeatures, numFeatures, random);

        for (int index = 0, i = 0; i < numObjects; i++) {
            for (int j = 0; j < numFeatures; j++) {
                solution.setVariableValue(index++, data[i][j]);
            }
        }

        return solution;
    }

    public Dataset build(DoubleSolution solution) {
        double[][] data = new double[numObjects][numFeatures];
        for (int index = 0, i = 0; i < numObjects; i++) {
            for (int j = 0; j < numFeatures; j++) {
                data[i][j] = solution.getVariableValue(index++);
            }
        }
        return new Dataset(data, extractor);
    }

    @Override
    public void evaluate(DoubleSolution solution) {
        try {
            solution.setObjective(0, errorFunction.applyAsDouble(build(solution)));
        } catch (Exception e) {
            if (e instanceof EndSearch) {
                throw e;
            }
            solution.setObjective(0, 100);
        }
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
