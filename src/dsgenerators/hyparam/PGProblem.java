package dsgenerators.hyparam;

import java.util.List;
import java.util.Random;
import java.util.function.ToDoubleFunction;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;

import clusterization.Dataset;
import clusterization.MetaFeaturesExtractor;
import dsgenerators.EndSearch;
import dsgenerators.ErrorFunction;

public class PGProblem implements DoubleProblem {

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

    private static final long serialVersionUID = 1L;
    final ToDoubleFunction<Dataset> errorFunction;
    final List<Dataset> datasets;
    final Random random = new Random();
    final int numFeatures, numObjects;
    public final MetaFeaturesExtractor extractor;

    public PGProblem(int a, int p, int n, ErrorFunction error, GeneratorBuilder g) {
        this.error = error;
        this.a = a;
        this.p = p;
        this.n = n;
        this.g = g;
    }

    @Override
    public DoubleSolution createSolution() {
        DoubleSolution solution = new DefaultDoubleSolution(this);
        return solution;
    }

    @Override
    public void evaluate(DoubleSolution solution) {
        int length = error.length();

        double[] sum = new double[length];

        Generator generator = g.generate(a, p, n, solution);

        int rep = g.repeat();

        for (int r = 0; r < rep; r++) {
            try {
                BinDataset d = generator.generate();
                double[] vector = error.componentwise(d);
                for (int i = 0; i < length; i++) {
                    sum[i] += vector[i];
                }
            } catch (Exception e) {
                if (e instanceof EndSearch) {
                    throw new RuntimeException(e);
                }
                e.printStackTrace();
                System.err.println(e.getMessage());
                for (int i = 0; i < length; i++) {
                    sum[i] += 100;
                }
            }
        }
        for (int i = 0; i < length; i++) {
            solution.setObjective(i, sum[i] / rep);
        }
    }

    @Override
    public Double getLowerBound(int index) {
        return g.getLowerBound(index);
    }

    @Override
    public String getName() {
        return g.getClass().getSimpleName() + "Problem";
    }

    @Override
    public int getNumberOfConstraints() {
        return 0;
    }

    @Override
    public int getNumberOfObjectives() {
        return error.length();
    }

    @Override
    public int getNumberOfVariables() {
        return g.length();
    }

    @Override
    public Double getUpperBound(int index) {
        return g.getUpperBound(index);
    }

}
