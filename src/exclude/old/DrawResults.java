package exclude.old;
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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import exclude.dsgenerators.ListMetaFeatures;
import exclude.features_inversion.classification.dataset.BinDataset;
import misc.FolderUtils;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class DrawResults {
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

                double[][] bestPlot = null;
                double bestVal = Double.POSITIVE_INFINITY;

                for (String a : alg) {

                    double sum = 0;
                    double cnt = 0;
                    double[][] plot = new double[3][m];

                    Arrays.fill(plot[1], Double.POSITIVE_INFINITY);
                    Arrays.fill(plot[2], Double.NEGATIVE_INFINITY);

                    for (String r : rep) {

                        File file = new File(folder + "\\1_" + a + "_" + p + "_" + r + "_" + d);

                        if (file.exists()) {
                            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                                double val = Double.parseDouble(reader.readLine().substring(2));
                                sum += val;
                                cnt += 1;

                                reader.readLine();

                                String[] f = reader.readLine().substring(2).split(" ");
                                double prefix = Double.NEGATIVE_INFINITY;

                                for (int i = 0; i < m; i++) {
                                    double v = Double.parseDouble(f[i]);
                                    if (Double.isFinite(v)) {
                                        prefix = Math.max(prefix, Math.log10(v));
                                    }
                                }

                                for (int i = 0; i < m; i++) {
                                    double v = Double.parseDouble(f[i]);
                                    if (Double.isFinite(v)) {
                                        prefix = Math.min(prefix, Math.log10(v));
                                    }

                                    plot[0][i] += prefix;
                                    plot[1][i] = Math.min(plot[1][i], prefix);
                                    plot[2][i] = Math.max(plot[2][i], prefix);
                                }
                            }
                        }
                    }

                    sum /= cnt;

                    if (sum < bestVal) {
                        bestVal = sum;

                        for (int i = 0; i < m; i++) {
                            plot[0][i] /= cnt;
                        }

                        bestPlot = plot;
                    }
                }
                if (bestPlot != null) {
                    plots.put(p, bestPlot);
                }
            }

            double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
            double minX = Math.log10(1), maxX = Math.log10(m);

            for (double[][] plot : plots.values()) {
                for (double val : plot[1]) {
                    minY = Math.min(minY, Math.floor(val));
                    maxY = Math.max(maxY, Math.ceil(val));
                }

                for (double val : plot[2]) {
                    minY = Math.min(minY, Math.floor(val));
                    maxY = Math.max(maxY, Math.ceil(val));
                }
            }

            System.out.println(minY + " " + maxY);

            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    image.setRGB(x, y, Color.WHITE.getRGB());
                }
            }

            for (String p : prb) {
                double[][] plot = plots.get(p);
                if (plot == null) {
                    continue;
                }

                Color c = clr.get(p);

                int rgb = clr.get(p).getRGB();

                Graphics2D graphics = (Graphics2D) image.getGraphics();

                graphics.setColor(new Color(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 0.3f));
                graphics.setStroke(new BasicStroke(1));

                for (int i = 1; i < m; i++) {
                    int x1 = (int) Math.floor(w * (Math.log10(i + 0) - minX) / (maxX - minX));
                    int x2 = (int) Math.floor(w * (Math.log10(i + 1) - minX) / (maxX - minX));

                    double b1 = plot[1][i - 1];
                    double b2 = plot[1][i - 0];

                    double t1 = plot[2][i - 1];
                    double t2 = plot[2][i - 0];

                    for (int x = x1; x < x2; x++) {
                        double yb = (x - x1) * (b2 - b1) / (x2 - x1) + b1;
                        int y1 = (int) Math.floor(h * (yb - minY) / (maxY - minY));

                        double yt = (x - x1) * (t2 - t1) / (x2 - x1) + t1;
                        int y2 = (int) Math.floor(h * (yt - minY) / (maxY - minY));

                        graphics.drawLine(x, h - y1 - 1, x, h - y2 - 1);
                    }
                }

                graphics.setColor(new Color(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 1.0f));
                graphics.setStroke(new BasicStroke(3));

                for (int i = 1; i < m; i++) {
                    int x1 = (int) Math.floor(w * (Math.log10(i + 0) - minX) / (maxX - minX));
                    int x2 = (int) Math.floor(w * (Math.log10(i + 1) - minX) / (maxX - minX));

                    int y1 = (int) Math.floor(h * (plot[0][i - 1] - minY) / (maxY - minY));
                    int y2 = (int) Math.floor(h * (plot[0][i - 0] - minY) / (maxY - minY));

                    graphics.drawLine(x1, h - y1 - 1, x2, h - y2 - 1);
                }

            }

            ImageIO.write(image, "png", new File(d + "_" + ((int) minY) + "_" + ((int) maxY) + ".png"));

        }

    }

    public static void main(String[] args) throws IOException {
        read();
        setColors();
        printAP();

        // System.out.println(Math.log(131));

    }

}
