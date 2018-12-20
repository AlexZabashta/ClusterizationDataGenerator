package exclude;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import com.ifmo.recommendersystem.metafeatures.MetaFeatureExtractor;

import exclude.features_inversion.classification.dataset.BinDataCross;
import exclude.features_inversion.classification.dataset.BinDataMutation;
import exclude.features_inversion.classification.dataset.BinDataset;
import exclude.features_inversion.classification.dataset.DAMetaFeatureExtractor;
import exclude.features_inversion.classification.dataset.MetaFeatureExtractorsCollection;
import exclude.features_inversion.classification.fun.AttributeFunction;
import exclude.features_inversion.classification.fun.RandomFunction;
import exclude.features_inversion.util.FeaturePoint;
import exclude.features_inversion.util.Point;
import misc.FolderUtils;
import optimization.Crossover;
import optimization.Mutation;
import optimization.gen.GeneticSearch;
import optimization.gen.ParallelRunnner;
import optimization.result.FunEvalLimit;
import optimization.result.Result;
import optimization.result.StoppingCriterion;
import weka.core.Instances;

public class SingleRunExp {

    public static double convert(int c, Point point, Point target, Point scale) {
        double v = (point.coordinate(c) - target.coordinate(c)) / (3 * scale.coordinate(c));
        return v + 0.5;
    }

    public static void printPoint(int px, int py, int w, int h, int rad, int color, BufferedImage image) {
        for (int dx = -rad; dx <= rad; dx++) {
            for (int dy = -rad; dy <= rad; dy++) {
                int ds = Math.abs(dx) + Math.abs(dy);
                if (ds <= rad) {
                    int x = px + dx;
                    int y = py + dy;
                    if (0 <= x && x < w && 0 <= y && y < h) {
                        image.setRGB(x, h - y - 1, color);
                    }

                }

            }
        }
    }

    public static void print(String path, List<? extends Point> points, Point target, Point scale) throws IOException {
        int w = 921;
        int h = 921;

        int pointColor = 0x000000, lineColor = 0x545454;

        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_BGR);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                image.setRGB(x, h - y - 1, 0xFFFFFF);
            }
        }

        int rad = 3;

        for (int r = 0; r < 30; r++) {
            int cx = (int) Math.round(0.5 * w);
            int cy = (int) Math.round(0.5 * h);

            printPoint(cx + r, cy + r, w, h, 4, lineColor, image);
            printPoint(cx - r, cy + r, w, h, 4, lineColor, image);
            printPoint(cx + r, cy - r, w, h, 4, lineColor, image);
            printPoint(cx - r, cy - r, w, h, 4, lineColor, image);

        }

        for (Point p : points) {
            if (p == null) {
                continue;
            }
            int px = (int) Math.round(convert(0, p, target, scale) * w);
            int py = (int) Math.round(convert(1, p, target, scale) * h);

            for (int dx = -rad; dx <= rad; dx++) {
                for (int dy = -rad; dy <= rad; dy++) {
                    int ds = Math.abs(dx) + Math.abs(dy);
                    if (ds <= rad) {
                        int x = px + dx;
                        int y = py + dy;
                        if (0 <= x && x < w && 0 <= y && y < h) {
                            image.setRGB(x, h - y - 1, pointColor);
                        }

                    }

                }
            }
        }

        // {
        // int y = (int) Math.floor(0.5 * h);
        // if (y >= h) {
        // y = h - 1;
        // }
        // if (y < 0) {
        // y = 0;
        // }
        // for (int x = 0; x < w; x++) {
        // image.setRGB(x, h - y - 1, lineColor);
        // }
        // }
        //
        // {
        // int x = (int) Math.floor(0.5 * w);
        // if (x >= w) {
        // x = w - 1;
        // }
        // if (x < 0) {
        // x = 0;
        // }
        // for (int y = 0; y < h; y++) {
        // image.setRGB(x, h - y - 1, lineColor);
        // }
        // }

        ImageIO.write(image, "png", new File(path));
    }

    static void printStd(int fid) {
        File dataFolder = new File("data" + File.separator + "carff");

        List<MetaFeatureExtractor> all = MetaFeatureExtractorsCollection.all();

        List<MetaFeatureExtractor> extractors = new ArrayList<>();
        extractors.add(all.get(fid));
        DAMetaFeatureExtractor extractor = new DAMetaFeatureExtractor(extractors);

        List<BinDataset> datasets = new ArrayList<BinDataset>();
        List<Point> points = new ArrayList<Point>();

        for (File arff : dataFolder.listFiles()) {
            try {
                Instances instances = new Instances(new FileReader(arff));
                instances.setClassIndex(instances.numAttributes() - 1);
                BinDataset dataset = BinDataset.fromInstances(instances);

                Point point = new Point(extractor.extract(dataset));

                datasets.add(dataset);
                points.add(point);

            } catch (Exception err) {
                // System.out.println(arff + " " + err.getMessage());
            }
        }
        int n = datasets.size();
        int m = extractor.numberOfFeatures();

        double[] sum1 = new double[m];
        double[] sum2 = new double[m];

        for (Point point : points) {
            for (int i = 0; i < m; i++) {
                double val = point.coordinate(i);
                sum1[i] += val;
                sum2[i] += val * val;
            }
        }

        double[] mean = new double[m], std = new double[m];

        for (int i = 0; i < m; i++) {
            mean[i] = sum1[i] / n;
            std[i] = Math.max(Math.sqrt(sum2[i] / n - mean[i] * mean[i]), 1e-9);
        }

        System.out.println(fid + " " + std[0]);
    }

    // final static Random random = new Random();

    static void printGS(int fx, int fy) throws IOException {
        File dataFolder = new File("data" + File.separator + "carff");

        List<MetaFeatureExtractor> all = MetaFeatureExtractorsCollection.all();

        List<MetaFeatureExtractor> extractors = new ArrayList<>();
        extractors.add(all.get(fx));
        extractors.add(all.get(fy));
        DAMetaFeatureExtractor extractor = new DAMetaFeatureExtractor(extractors);

        List<BinDataset> datasets = new ArrayList<BinDataset>();
        List<Point> points = new ArrayList<Point>();

        for (File arff : dataFolder.listFiles()) {
            try {
                Instances instances = new Instances(new FileReader(arff));
                instances.setClassIndex(instances.numAttributes() - 1);
                BinDataset dataset = BinDataset.fromInstances(instances);

                Point point = new Point(extractor.extract(dataset));

                datasets.add(dataset);
                points.add(point);

            } catch (Exception err) {
                System.out.println(arff + " " + err.getMessage());
            }
        }
        int n = datasets.size();
        int m = extractor.numberOfFeatures();

        double[] sum1 = new double[m];
        double[] sum2 = new double[m];

        for (Point point : points) {
            for (int i = 0; i < m; i++) {
                double val = point.coordinate(i);
                sum1[i] += val;
                sum2[i] += val * val;
            }
        }

        double[] mean = new double[m], std = new double[m];

        for (int i = 0; i < m; i++) {
            mean[i] = sum1[i] / n;
            std[i] = Math.max(Math.sqrt(sum2[i] / n - mean[i] * mean[i]), 1e-9);
        }

        Point target = new Point(mean);
        Point scale = new Point(std);

        List<FeaturePoint<BinDataset>> featurePoints = new ArrayList<FeaturePoint<BinDataset>>();

        for (int i = 0; i < n; i++) {
            try {
                featurePoints.add(new FeaturePoint<BinDataset>(target, scale, datasets.get(i), extractor));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        Collections.sort(featurePoints);

        List<FeaturePoint<BinDataset>> start = featurePoints.subList(0, (featurePoints.size() * 2) / 3);

        String path = FolderUtils.openOrCreate("init2", fx + "_" + fy);

        print(path + "init.png", featurePoints, target, scale);
        // print(path + "start.png", start, target, scale);

        int evalLimit = 6543;

        Crossover<FeaturePoint<BinDataset>> crossover = new BinDataCross(extractor);
        Mutation<FeaturePoint<BinDataset>> mutation = new BinDataMutation(extractor);

        GeneticSearch<FeaturePoint<BinDataset>> search = new GeneticSearch<FeaturePoint<BinDataset>>(crossover, mutation);

        StoppingCriterion<FeaturePoint<BinDataset>> stoppingCriterion = new FunEvalLimit<>(evalLimit);
        List<Result<FeaturePoint<BinDataset>>> results = new ArrayList<>();
        Result<FeaturePoint<BinDataset>> result = search.search(start, 0.5, 0.5, stoppingCriterion, results);
        // List<FeaturePoint<BinDataset>> finish = result.instances;

        int len = results.size();

        for (int i = 0; i < len; i++) {
            print(path + "step" + i + ".png", results.get(i).instances, target, scale);
        }

        List<FeaturePoint<BinDataset>> gen = new ArrayList<>();

        int threads = 16;
        List<Runnable> generators = new ArrayList<>();
        int genSize = evalLimit / threads;

        for (int t = 0; t < threads; t++) {
            generators.add(new Runnable() {
                @Override
                public void run() {
                    Random random = new Random();
                    for (int i = 0; i < genSize; i++) {
                        int attr = random.nextInt(200) + 1;
                        int posN = random.nextInt(200) + 1;
                        int negN = random.nextInt(200) + 1;

                        double[][] pos = new double[posN][attr];
                        double[][] neg = new double[negN][attr];

                        for (int j = 0; j < attr; j++) {
                            AttributeFunction fun = RandomFunction.generate(random, j, 4);
                            BinDataMutation.apply(fun, pos, j, true);
                            BinDataMutation.apply(fun, neg, j, false);
                        }

                        BinDataset genDataset = new BinDataset(pos, neg, attr);
                        try {
                            FeaturePoint<BinDataset> point = new FeaturePoint<BinDataset>(target, scale, genDataset, extractor);
                            synchronized (gen) {
                                gen.add(point);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            });
        }

        ParallelRunnner.run(generators);

        Result<FeaturePoint<BinDataset>> genData = new Result<>(evalLimit, 0, gen);
        print(path + "gen.png", gen, target, scale);
        try (PrintWriter log = new PrintWriter((path + "result.txt"))) {
            log.println(result.bestValue);
            log.println(genData.bestValue);
        }

        System.out.println(fx + " " + fy);
    }

    public static void main(String[] args) throws IOException {

        printGS(4, 24);

        // int l = 4, r = 34;
        //
        // for (int fy = l; fy < r; fy++) {
        // for (int fx = l; fx < fy; fx++) {
        // printGS(fx, fy);
        // }
        // }

    }
}
