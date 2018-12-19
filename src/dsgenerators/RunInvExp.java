package dsgenerators;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.abyss.ABYSSBuilder;
import org.uma.jmetal.algorithm.multiobjective.gde3.GDE3Builder;
import org.uma.jmetal.algorithm.multiobjective.ibea.IBEABuilder;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder.Variant;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.omopso.OMOPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.paes.PAESBuilder;
import org.uma.jmetal.algorithm.multiobjective.pesa2.PESA2;
import org.uma.jmetal.algorithm.multiobjective.pesa2.PESA2Builder;
import org.uma.jmetal.algorithm.multiobjective.randomsearch.RandomSearchBuilder;
import org.uma.jmetal.algorithm.multiobjective.smsemoa.SMSEMOABuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.CovarianceMatrixAdaptationEvolutionStrategy;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import clusterization.direct.GDSProblem;
import clusterization.vect.SimpleProblem;
import dsgenerators.direct.BinDataSetSolution;
import dsgenerators.direct.Crossover;
import dsgenerators.direct.Mutation;
import dsgenerators.hyparam.BayesNetGen;
import dsgenerators.hyparam.GMMGen;
import dsgenerators.hyparam.RBFGen;
import dsgenerators.hyparam.RDG1Gen;
import features_inversion.classification.dataset.BinDataset;
import misc.FolderUtils;
import misc.Experiment;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class RunInvExp {

    public static void main(String[] args) throws Exception {
        final int[] mfIndices = { 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33 };
        final int n = mfIndices.length;

        String res = FolderUtils.buildPath(false, Long.toString(System.currentTimeMillis()));

        final double[] sum0 = new double[n];
        final double[] sum1 = new double[n];
        final double[] sum2 = new double[n];

        final List<BinDataset> datasets = new ArrayList<BinDataset>();
        final List<String> fileNames = new ArrayList<String>();

        for (File file : new File("data\\carff\\").listFiles()) {
            try (FileReader reader = new FileReader(file)) {
                Instances instances = new Instances(reader);
                instances.setClassIndex(instances.numAttributes() - 1);
                BinDataset dataset = BinDataset.fromInstances(instances);

                if (instances.numAttributes() > 300 || instances.numInstances() > 1000) {
                    System.err.println(file.getName() + " too big");
                    continue;
                }

                boolean nan = false;
                for (int i = 0; i < n; i++) {
                    double val = dataset.getMetaFeature(mfIndices[i]);
                    if (Double.isNaN(val) || Double.isInfinite(val)) {
                        nan = true;
                    } else {
                        sum0[i] += 1;
                        sum1[i] += val;
                        sum2[i] += val * val;
                    }
                }

                if (nan) {
                    System.err.println(file.getName() + " mf is NaN");
                } else {
                    datasets.add(dataset);
                    fileNames.add(file.getName());
                    System.err.println(file.getName() + " added");
                }

            } catch (Exception e) {
                System.err.println(file.getName() + " " + e.getMessage());
            }
        }

        System.out.println(Arrays.toString(sum0));
        System.out.println(Arrays.toString(sum1));
        System.out.println(Arrays.toString(sum2));

        final double[] weight = new double[n];
        final double[] target = new double[n];

        for (int i = 0; i < n; i++) {
            double mX1 = sum1[i] / sum0[i];
            double mX2 = sum2[i] / sum0[i];

            double mean = mX1;
            double var = mX2 - mX1 * mX1;
            double std = Math.sqrt(var);

            target[i] = mean;
            weight[i] = 1 / std;
        }

        List<Classifier> classifiers = new ArrayList<>();

        classifiers.add(new Bagging());
        classifiers.add(new IBk(3));
        classifiers.add(new J48());
        classifiers.add(new MultilayerPerceptron());
        classifiers.add(new NaiveBayes());
        classifiers.add(new RandomForest());
        classifiers.add(new SMO());

        System.out.println(Arrays.toString(weight));

        final int size = datasets.size();

        final int limit = 10204;

        ExecutorService threads = Executors.newFixedThreadPool(8);

        List<Experiment> exp = new ArrayList<>();

        BinDataset targetDataset = null;

        {

            final ErrorFunction sdist = new EuclideanDist(new double[] { target[0], target[1], target[5] }, new double[] { weight[0], weight[1], weight[5] }, new int[] { mfIndices[0], mfIndices[1], mfIndices[5] });
            double best = Double.POSITIVE_INFINITY;
            for (BinDataset dataset : datasets) {
                double dist = sdist.evaluate(dataset);
                if (dist < best) {
                    targetDataset = dataset;
                    best = dist;

                    System.out.println("dist  = " + dist);
                }
            }
        }

        try (PrintWriter writer = new PrintWriter(new File(res + "target.txt"))) {
            for (int i = 0; i < n; i++) {
                writer.print(target[i]);
                writer.print(' ');
            }
            writer.println();

            for (int i = 0; i < n; i++) {
                writer.print(weight[i]);
                writer.print(' ');
            }
            writer.println();

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Classifier classifier : classifiers) {

            final ErrorFunction function = new DistAndAcc(target, weight, mfIndices, classifier, datasets);

            final String fileName = function.toString() + ".arff";

            System.out.println(fileName + " build");

            for (Experiment probFunAlg : problems(targetDataset.numAttr, targetDataset.pos.length, targetDataset.neg.length, function, limit, datasets)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                try {
                    DoubleProblem problem = (DoubleProblem) prob;
                    Algorithm<?> algorithm = new GDE3Builder(problem).setMaxIterations(10000000).setPopulationSize(32).build();
                    probFunAlg.algorithm = algorithm;

                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }

            for (Experiment probFunAlg : problems(targetDataset.numAttr, targetDataset.pos.length, targetDataset.neg.length, function, limit, datasets)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                try {
                    DoubleProblem problem = (DoubleProblem) prob;
                    Algorithm<?> algorithm = new IBEABuilder(problem).setMaxEvaluations(10000000).setPopulationSize(32).build();
                    probFunAlg.algorithm = algorithm;

                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }

            for (Experiment probFunAlg : problems(targetDataset.numAttr, targetDataset.pos.length, targetDataset.neg.length, function, limit, datasets)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                try {
                    GDSProblem problem = (GDSProblem) prob;

                    Algorithm<?> algorithm = new MOCellBuilder<BinDataSetSolution>(problem, new Crossover(), new Mutation()).setMaxEvaluations(10000000).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);

                } catch (ClassCastException ifNotGDS) {
                }

                try {
                    DoubleProblem problem = (DoubleProblem) prob;
                    Algorithm<?> algorithm = new MOCellBuilder<DoubleSolution>(problem, new SBXCrossover(0.9, 20.0), new PolynomialMutation()).setMaxEvaluations(10000000).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }

            for (Experiment probFunAlg : problems(targetDataset.numAttr, targetDataset.pos.length, targetDataset.neg.length, function, limit, datasets)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                try {
                    DoubleProblem problem = (DoubleProblem) prob;
                    Algorithm<?> algorithm = new MOEADBuilder(problem, Variant.MOEAD).setMaxEvaluations(10000000).setPopulationSize(32).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }

            for (Experiment probFunAlg : problems(targetDataset.numAttr, targetDataset.pos.length, targetDataset.neg.length, function, limit, datasets)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                try {
                    GDSProblem problem = (GDSProblem) prob;

                    Algorithm<?> algorithm = new NSGAIIBuilder<BinDataSetSolution>(problem, new Crossover(), new Mutation()).setMaxIterations(10000000).setPopulationSize(32).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);

                } catch (ClassCastException ifNotGDS) {
                }

                try {
                    DoubleProblem problem = (DoubleProblem) prob;
                    Algorithm<?> algorithm = new NSGAIIBuilder<DoubleSolution>(problem, new SBXCrossover(0.9, 20.0), new PolynomialMutation()).setMaxIterations(10000000).setPopulationSize(32).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }

            for (Experiment probFunAlg : problems(targetDataset.numAttr, targetDataset.pos.length, targetDataset.neg.length, function, limit, datasets)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                try {
                    DoubleProblem problem = (DoubleProblem) prob;
                    Algorithm<?> algorithm = new OMOPSOBuilder(problem, new SequentialSolutionListEvaluator<>()).setMaxIterations(10000000).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }

            for (Experiment probFunAlg : problems(targetDataset.numAttr, targetDataset.pos.length, targetDataset.neg.length, function, limit, datasets)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                try {
                    GDSProblem problem = (GDSProblem) prob;

                    Algorithm<?> algorithm = new PAESBuilder<BinDataSetSolution>(problem).setMutationOperator(new Mutation()).setMaxEvaluations(10000000).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);

                } catch (ClassCastException ifNotGDS) {
                }

                try {
                    DoubleProblem problem = (DoubleProblem) prob;
                    Algorithm<?> algorithm = new PAESBuilder<DoubleSolution>(problem).setMutationOperator(new PolynomialMutation()).setMaxEvaluations(10000000).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }

            for (Experiment probFunAlg : problems(targetDataset.numAttr, targetDataset.pos.length, targetDataset.neg.length, function, limit, datasets)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                try {
                    GDSProblem problem = (GDSProblem) prob;

                    Algorithm<?> algorithm = new PESA2Builder<BinDataSetSolution>(problem, new Crossover(), new Mutation()).setMaxEvaluations(10000000).setPopulationSize(32).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);

                } catch (ClassCastException ifNotGDS) {
                }

                try {
                    DoubleProblem problem = (DoubleProblem) prob;
                    Algorithm<?> algorithm = new PESA2Builder<DoubleSolution>(problem, new SBXCrossover(0.9, 20.0), new PolynomialMutation()).setMaxEvaluations(10000000).setPopulationSize(32).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }

            for (Experiment probFunAlg : problems(targetDataset.numAttr, targetDataset.pos.length, targetDataset.neg.length, function, limit, datasets)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                try {
                    GDSProblem problem = (GDSProblem) prob;

                    Algorithm<?> algorithm = new RandomSearchBuilder<BinDataSetSolution>(problem).setMaxEvaluations(10000000).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);

                } catch (ClassCastException ifNotGDS) {
                }

                try {
                    DoubleProblem problem = (DoubleProblem) prob;
                    Algorithm<?> algorithm = new RandomSearchBuilder<DoubleSolution>(problem).setMaxEvaluations(10000000).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }
            for (Experiment probFunAlg : problems(targetDataset.numAttr, targetDataset.pos.length, targetDataset.neg.length, function, limit, datasets)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                try {
                    GDSProblem problem = (GDSProblem) prob;

                    Algorithm<?> algorithm = new SMSEMOABuilder<BinDataSetSolution>(problem, new Crossover(), new Mutation()).setMaxEvaluations(10000000).setPopulationSize(32).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);

                } catch (ClassCastException ifNotGDS) {
                }

                try {
                    DoubleProblem problem = (DoubleProblem) prob;
                    Algorithm<?> algorithm = new SMSEMOABuilder<DoubleSolution>(problem, new SBXCrossover(0.9, 20.0), new PolynomialMutation()).setMaxEvaluations(10000000).setPopulationSize(32).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }

            for (Experiment probFunAlg : problems(targetDataset.numAttr, targetDataset.pos.length, targetDataset.neg.length, function, limit, datasets)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                try {
                    GDSProblem problem = (GDSProblem) prob;

                    Algorithm<?> algorithm = new SPEA2Builder<BinDataSetSolution>(problem, new Crossover(), new Mutation()).setMaxIterations(10000000).setPopulationSize(32).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);

                } catch (ClassCastException ifNotGDS) {
                }

                try {
                    DoubleProblem problem = (DoubleProblem) prob;
                    Algorithm<?> algorithm = new SPEA2Builder<DoubleSolution>(problem, new SBXCrossover(0.9, 20.0), new PolynomialMutation()).setMaxIterations(10000000).setPopulationSize(32).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }

            for (Experiment probFunAlg : problems(targetDataset.numAttr, targetDataset.pos.length, targetDataset.neg.length, function, limit, datasets)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                if (prob instanceof SimpleProblem) {
                    continue;
                }

                try {
                    DoubleProblem problem = (DoubleProblem) prob;
                    Algorithm<?> algorithm = new CovarianceMatrixAdaptationEvolutionStrategy.Builder(problem).setMaxEvaluations(10000000).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }

        }

        System.out.println("EXP = " + exp.size());

        for (final Experiment probFunAlg : exp) {
            final Algorithm<?> algorithm = probFunAlg.algorithm;
            if (algorithm == null) {
                continue;
            }
            final Problem<?> problem = probFunAlg.problem;
            final String file = probFunAlg.name;
            final Limited fun = probFunAlg.function;

            String name = algorithm.getClass().getSimpleName() + "_" + problem.getName() + "_" + file;

            threads.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        algorithm.run();
                        algorithm.getResult();
                    } catch (RuntimeException exception) {
                        if (!(exception.getCause() instanceof EndSearch)) {
                            exception.printStackTrace();
                        }
                    }

                    BinDataset dataset = fun.dataset;
                    if (dataset != null) {
                        Instances instances = dataset.WEKAInstances();
                        synchronized (res) {
                            try (PrintWriter writer = new PrintWriter(new File(res + name))) {
                                writer.println("% " + fun.best);
                                writer.print("%");

                                for (int i = 0; i < n; i++) {
                                    writer.print(' ');
                                    writer.print(dataset.getMetaFeature(mfIndices[i]));
                                }

                                writer.print(" " + ((DistAndAcc) fun.function).test(dataset));
                                writer.println(" " + dataset.getMetaFeature(79) + " " + dataset.getMetaFeature(80));

                                writer.println(instances);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println(name + " " + fun.best);
                            System.out.flush();
                        }

                    }
                }

            });
        }

        threads.shutdown();
    }

    static List<Experiment> problems(int a, int p, int n, ErrorFunction ef, int limit, List<BinDataset> datasets) {
        final List<Experiment> problems = new ArrayList<Experiment>();

        {
            Limited lef = new Limited(ef, limit);
            Problem<?> problem = (new GDSProblem(a, p, n, lef, datasets));
            problems.add(new Experiment(problem, lef));
        }
        {
            Limited lef = new Limited(ef, limit);
            Problem<?> problem = (new GDSProblem(a, p, n, lef, null));
            problems.add(new Experiment(problem, lef));
        }
        {
            Limited lef = new Limited(ef, limit);
            Problem<?> problem = (new SimpleProblem(a, p, n, lef, datasets));
            problems.add(new Experiment(problem, lef));
        }
        {
            Limited lef = new Limited(ef, limit);
            Problem<?> problem = (new SimpleProblem(a, p, n, lef, null));
            problems.add(new Experiment(problem, lef));
        }
        {
            Limited lef = new Limited(ef, limit);
            Problem<?> problem = (new BayesNetGen(a, p, n, lef));
            problems.add(new Experiment(problem, lef));
        }
        {
            Limited lef = new Limited(ef, limit);
            Problem<?> problem = (new GMMGen(a, p, n, lef));
            problems.add(new Experiment(problem, lef));
        }
        {
            Limited lef = new Limited(ef, limit);
            Problem<?> problem = (new RBFGen(a, p, n, lef));
            problems.add(new Experiment(problem, lef));
        }
        {
            Limited lef = new Limited(ef, limit);
            Problem<?> problem = (new RDG1Gen(a, p, n, lef));
            problems.add(new Experiment(problem, lef));
        }

        return problems;
    }

}
