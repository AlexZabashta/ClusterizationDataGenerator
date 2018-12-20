package exclude.dsgenerators.hyparam;

import org.uma.jmetal.solution.DoubleSolution;

import exclude.features_inversion.classification.dataset.BinDataset;
import jMEF.MultivariateGaussian;
import jMEF.PVectorMatrix;

public class GMMGen implements GeneratorBuilder {

    final int a, p, n;

    double lowerBound = -100;
    double upperBound = +100;

    public GMMGen(int a, int p, int n) {
        this.a = a;
        this.p = p;
        this.n = n;
    }

    @Override
    public Generator generate(int a, int p, int n, DoubleSolution solution) {
        int index = 0;

        PVectorMatrix posVM, negVM;
        {
            posVM = new PVectorMatrix(a);
            double[] mu = posVM.v.array;
            for (int i = 0; i < a; i++) {
                mu[i] = solution.getVariableValue(index++);
            }

            double[][] sigma = posVM.M.array;
            for (int i = 0; i < a; i++) {
                for (int j = 0; j < a; j++) {
                    sigma[i][j] = solution.getVariableValue(index++);
                }
            }

            posVM.M = posVM.M.Multiply(posVM.M.Transpose());
        }
        {

            negVM = new PVectorMatrix(a);
            double[] mu = negVM.v.array;
            for (int i = 0; i < a; i++) {
                mu[i] = solution.getVariableValue(index++);
            }
            double[][] sigma = negVM.M.array;
            for (int i = 0; i < a; i++) {
                for (int j = 0; j < a; j++) {
                    sigma[i][j] = solution.getVariableValue(index++);
                }
            }

            negVM.M = negVM.M.Multiply(negVM.M.Transpose());
        }

        MultivariateGaussian gmm = new MultivariateGaussian();
        return new Generator() {
            @Override
            public BinDataset generate() throws Exception {
                double[][] pos = new double[p][];
                double[][] neg = new double[n][];

                for (int i = 0; i < p; i++) {
                    pos[i] = gmm.drawRandomPoint(posVM).array;
                }
                for (int i = 0; i < n; i++) {
                    neg[i] = gmm.drawRandomPoint(negVM).array;
                }
                return new BinDataset(pos, neg, a);
            }
        };
    }

    @Override
    public Double getLowerBound(int index) {
        return lowerBound;
    }

    @Override
    public Double getUpperBound(int index) {
        return upperBound;
    }

    @Override
    public int length() {
        return 2 * (a * a + a);
    }

    @Override
    public int repeat() {
        return 10;
    }

}
