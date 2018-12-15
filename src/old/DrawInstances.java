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
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

public class DrawInstances {

    static String src = "PCA_instances.arff";

    public static void main(String[] args) throws IOException {

        int w = 1600, h = 1200, r = 3;
        double scale = 0.98, offset = (1 - scale) / 2;

        Color[] colors = { Color.BLACK, Color.RED, Color.GREEN, Color.BLUE };

        try (Reader reader = new FileReader(src)) {

            Instances instances = new Instances(reader);
            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

            int n = instances.size();
            int k = colors.length;
            int m = n / k;

            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    image.setRGB(x, y, -1);
                }
            }
            double minX = Double.POSITIVE_INFINITY, minY = minX, maxX = -minX, maxY = maxX;

            for (int i = 0; i < n; i++) {
                double x = instances.get(i).value(0);
                double y = instances.get(i).value(1);

                minX = Math.min(minX, x);
                maxX = Math.max(maxX, x);

                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
            }

            Graphics2D graphics = (Graphics2D) image.getGraphics();

            Random random = new Random();
            int[] p = new int[n];

            for (int i = 0; i < n; i++) {
                int j = random.nextInt(i + 1);
                p[i] = p[j];
                p[j] = i;
            }

            for (int i : p) {
                int t = toInt(instances.get(i).value(2));
                int b = i % m;
                Color c = colors[t];

                if (t == 0) {
                    continue;
                }

                int x1 = toInt(w * (scale * (instances.get(i).value(0) - minX) / (maxX - minX) + offset));
                int y1 = toInt(h * (scale * (instances.get(i).value(1) - minY) / (maxY - minY) + offset));

                int x2 = toInt(w * (scale * (instances.get(b).value(0) - minX) / (maxX - minX) + offset));
                int y2 = toInt(h * (scale * (instances.get(b).value(1) - minY) / (maxY - minY) + offset));

                graphics.setColor(new Color(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 0.5f));
                graphics.drawLine(x1, h - y1 - 1, x2, h - y2 - 1);
            }

            for (int i = 0; i < n; i++) {
                int j = random.nextInt(i + 1);
                p[i] = p[j];
                p[j] = i;
            }

            for (int i : p) {
                int t = toInt(instances.get(i).value(2));
                int c = colors[t].getRGB();

                int x = toInt(w * (scale * (instances.get(i).value(0) - minX) / (maxX - minX) + offset));
                int y = toInt(h * (scale * (instances.get(i).value(1) - minY) / (maxY - minY) + offset));

                for (int dx = -r; dx <= r; dx++) {
                    for (int dy = -r; dy <= r; dy++) {
                        if (Math.abs(dx) + Math.abs(dy) <= r) {
                            int px = x + dx;
                            int py = y + dy;
                            if (0 <= px && px < w) {
                                if (0 <= py && py < h) {
                                    image.setRGB(px, h - py - 1, c);
                                }
                            }
                        }

                    }
                }

            }

            ImageIO.write(image, "png", new File("img.png"));
        }

    }

    static int toInt(double d) {
        return (int) Math.round(d);
    }

}
