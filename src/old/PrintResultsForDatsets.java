package old;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import dsgenerators.ListMetaFeatures;
import features_inversion.classification.dataset.BinDataset;
import misc.FolderUtils;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class PrintResultsForDatsets {
    // D:\eclipse\FeaturesInversion\result\dsgenerators.RunTwoDataset\1500049894797
    // static String src = "result\\dsgenerators.RunMultCritExp\\1498841833828";
    // static String src = "result\\dsgenerators.RunMultCritExp\\1499696328585";

    static String src = "result\\dsgenerators.RunTwoDataset\\1500123269522";
    // static final int[] mfIndices = { 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33 };
    // static final int n = mfIndices.length;

    static Set<String> alg = new TreeSet<>();
    static Set<String> prb = new TreeSet<>();
    static Set<String> aps = new TreeSet<>();
    static Set<String> rep = new TreeSet<>();

    static Map<String, Color> clr = new HashMap<>();

    static Set<String> dat = new TreeSet<>();
    static Map<String, Double> avg = new TreeMap<String, Double>();

    static File folder = new File(src);

    static void read() throws IOException {
        for (File file : folder.listFiles()) {
            String[] name = file.getName().split("_");

            if (name.length != 5) {
                continue;
            }

            alg.add(name[1]);
            prb.add(name[2]);
            rep.add(name[3]);
            dat.add(name[4]);
            aps.add(name[1] + "_" + name[2]);

        }

    }

    static void setColors() {
        Color[] colors = { Color.RED, Color.GREEN, Color.BLUE, Color.PINK, Color.CYAN, Color.MAGENTA, Color.BLACK };
        int i = 0;
        for (String name : prb) {
            Color c = colors[i++];
            clr.put(name, c);

            System.out.println(name + " " + c);

        }
    }

    static void printAP() throws IOException {

        int w = 2000, h = 400;

        int m = 1000;

        for (String d : dat) {

            Map<String, double[][]> plots = new HashMap<>();

            for (String p : prb) {

                String bestAlg = null;
                double bestVal = Double.POSITIVE_INFINITY;

                for (String a : alg) {

                    double sum = 0;
                    double cnt = 0;

                    for (String r : rep) {

                        File file = new File(folder + "\\1_" + a + "_" + p + "_" + r + "_" + d);

                        if (file.exists()) {
                            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                                double val = Double.parseDouble(reader.readLine().substring(2));
                                sum += val;
                                cnt += 1;

                            }
                        }
                    }

                    sum /= cnt;

                    if (sum < bestVal) {
                        bestVal = sum;
                        bestAlg = a;
                    }
                }

                System.out.printf(Locale.ENGLISH, "%s %s %s %s%n", d, p, Double.toString(bestVal), bestAlg);
            }

        }

    }

    public static void main(String[] args) throws IOException {
        read();
        setColors();
        printAP();

        // System.out.println(Math.log(131));

    }

}
