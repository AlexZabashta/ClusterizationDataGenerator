package dsgenerators;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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

import dsgenerators.direct.BinDataSetSolution;
import dsgenerators.direct.Crossover;
import dsgenerators.direct.GDSProblem;
import dsgenerators.direct.Mutation;
import dsgenerators.hyparam.BayesNetGen;
import dsgenerators.hyparam.GMMGen;
import dsgenerators.hyparam.PGProblem;
import dsgenerators.hyparam.RBFGen;
import dsgenerators.hyparam.RDG1Gen;
import dsgenerators.vect.SimpleProblem;
import features_inversion.classification.dataset.BinDataset;
import misc.FolderUtils;
import misc.Experiment;
import weka.core.Instances;

public class RunTwoDataset {

    public static void main(String[] args) {
        final int[] mfIndices = { 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33 };
        final int n = mfIndices.length;

        String res = FolderUtils.buildPath(false, Long.toString(System.currentTimeMillis()));

        final double[] sum0 = new double[n];
        final double[] sum1 = new double[n];
        final double[] sum2 = new double[n];

        final List<BinDataset> datasets = new ArrayList<BinDataset>();
        final List<String> fileNames = new ArrayList<String>();

        for (File file : new File("data\\bin_all\\").listFiles()) {
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

        for (int i = 0; i < n; i++) {
            double mX1 = sum1[i] / sum0[i];
            double mX2 = sum2[i] / sum0[i];

            double mean = mX1;
            double var = mX2 - mX1 * mX1;
            double std = Math.sqrt(var);

            weight[i] = 1 / std;
        }

        System.out.println(Arrays.toString(weight));

        final int size = datasets.size();

        final int limit = 100000;

        ExecutorService threads = Executors.newFixedThreadPool(8);

        List<Experiment> exp = new ArrayList<>();

        for (int rep = 0; rep < 10; rep++) {

            for (int targetIndex = 0; targetIndex < size; targetIndex++) {
                String fileName = fileNames.get(targetIndex).replace('_', '-');

                // if (fileName.equals("australian.arff") || fileName.equals("1473.arff") || fileName.equals("56.arff") || fileName.equals("monks-problems-2-test.arff") || fileName.equals("820.arff")
                // || fileName.equals("927.arff")) {
                if (fileName.equals("australian.arff") || fileName.equals("1473.arff") || fileName.equals("56.arff")) {

                    fileName = rep + "_" + fileName;

                    BinDataset targetDataset = datasets.get(targetIndex);
                    final double[] target = new double[n];
                    for (int i = 0; i < n; i++) {
                        target[i] = targetDataset.getMetaFeature(mfIndices[i]);
                    }

                    final ErrorFunction ef = new EuclideanDist(target, weight, mfIndices, false);

                    List<BinDataset> adatasets = new ArrayList<>(datasets);

                    Collections.sort(adatasets, new Comparator<BinDataset>() {
                        @Override
                        public int compare(BinDataset x, BinDataset y) {
                            try {
                                return Double.compare(ef.componentwise(y)[0], ef.componentwise(x)[0]);
                            } catch (EndSearch e) {
                                return 0;
                            }
                        }
                    });

                    adatasets = adatasets.subList(0, adatasets.size() - 20);

                    // adatasets = null;

                    int numAttr = targetDataset.numAttr;
                    int numPos = targetDataset.pos.length;
                    int numNeg = targetDataset.neg.length;

                    {
                        // Limited lef = new Limited(ef, limit);
                        // DoubleProblem problem = new PGProblem(numAttr, numPos, numNeg, lef, new GMMGen(numAttr, numPos, numNeg));
                        // Experiment e = new Experiment(problem, lef, fileName);
                        // e.algorithm = new CovarianceMatrixAdaptationEvolutionStrategy.Builder(problem).setMaxEvaluations(10000000).build();
                        // exp.add(e);
                    }

                    {
                        Limited lef = new Limited(ef, limit);
                        DoubleProblem problem = new SimpleProblem(numAttr, numPos, numNeg, lef, adatasets);
                        Experiment e = new Experiment(problem, lef, fileName);
                        e.algorithm = new MOCellBuilder<DoubleSolution>(problem, new SBXCrossover(0.9, 20.0), new PolynomialMutation()).setMaxEvaluations(10000000).build();
                        exp.add(e);
                    }

                    {
                        Limited lef = new Limited(ef, limit);
                        DoubleProblem problem = new SimpleProblem(numAttr, numPos, numNeg, lef, adatasets);
                        Experiment e = new Experiment(problem, lef, fileName);
                        e.algorithm = new NSGAIIBuilder<DoubleSolution>(problem, new SBXCrossover(0.9, 20.0), new PolynomialMutation()).setMaxIterations(10000000).build();
                        exp.add(e);
                    }
                    {
                        Limited lef = new Limited(ef, limit);
                        DoubleProblem problem = new SimpleProblem(numAttr, numPos, numNeg, lef, adatasets);
                        Experiment e = new Experiment(problem, lef, fileName);
                        e.algorithm = new SMSEMOABuilder<DoubleSolution>(problem, new SBXCrossover(0.9, 20.0), new PolynomialMutation()).setMaxEvaluations(10000000).build();
                        exp.add(e);
                    }

                    {
                        Limited lef = new Limited(ef, limit);
                        GDSProblem problem = new GDSProblem(numAttr, numPos, numNeg, lef, adatasets);
                        Experiment e = new Experiment(problem, lef, fileName);
                        e.algorithm = new SPEA2Builder<BinDataSetSolution>(problem, new Crossover(), new Mutation()).setMaxIterations(10000000).build();
                        exp.add(e);
                    }
                    {
                        Limited lef = new Limited(ef, limit);
                        GDSProblem problem = new GDSProblem(numAttr, numPos, numNeg, lef, adatasets);
                        Experiment e = new Experiment(problem, lef, fileName);
                        e.algorithm = new NSGAIIBuilder<BinDataSetSolution>(problem, new Crossover(), new Mutation()).setMaxIterations(10000000).build();
                        exp.add(e);
                    }
                    {
                        Limited lef = new Limited(ef, limit);
                        GDSProblem problem = new GDSProblem(numAttr, numPos, numNeg, lef, adatasets);
                        Experiment e = new Experiment(problem, lef, fileName);
                        e.algorithm = new MOCellBuilder<BinDataSetSolution>(problem, new Crossover(), new Mutation()).setMaxEvaluations(10000000).build();
                        exp.add(e);
                    }
                }

                // exp.addAll(ListExperiments.experiments(targetDataset.numAttr, targetDataset.pos.length, targetDataset.neg.length, ef, limit, fileName, adatasets));
                // exp.addAll(ListExperiments.experiments(targetDataset.numAttr, targetDataset.pos.length, targetDataset.neg.length, efMo, limit, fileName, adatasets));
            }
        }
        System.out.println("EXP = " + exp.size());

        Collections.shuffle(exp);

        for (final Experiment experiment : exp) {
            final Algorithm<?> algorithm = experiment.algorithm;
            if (algorithm == null) {
                continue;
            }
            final Problem<?> problem = experiment.problem;

            final String file = experiment.name;
            final Limited fun = experiment.function;

            String name = algorithm.getClass().getSimpleName() + "_" + problem.getName() + "_" + file;

            threads.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        algorithm.run();
                        algorithm.getResult();
                    } catch (RuntimeException exception) {
                        if (exception.getCause() instanceof EndSearch) {
                            BinDataset dataset = fun.dataset;

                            if (dataset != null && fun.qid == fun.log.length) {

                                Instances instances = dataset.WEKAInstances();
                                synchronized (res) {
                                    try (PrintWriter writer = new PrintWriter(new File(res + fun.length() + "_" + name))) {
                                        writer.println("% " + fun.best);
                                        writer.print("%");

                                        for (int i = 0; i < n; i++) {
                                            writer.print(' ');
                                            writer.print(dataset.getMetaFeature(mfIndices[i]));
                                        }

                                        writer.println();

                                        writer.print("%");
                                        for (double val : fun.log) {
                                            writer.print(' ');
                                            writer.print(val);
                                        }
                                        writer.println();

                                        writer.println(instances);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    System.out.println(name + " " + fun.best);
                                    System.out.flush();
                                }

                            }
                        } else {
                            exception.printStackTrace();
                        }
                    }
                }

            });
        }

        threads.shutdown();
    }

}
