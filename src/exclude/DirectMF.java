package exclude;

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
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import com.ifmo.recommendersystem.metafeatures.MetaFeatureExtractor;

import exclude.features_inversion.classification.dataset.BinDataCross;
import exclude.features_inversion.classification.dataset.BinDataMutation;
import exclude.features_inversion.classification.dataset.BinDataset;
import exclude.features_inversion.classification.dataset.DAMetaFeatureExtractor;
import exclude.features_inversion.classification.dataset.MetaFeatureExtractorsCollection;
import exclude.features_inversion.util.FeaturePoint;
import exclude.features_inversion.util.Point;
import misc.FolderUtils;
import optimization.Crossover;
import optimization.Mutation;
import optimization.gen.GeneticSearch;
import optimization.result.FunEvalLimit;
import optimization.result.Result;
import optimization.result.StoppingCriterion;
import weka.core.Instances;

public class DirectMF {

    public static void main(String[] args) throws IOException {

        File dataFolder = new File("data" + File.separator + "carff");

        List<MetaFeatureExtractor> all = MetaFeatureExtractorsCollection.all();

        int n = 34;
        boolean[] integer = new boolean[n];
        Arrays.fill(integer, true);

        Set<Double>[] values = new Set[n];
        for (int i = 0; i < n; i++) {
            values[i] = new HashSet<Double>(1024);
        }

        for (File arff : dataFolder.listFiles()) {
            try {
                Instances instances = new Instances(new FileReader(arff));
                instances.setClassIndex(instances.numAttributes() - 1);

                // BinDataset dataset = BinDataset.fromInstances(instances);
                //
                // for (int i = 0; i < n; i++) {
                // MetaFeatureExtractor mfe = all.get(i);
                // double value = mfe.extractValue(instances);
                // if (Double.isInfinite(value)) {
                // continue;
                // }
                // if (Double.isNaN(value)) {
                // continue;
                // }
                //
                // integer[i] &= Math.round(value) == value;
                // }

                BinDataset dataset = BinDataset.fromInstances(instances);
                // Instances winst = dataset.WEKAInstances();

                for (int i = 0; i < n; i++) {
                    MetaFeatureExtractor mfe = all.get(i);
                    double value = mfe.extractValue(dataset);

                    values[i].add(value);

                    // out.print(value + " ");
                    // System.out.print(value + " ");
                }
                // System.out.println();

                // for (MetaFeatureExtractor mfe : all) {
                // double value = mfe.extractValue(winst);
                // // out.print(value + " ");
                // System.out.print(value + " ");
                // }
                // System.out.println();
                // System.out.println();

                // BinDataset dataset = BinDataset.fromInstances(instances);
                //
                // Point point = new Point(extractor.extract(dataset));
                //
                // datasets.add(dataset);
                // points.add(point);

                System.err.println(arff );
            } catch (Exception err) {
                System.err.println(arff + " " + err.getMessage());
            }
        }

        for (int i = 0; i < n; i++) {
            System.out.println(i + " " + values[i].size());
        }

    }
}
