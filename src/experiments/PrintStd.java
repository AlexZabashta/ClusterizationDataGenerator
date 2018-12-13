package experiments;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

import com.ifmo.recommendersystem.metafeatures.MetaFeatureExtractor;

import features_inversion.classification.dataset.BinDataCross;
import features_inversion.classification.dataset.BinDataMutation;
import features_inversion.classification.dataset.BinDataset;
import features_inversion.classification.dataset.MetaFeatureExtractorsCollection;
import features_inversion.classification.dataset.DAMetaFeatureExtractor;
import features_inversion.classification.fun.AttributeFunction;
import features_inversion.classification.fun.RandomFunction;
import features_inversion.util.FeaturePoint;
import features_inversion.util.Point;
import misc.FolderUtils;
import optimization.Crossover;
import optimization.Mutation;
import optimization.gen.GeneticSearch;
import optimization.gen.ParallelRunnner;
import optimization.result.FunEvalLimit;
import optimization.result.Result;
import optimization.result.StoppingCriterion;
import weka.core.Instances;

public class PrintStd {

    public static void main(String[] args) throws IOException {

        File dataFolder = new File("data" + File.separator + "carff");

        List<MetaFeatureExtractor> all = MetaFeatureExtractorsCollection.all();

        List<MetaFeatureExtractor> extractors = new ArrayList<>();

        for (int i = 4; i < 53; i++) {
            extractors.add(all.get(i));
        }

        DAMetaFeatureExtractor extractor = new DAMetaFeatureExtractor(extractors);

        List<Point> points = new ArrayList<Point>();

        int m = extractor.numberOfFeatures();

        Set<Double>[] values = new Set[m];
        for (int i = 0; i < m; i++) {
            values[i] = new HashSet<Double>(1024);
        }

        for (File arff : dataFolder.listFiles()) {
            try {
                Instances instances = new Instances(new FileReader(arff));
                instances.setClassIndex(instances.numAttributes() - 1);
                BinDataset dataset = BinDataset.fromInstances(instances);

                Point point = new Point(extractor.extract(dataset));

                for (int i = 0; i < m; i++) {
                    values[i].add(point.coordinate(i));
                }

                points.add(point);

            } catch (Exception err) {
                System.out.println(arff + " " + err.getMessage());
            }
        }

        int n = points.size();
        String path = FolderUtils.openOrCreate("test1");

        System.out.println("size = " + n);

        for (int j = 0; j < m; j++) {
            double[] vals = new double[n];
            double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;

            int w = n;
            int h = 512;

            for (int i = 0; i < n; i++) {
                double val = points.get(i).coordinate(j);
                min = Math.min(min, val);
                max = Math.max(max, val);

                vals[i] = val;
            }
            Arrays.sort(vals);

            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    image.setRGB(x, h - y - 1, 0xFFFFFF);
                }
            }

            for (int x = 0; x < n; x++) {
                int y = (int) Math.round((vals[x] - min) / (max - min) * (h - 1));
                if (0 <= x && x < w && 0 <= y && y < h) {
                    image.setRGB(x, h - y - 1, 0);
                }

            }

            ImageIO.write(image, "png", new File(path + j + ".png"));
        }
    }
}
