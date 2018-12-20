package exclude.old;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.ifmo.recommendersystem.metafeatures.MetaFeatureExtractor;

import exclude.features_inversion.classification.dataset.MetaFeatureExtractorsCollection;
import misc.FolderUtils;

public class AggregateResults {

    final static String q = File.separator;

    public static void main(String[] args) throws IOException {

        String src = "result" + q + "experiments.TestExp" + q + "init2" + q;
        String dst = FolderUtils.openOrCreate();
        List<MetaFeatureExtractor> all = MetaFeatureExtractorsCollection.all();
        int[] fid = { 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 23, 24, 25 };
        int n = fid.length;

        int[] xid = { 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 };
        int[] yid = { 23, 15, 24, 16, 25, 17 };

        for (int y : yid) {
            for (int x : xid) {
                try {
                    File subFolder = new File(src + x + "_" + y);

                    try (BufferedReader result = new BufferedReader(new FileReader(new File(subFolder + q + "result.txt")))) {
                        double a = -Double.parseDouble(result.readLine());
                        double b = -Double.parseDouble(result.readLine());

                        System.out.printf("%.3f%n", b);

                    }

                } catch (IOException exception) {
                    System.err.println(exception);
                }

            }
            System.out.println("----------------");
        }

        // for (int i = 0; i < n; i++) {
        // int x = fid[i];
        // for (int j = 0; j < i; j++) {
        // int y = fid[j];
        // try {
        // File subFolder = new File(src + x + "_" + y);
        // try (BufferedReader result = new BufferedReader(new FileReader(new File(subFolder + q + "result.txt")))) {
        // double a = -Double.parseDouble(result.readLine());
        // double b = -Double.parseDouble(result.readLine());
        // System.out.println(a + " " + b);
        // }
        // System.out.println(subFolder);
        // } catch (IOException exception) {
        // System.err.println(exception);
        // }
        // }
        // }

    }
}
