package old;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.ProblemUtils;

import com.ifmo.recommendersystem.metafeatures.MetaFeatureExtractor;

import dsgenerators.EndSearch;
import dsgenerators.ErrorFunction;
import dsgenerators.EuclideanDist;
import dsgenerators.Limited;
import dsgenerators.ListMetaFeatures;
import dsgenerators.direct.BinDataSetSolution;
import dsgenerators.direct.Crossover;
import dsgenerators.direct.GDSProblem;
import dsgenerators.direct.Mutation;
import features_inversion.classification.dataset.BinDataset;
import features_inversion.classification.dataset.MetaFeatureExtractorsCollection;
import jMEF.MultivariateGaussian;
import jMEF.PMatrix;
import jMEF.PVector;
import jMEF.PVectorMatrix;
import misc.FolderUtils;
import weka.core.Attribute;
import weka.core.Instances;

public class DrawEllipse {

    static PVectorMatrix fit(int d, double[][] data) {
        PVectorMatrix vectorMatrix = new PVectorMatrix(d);

        double[] mean = vectorMatrix.v.array;

        for (double[] point : data) {
            for (int i = 0; i < d; i++) {
                mean[i] += point[i] / data.length;
            }
        }

        double[][] cov = vectorMatrix.M.array;

        for (double[] point : data) {
            for (int i = 0; i < d; i++) {
                for (int j = 0; j < d; j++) {
                    cov[i][j] += (point[i] - mean[i]) * (point[j] - mean[j]) / (data.length - 1);
                }
            }
        }

        return vectorMatrix;
    }

    static void test() throws IOException {

        Random random = new Random();

        int w = 800, h = 600;

        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                image.setRGB(x, y, -1);
            }
        }

        int d = 2;

        PVectorMatrix params = new PVectorMatrix(d);
        double[][] sigma = params.M.array;

        for (int i = 0; i < d; i++) {
            for (int j = 0; j < d; j++) {
                sigma[i][j] = random.nextGaussian();
            }
        }

        double[] mu = params.v.array;
        for (int i = 0; i < d; i++) {
            mu[i] = random.nextGaussian();
        }

        params.M = params.M.Multiply(params.M.Transpose());
        PMatrix matrix = params.M;

        MultivariateGaussian gmm = new MultivariateGaussian();

        // gmm.density(params.v, params);

        int n = 100;
        double scale = 25;

        double[][] data = new double[n][];
        for (int i = 0; i < n; i++) {
            data[i] = gmm.drawRandomPoint(params).array;
        }

        for (double[] point : data) {
            int x = (int) (point[0] * scale + w / 2.0);
            int y = (int) (point[1] * scale + h / 2.0);

            if (0 <= x && x < w && 0 <= y && y < h) {
                image.setRGB(x, y, Color.BLACK.getRGB());
            }
        }

        int m = 5000;

        for (int p = 0; p < m; p++) {
            PVector vector = new PVector(d);

            double[] rad = vector.array;

            for (int i = 0; i < d; i++) {
                rad[i] = random.nextGaussian();
            }

            double dist = 0;
            for (int i = 0; i < d; i++) {
                dist += rad[i] * rad[i];
            }

            double norm = Math.sqrt(dist);

            for (int i = 0; i < d; i++) {
                rad[i] /= norm;
            }

            double[] point = matrix.Cholesky().MultiplyVectorRight(vector).Plus(params.v).array;

            int x = (int) (point[0] * scale + w / 2.0);
            int y = (int) (point[1] * scale + h / 2.0);

            if (0 <= x && x < w && 0 <= y && y < h) {
                image.setRGB(x, y, Color.RED.getRGB());
            }
        }

        PVectorMatrix predict = fit(d, data);

        System.out.println(gmm.KLD(params, predict));
        // System.out.println(gmm.KLD(predict, params));
        for (int p = 0; p < m; p++) {
            PVector vector = new PVector(d);

            double[] rad = vector.array;

            for (int i = 0; i < d; i++) {
                rad[i] = random.nextGaussian();
            }

            double dist = 0;
            for (int i = 0; i < d; i++) {
                dist += rad[i] * rad[i];
            }

            double norm = Math.sqrt(dist);

            for (int i = 0; i < d; i++) {
                rad[i] /= norm;
            }

            double[] point = predict.M.Cholesky().MultiplyVectorRight(vector).Plus(predict.v).array;

            int x = (int) (point[0] * scale + w / 2.0);
            int y = (int) (point[1] * scale + h / 2.0);

            if (0 <= x && x < w && 0 <= y && y < h) {
                image.setRGB(x, y, Color.GREEN.getRGB());
            }
        }

        ImageIO.write(image, "png", new File("ellipse.png"));

    }

    // static final int[] mfIndices = { 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33 };
    static final int[] mfIndices = { 11, 23 };
    static final int n = mfIndices.length;

    static int limit = 32 * 10;

    static BinDataset generate(double[] target, double[] weight, List<BinDataset> datasets) {
        final ErrorFunction ef = new EuclideanDist(target, weight, mfIndices, false);
        Limited lef = new Limited(ef, limit);

        try {
            Problem<BinDataSetSolution> problem = new GDSProblem(-1, -1, -1, lef, datasets);
            Algorithm<List<BinDataSetSolution>> algorithm = new SPEA2Builder<BinDataSetSolution>(problem, new Crossover(), new Mutation()).setMaxIterations(10000000).setPopulationSize(32).build();
            algorithm.run();
        } catch (Exception e) {
        }

        return lef.dataset;
    }

    public static void main(String[] args) throws IOException {

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
        final double[] center = new double[n];

        for (int i = 0; i < n; i++) {
            double mX1 = sum1[i] / sum0[i];
            double mX2 = sum2[i] / sum0[i];

            double mean = mX1;
            double var = mX2 - mX1 * mX1;
            double std = Math.sqrt(var);

            weight[i] = 1 / std;
            center[i] = mean;
        }

        System.out.println(Arrays.toString(weight));
        System.out.println(ListMetaFeatures.size());

        int w = 200, h = 150;

        for (BinDataset dataset : datasets) {
            // System.out.printf("%7.3f %7.3f%n", dataset.getMetaFeature(79), dataset.getMetaFeature(80));
            // System.out.println(dataset.getMetaFeature(79) > dataset.getMetaFeature(80));
        }

        int len = ListMetaFeatures.size();

        // 11 23

        // ArrayList<Attribute> attributies;
        // Instances metaDataset = new Instances("meta", attributies, datasets.size());

        double[] centerA = new double[n];
        double[] centerB = new double[n];

        int cntA = 0, cntB = 0;

        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                image.setRGB(x, y, -1);
            }
        }

        for (BinDataset dataset : datasets) {
            // System.out.printf("%7.3f %7.3f%n", dataset.getMetaFeature(79), dataset.getMetaFeature(80));
            // System.out.println();

            boolean c = dataset.getMetaFeature(79) > dataset.getMetaFeature(80);

            double x0 = (dataset.getMetaFeature(mfIndices[0]) - center[0]) * weight[0];
            double x1 = (dataset.getMetaFeature(mfIndices[1]) - center[1]) * weight[1];

            if (c) {
                draw(x0, x1, w, h, image, Color.RED.getRGB());
                centerA[0] += dataset.getMetaFeature(mfIndices[0]);
                centerA[1] += dataset.getMetaFeature(mfIndices[1]);
                ++cntA;
            } else {
                draw(x0, x1, w, h, image, Color.GREEN.getRGB());
                centerB[0] += dataset.getMetaFeature(mfIndices[0]);
                centerB[1] += dataset.getMetaFeature(mfIndices[1]);
                ++cntB;
            }
        }
        ImageIO.write(image, "png", new File(res + "init.png"));

        centerA[0] /= cntA;
        centerA[1] /= cntA;

        centerB[0] /= cntB;
        centerB[1] /= cntB;

        double[] mid = new double[n];

        mid[0] = (centerA[0] + centerB[0]) / 2;
        mid[1] = (centerA[1] + centerB[1]) / 2;

        for (int rep = 0; rep < 200; rep++) {
            BinDataset dataset = generate(mid, weight, datasets);
            if (dataset != null) {
                datasets.add(dataset);
            } else {
                System.out.print("null ");
            }
            System.out.println(rep);
            System.out.flush();
        }

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                image.setRGB(x, y, -1);
            }
        }
        for (BinDataset dataset : datasets) {
            // System.out.printf("%7.3f %7.3f%n", dataset.getMetaFeature(79), dataset.getMetaFeature(80));
            // System.out.println();

            boolean c = dataset.getMetaFeature(79) > dataset.getMetaFeature(80);

            double x0 = (dataset.getMetaFeature(mfIndices[0]) - center[0]) * weight[0];
            double x1 = (dataset.getMetaFeature(mfIndices[1]) - center[1]) * weight[1];

            if (c) {
                draw(x0, x1, w, h, image, Color.RED.getRGB());
            } else {
                draw(x0, x1, w, h, image, Color.GREEN.getRGB());
            }
        }
        ImageIO.write(image, "png", new File(res + "last.png"));

        // 79 80
    }

    static void draw(double x, double y, int w, int h, BufferedImage image, int rgb) {
        double scale = Math.hypot(w, h) / 20;

        int ix = (int) (x * scale + w / 2.0);
        int iy = (int) (y * scale + h / 2.0);

        if (0 <= ix && ix < w && 0 <= iy && iy < h) {
            image.setRGB(ix, iy, rgb);
        }

    }
}
