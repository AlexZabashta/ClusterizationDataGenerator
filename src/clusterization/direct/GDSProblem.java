package clusterization.direct;

import java.util.List;
import java.util.Random;
import java.util.function.ToDoubleFunction;

import org.uma.jmetal.problem.Problem;

import clusterization.Dataset;
import clusterization.MetaFeaturesExtractor;
import clusterization.direct.fun.RandomFunction;

public class GDSProblem implements Problem<DataSetSolution> {

    private static final long serialVersionUID = 1L;
    final ToDoubleFunction<Dataset> errorFunction;
    final List<Dataset> datasets;
    final Random random = new Random();
    final int numFeatures, numObjects;
    public final MetaFeaturesExtractor extractor;

    public GDSProblem(int numObjects, int numFeatures, ToDoubleFunction<Dataset> errorFunction, List<Dataset> datasets, MetaFeaturesExtractor extractor) {
        this.numObjects = numObjects;
        this.numFeatures = numFeatures;
        this.errorFunction = errorFunction;
        this.datasets = datasets;
        this.extractor = extractor;
    }

    @Override
    public int getNumberOfVariables() {
        return 1;
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

    @Override
    public void evaluate(DataSetSolution solution) {
        Dataset dataset = solution.getVariableValue(0);
        solution.setObjective(0, errorFunction.applyAsDouble(dataset));
    }

    public Dataset fit(Dataset dataset) {
        double[][] data = dataset.data();
        data = RelationsGenerator.changeNumObjects(data, numObjects, dataset.numFeatures, random);
        data = RelationsGenerator.changeNumFeatures(data, dataset.numFeatures, numFeatures, random);
        return new Dataset(data, extractor);
    }

    @Override
    public DataSetSolution createSolution() {
        if (datasets == null || datasets.isEmpty()) {
            double[][] data = new double[numObjects][numFeatures];
            for (int j = 0; j < numFeatures; j++) {
                int d = random.nextInt(4) + 3;
                RelationsGenerator.apply(RandomFunction.generate(random, j, d), data, j);
            }
            return new DataSetSolution(new Dataset(data, extractor));

        } else {
            return new DataSetSolution((datasets.get(random.nextInt(datasets.size()))));
        }
    }
}
