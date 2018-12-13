package dsgenerators.hyparam;

import java.util.Random;

import org.uma.jmetal.solution.DoubleSolution;

import features_inversion.classification.dataset.BinDataset;
import weka.core.Instances;
import weka.datagenerators.classifiers.classification.BayesNet;

public class BayesNetGen implements GeneratorBuilder {

    final int a, p, n;

    Random random = new Random();

    public BayesNetGen(int a, int p, int n) {
        this.a = a;
        this.p = p;
        this.n = n;
    }

    @Override
    public Generator generate(int a, int p, int n, DoubleSolution solution) {
        final double arcsD = solution.getVariableValue(0);

        return new Generator() {
            @Override
            public BinDataset generate() throws Exception {

                int arcs = (int) Math.round(arcsD + random.nextDouble() - 0.5);

                BayesNet bayesNet = new BayesNet();

                bayesNet.setRelationName("gen");
                bayesNet.setNumExamples(2 * (p + n));
                bayesNet.setNumAttributes(a + 1);
                bayesNet.setNumArcs(Math.max(a, Math.min(arcs, a * (a + 1) / 2)));
                bayesNet.setSeed(random.nextInt());
                Instances instances = bayesNet.generateExamples();
                instances.setClassIndex(0);
                return Generator.convert(a, p, n, instances, random);

            }
        };
    }

    @Override
    public Double getLowerBound(int index) {
        return (double) (a);
    }

    @Override
    public Double getUpperBound(int index) {
        return (double) Math.min(a * 3, a * (a + 1) / 2);
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
