package exclude.temp;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.CovarianceMatrixAdaptationEvolutionStrategy;
import org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.CovarianceMatrixAdaptationEvolutionStrategy.Builder;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.runner.singleobjective.CovarianceMatrixAdaptationEvolutionStrategyRunner;
import org.uma.jmetal.solution.DoubleSolution;

import com.ifmo.recommendersystem.utils.StatisticalUtils;

import exclude.features_inversion.classification.dataset.mf.MetaFeatures;
import exclude.temp.aggr.Kurt;
import exclude.temp.aggr.Skewn;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.datagenerators.classifiers.classification.Agrawal;
import weka.datagenerators.classifiers.classification.BayesNet;
import weka.datagenerators.classifiers.classification.LED24;
import weka.datagenerators.classifiers.classification.RDG1;
import weka.datagenerators.classifiers.classification.RandomRBF;

public class Test {

    public static void main(String[] args) throws Exception {
        wekaGen();
    }

    static void abstrTest() {

        AbstractDoubleProblem abstractDoubleProblem = new AbstractDoubleProblem() {

            @Override
            public void evaluate(DoubleSolution solution) {
                solution.setObjective(0, 10);
                solution.getObjective(10);
            }
        };

        DoubleProblem problem = new DoubleProblem() {

            @Override
            public int getNumberOfVariables() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public int getNumberOfObjectives() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public int getNumberOfConstraints() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public String getName() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void evaluate(DoubleSolution solution) {
                // TODO Auto-generated method stub

            }

            @Override
            public DoubleSolution createSolution() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Double getUpperBound(int index) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Double getLowerBound(int index) {
                // TODO Auto-generated method stub
                return null;
            }
        };

        Builder cmaes = new CovarianceMatrixAdaptationEvolutionStrategy.Builder(problem);

    }

    static void testAllParam() {

        int n = 20;

        // g5.setNumClasses(34);

        for (int m = 5; m < 13; m++) {
            for (int q = 0; q <= m; q++) {

                for (int l = 1; l <= 16; l++) {
                    for (int r = 1; r <= 16; r++) {

                        for (int i = 0; i <= m; i++) {
                            if (i + l > m) {
                                break;
                            }

                            try {
                                RDG1 g5 = new RDG1();
                                g5.setNumExamples(n);
                                g5.setNumAttributes(m);

                                g5.setNumNumeric(q);

                                g5.setMaxRuleSize(r); // R
                                g5.setMinRuleSize(l); // L
                                g5.setNumIrrelevant(i); // I

                                // g5.setVoteFlag(false);

                                g5.defineDataFormat();
                                g5.generateExamples();
                            } catch (Exception e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                    }
                }

            }
        }

    }

    static void wekaGen() throws Exception {

        Agrawal g1 = new Agrawal();
        BayesNet g2 = new BayesNet();
        LED24 g3 = new LED24();
        RandomRBF g4 = new RandomRBF();
        RDG1 g5 = new RDG1();

        int n = 20, m = 23;
        ArrayList<Attribute> attributes = new ArrayList<>(m + 1);
        for (int i = 0; i < m; i++) {
            attributes.add(new Attribute("atr" + i));
        }
        attributes.add(new Attribute("class", Arrays.asList("n", "p")));
        Instances format = new Instances("gen", attributes, 0);
        format.setClassIndex(m);

        Random random = new Random();

        // g2.setDatasetFormat(format);
        g2.setRelationName("gen");
        g2.setNumExamples(n);
        g2.setNumAttributes(m + 1);
        g2.setNumArcs(m * (m + 1) / 2); // m <= m *(m-1)/2
        g2.setCardinality(1);
        g2.setSeed(random.nextInt());

        // g4.setDatasetFormat(format);
        g4.setNumClasses(2);
        g4.setNumExamples(n);
        g4.setNumAttributes(m + 1);
        g4.setNumCentroids(3);
        g4.setSeed(random.nextInt());
        g4.defineDataFormat();

        // g5.setDatasetFormat(format);
        g5.setNumExamples(n);
        g5.setNumAttributes(m);

        // g5.setNumClasses(34);

        g5.setNumNumeric(m - 3); // N
        g5.setMaxRuleSize(25); // R
        g5.setMinRuleSize(14); // L
        g5.setNumIrrelevant(9); // I
        g5.setSeed(random.nextInt());
        // g5.setVoteFlag(false);

        g5.defineDataFormat();

        int x = 0, y = 0;
        Instances instances = g5.generateExamples();
        instances.setClassIndex(m);
        // instances.setClassIndex(m);

        for (Instance instance : instances) {
            if (instance.classValue() < 0.5) {
                ++x;
            } else {
                ++y;
            }
        }

        System.out.println(instances);
        System.out.println(instances.classIndex());
        System.out.println(x + " " + y);

    }

    static void testStat() {
        Skewn skewn = new Skewn();
        Kurt kurt = new Kurt();

        Random random = new Random();

        int n = 4;

        double[] array = new double[n];

        for (int i = 0; i < n; i++) {
            array[i] = random.nextGaussian() * (i + 1);

            System.out.printf(Locale.ENGLISH, "%f%n", array[i]);
        }
        System.out.println();

        double[] values = array.clone();
        double mean = StatisticalUtils.mean(values);
        double variance = StatisticalUtils.variance(values, mean);

        System.out.println(StatisticalUtils.centralMoment(values, 4, mean) / Math.pow(variance, 2));
        System.out.println(kurt.aggregate(array));

        System.out.println();
        System.out.println(StatisticalUtils.centralMoment(values, 3, mean) / Math.pow(variance, 1.5));
        System.out.println(skewn.aggregate(array));

    }

}
