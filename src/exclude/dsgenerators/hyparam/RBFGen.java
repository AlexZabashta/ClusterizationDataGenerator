package exclude.dsgenerators.hyparam;

import java.util.Random;

import org.uma.jmetal.solution.DoubleSolution;

import exclude.features_inversion.classification.dataset.BinDataset;
import weka.core.Instances;
import weka.datagenerators.classifiers.classification.RandomRBF;

public class RBFGen implements GeneratorBuilder {

    final int a, p, n;

    Random random = new Random();

    public RBFGen(int a, int p, int n) {
        this.a = a;
        this.p = p;
        this.n = n;
    }

    @Override
    public Generator generate(int a, int p, int n, DoubleSolution solution) {
        final double centrD = solution.getVariableValue(0);

        return new Generator() {

            @Override
            public BinDataset generate() throws Exception {

                int centr = (int) Math.round(centrD + random.nextDouble() - 0.5);

                RandomRBF rbf = new RandomRBF();

                rbf.setRelationName("gen");
                rbf.setNumExamples(2 * (p + n));
                rbf.setNumAttributes(a + 1);
                rbf.setNumCentroids(Math.max(1, centr));
                rbf.setSeed(random.nextInt());

                rbf.defineDataFormat();
                Instances instances = rbf.generateExamples();
                instances.setClassIndex(a);

                return Generator.convert(a, p, n, instances, random);

            }
        };
    }

    @Override
    public Double getLowerBound(int index) {
        return (double) (1);
    }

    @Override
    public Double getUpperBound(int index) {
        return (double) (32);
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public int repeat() {
        return 10;
    }

}
