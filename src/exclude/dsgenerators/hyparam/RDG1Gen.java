package exclude.dsgenerators.hyparam;

import java.util.Random;

import org.uma.jmetal.solution.DoubleSolution;

import exclude.features_inversion.classification.dataset.BinDataset;
import weka.core.Instances;
import weka.datagenerators.classifiers.classification.RDG1;

public class RDG1Gen implements GeneratorBuilder {

    static int rep(int l, int m, int r) {
        return Math.max(l, Math.min(m, r));
    }

    final int a, p, n;

    Random random = new Random();

    public RDG1Gen(int a, int p, int n) {
        this.a = a;
        this.p = p;
        this.n = n;
    }

    @Override
    public Generator generate(int a, int p, int n, DoubleSolution solution) {
        double rNum = solution.getVariableValue(0);
        double rMax = solution.getVariableValue(1);
        double rMin = solution.getVariableValue(2);
        double rIrr = solution.getVariableValue(3);

        return new Generator() {

            @Override
            public BinDataset generate() throws Exception {

                int m = a + 1;

                RDG1 rdg = new RDG1();
                rdg.setNumExamples(2 * (p + n));
                rdg.setNumAttributes(m);

                rdg.setNumNumeric(rep(0, (int) Math.round(m * rNum), m));
                rdg.setMaxRuleSize(rep(1, (int) Math.round(m * rMax), m));

                int nMin = rep(1, (int) Math.round(m * rMin), m);
                int nRem = m - nMin;
                int nIrr = rep(0, (int) Math.round(nRem * rIrr), nRem);

                rdg.setMinRuleSize(nMin);
                rdg.setNumIrrelevant(nIrr);
                rdg.setRelationName("gen");

                rdg.setSeed(random.nextInt());

                rdg.defineDataFormat();
                Instances instances = rdg.generateExamples();
                instances.setClassIndex(a);

                return Generator.convert(a, p, n, instances, random);
            }
        };

    }

    @Override
    public Double getLowerBound(int index) {
        return 0.0;
    }

    @Override
    public Double getUpperBound(int index) {
        return 1.0;
    }

    @Override
    public int length() {
        return 4;
    }

    @Override
    public int repeat() {
        return 10;
    }

}
