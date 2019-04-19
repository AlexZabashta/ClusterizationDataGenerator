package experiments;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.ToDoubleFunction;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.problem.Problem;

import clusterization.CMFExtractor;
import clusterization.Dataset;
import clusterization.MetaFeaturesExtractor;
import clusterization.direct.Crossover;
import clusterization.direct.DataSetSolution;
import clusterization.direct.GDSProblem;
import clusterization.direct.Mutation;
import net.sourceforge.jdistlib.disttest.DistributionTest;
import utils.ArrayUtils;
import utils.EndSearch;
import utils.FolderUtils;
import utils.Limited;
import utils.MahalanobisDistance;
import utils.MatrixUtils;
import utils.RandomUtils;
import utils.StatUtils;
import weka.core.Instances;

public class GenDip {

    public static void main(String[] args) {

        final int limit = 3000;

        MetaFeaturesExtractor extractor = new CMFExtractor();

        int numMF = extractor.lenght();
        int numData = 0;

        double[][] metaData = new double[512][];
        List<Dataset> datasets = new ArrayList<>();

        Map<Dataset, String> fileNames = new HashMap<>();

        for (File file : new File("pdata").listFiles()) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
                int n = objectInputStream.readInt();
                int m = objectInputStream.readInt();
                double[][] data = (double[][]) objectInputStream.readObject();

                Dataset dataset = new Dataset(data, extractor);
                double[] mf = dataset.metaFeatures();

                if (mf != null && mf.length == numMF) {
                    metaData[numData++] = mf;
                    datasets.add(dataset);
                    fileNames.put(dataset, file.getName());
                }

                System.out.println(file.getName() + " " + n + " " + m);
                System.out.flush();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        double[] mean = StatUtils.mean(numData, numMF, metaData);
        ArrayUtils.print(mean);

        double[][] cov = StatUtils.covarianceMatrix(numData, numMF, metaData);
        ArrayUtils.print(cov);
        double[][] invCov = MatrixUtils.inv(numMF, cov);
        ArrayUtils.print(invCov);

        MahalanobisDistance distance = new MahalanobisDistance(numMF, invCov);

        String res = FolderUtils.buildPath(false, Long.toString(System.currentTimeMillis()));

        ToDoubleFunction<Dataset> dipTest = new ToDoubleFunction<Dataset>() {

            @Override
            public double applyAsDouble(Dataset dataset) {
                int n = dataset.numObjects;
                if (n < 10) {
                    return Double.NaN;
                }

                int m = dataset.numFeatures;

                double[][] data = dataset.data();
                double[] dists = new double[n * (n - 1) / 2];

                for (int p = 0, i = 0; i < n; i++) {
                    for (int j = 0; j < i; j++, p++) {
                        double dist = 0;

                        for (int f = 0; f < m; f++) {
                            double diff = data[i][f] - data[j][f];
                            dist += diff * diff;
                        }

                        dists[p] = Math.sqrt(dist);
                    }
                }

                Arrays.sort(dists);
                double[] dip = DistributionTest.diptest_presorted(dists);
                return dip[1];
            }
        };

        for (int tid = 2; tid <= 10; tid += 2) {
            int numFeatures = tid;
            new Thread() {
                @Override
                public void run() {

                    Random random = new Random(numFeatures + 42);

                    for (int targetID = 0; targetID < 10000; targetID++) {
                        double targetDip = random.nextDouble();
                        ToDoubleFunction<Dataset> ef = new ToDoubleFunction<Dataset>() {
                            @Override
                            public double applyAsDouble(Dataset dataset) {
                                double dip = dipTest.applyAsDouble(dataset);
                                if (!Double.isFinite(dip)) {
                                    return 322;
                                }

                                final double dist = distance.distance(mean, extractor.extract(dataset));

                                double error;

                                if (dist < 5) {
                                    error = Math.exp(dist - 5);
                                } else {
                                    error = dist * 0.2;
                                }

                                return (error + 2) * Math.abs(dip - targetDip);
                            }
                        };
                        Limited lef = new Limited(ef, limit);
                        int numObjects = RandomUtils.randomFromSegment(random, 100, 300);
                        Problem<DataSetSolution> problem = (new GDSProblem(numObjects, numFeatures, lef, datasets, extractor));
                        Algorithm<?> algorithm = new NSGAIIBuilder<DataSetSolution>(problem, new Crossover(extractor), new Mutation(numObjects, numFeatures, extractor)).setMaxEvaluations(10000000).setPopulationSize(32).build();

                        try {
                            algorithm.run();
                        } catch (EndSearch e) {

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Dataset dataset = lef.dataset;
                        if (dataset == null) {
                            continue;
                        }

                        double dip = dipTest.applyAsDouble(dataset);
                        if (!Double.isFinite(dip)) {
                            continue;
                        }
                        Instances instances = dataset.toInstances();
                        synchronized (res) {
                            try (PrintWriter writer = new PrintWriter(new File(res + numFeatures + "_" + targetID + ".arff"))) {
                                writer.println("% " + dip);
                                writer.print("%");

                                double[] mf = dataset.metaFeatures();

                                for (int i = 0; i < numMF; i++) {
                                    writer.print(' ');
                                    writer.print(mf[i]);
                                }

                                writer.println();

                                writer.println(instances);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println(numFeatures + "  " + targetID + "  " + targetDip + " " + dip);
                            System.out.flush();
                        }

                    }

                };
            }.start();
        }

    }

}
