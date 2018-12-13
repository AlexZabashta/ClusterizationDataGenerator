package dsgenerators.hyparam;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;

import dsgenerators.EndSearch;
import dsgenerators.ErrorFunction;
import features_inversion.classification.dataset.BinDataset;

public class PGProblem implements DoubleProblem {

    final int a, p, n;
    final ErrorFunction error;

    final GeneratorBuilder g;

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
