package ru.ifmo.ctddev.FSSARecSys.utils;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.instance.Normalize;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by sergey on 23.04.16.
 */
public class DrawPicture {
    private static ArrayList<Color> colors = new ArrayList<>();

    private static void assignColors(){
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.PINK);
        colors.add(Color.CYAN);
        colors.add(Color.orange);
        colors.add(Color.MAGENTA);
        colors.add(Color.YELLOW);
        colors.add(Color.getHSBColor(180, 240, 120));
        colors.add(Color.getHSBColor(120, 80, 90));
        colors.add(Color.getHSBColor(40, 80, 90));
        colors.add(Color.getHSBColor(200, 240, 30));
        colors.add(Color.getHSBColor(200, 240, 132));
        colors.add(Color.getHSBColor(0, 240, 60));
        colors.add(Color.getHSBColor(88, 228, 133));
        colors.add(Color.getHSBColor(160, 240, 180));
        colors.add(Color.getHSBColor(80, 240, 120));
        colors.add(Color.getHSBColor(140, 240, 120));
        colors.add(Color.getHSBColor(220, 240, 180));
        colors.add(Color.LIGHT_GRAY);
        colors.add(Color.getHSBColor(0, 150, 255));
    }



    private int numOfClusters;
    private static Instances dataSet;
    private static int[] assignments;

    //private ArrayList<Color> colors = new ArrayList<>();

    public static void setDataSet(Instances dataSetNew) {
        dataSet = dataSetNew;
    }

    public static void setCapacities(int[] ass){
        assignments = ass;
        assignColors();
    }

    public static void drawSimplePicture(int width, int height, String path) throws IOException {

        Instances instances = dataSet;

        double minx = Double.MAX_VALUE;
        double miny = Double.MAX_VALUE;

        for(int i = 0; i < instances.numInstances(); i++) {
            if (minx > instances.instance(i).value(0))
                minx = instances.instance(i).value(0);
            if (miny > instances.instance(i).value(1))
                miny = instances.instance(i).value(1);
        }

        for (int i = 0; i < instances.numInstances(); i++) {
            double currX = instances.instance(i).value(0);
            double currY = instances.instance(i).value(1);

            instances.instance(i).setValue(0, currX + Math.abs(minx));
            instances.instance(i).setValue(1, currY + Math.abs(miny));
        }

        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for(int i = 0; i < instances.numInstances(); i++) {
            if (maxX < instances.instance(i).value(0))
                maxX = instances.instance(i).value(0);
            if (maxY < instances.instance(i).value(1))
                maxY = instances.instance(i).value(1);
        }

        for (int i = 0; i < instances.numInstances(); i++) {
            double currX = instances.instance(i).value(0);
            double currY = instances.instance(i).value(1);

            instances.instance(i).setValue(0, currX / maxX);
            instances.instance(i).setValue(1, currY / maxY);
        }



        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D ig = bi.createGraphics();

        ig.setColor(Color.WHITE);
        ig.fillRect(0, 0, width, height);

        //ig.setBackground(Color.GRAY);
        for (int i = 0; i < instances.numInstances(); i++){
            Instance instance = instances.instance(i);
            int x = (int)(10 + instance.value(0) * 500.0);
            int y = (int)(10 + instance.value(1) * 500.0);

            if (assignments != null) {
                if (i > assignments.length)
                    ig.setPaint(Color.BLACK);
                else {
                    if (assignments[i] < 0)
                        ig.setPaint(Color.DARK_GRAY);
                    else
                        ig.setPaint(colors.get(assignments[i]));
                }

            } else {
                ig.setPaint(Color.black);
            }

            ig.draw(new Line2D.Double(x-1, y-1, x+1, y+1));
            ig.draw(new Line2D.Double(x-1, y+1, x+1, y-1));
            ig.draw(new Line2D.Double(x-1, y, x+1, y));
            ig.draw(new Line2D.Double(x, y-1, x, y-1));

            //ig.setPaint(Color.black);
           //ig.drawString(algoName, width / 3, height - 25);

        }
        File file = new File(path);
        ImageIO.write(bi, "PNG", file);

    }

    public static void main(String[] args) throws Exception {
        ArrayList<File> fileSet = new ArrayList<>();

        Files.walk(Paths.get("/home/sergey/anew_datasets")).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                //System.out.println(filePath);
                File file = new File(filePath.toString());
                fileSet.add(file);
            }
        });

        for (File f : fileSet) {
            BufferedReader reader =
                    new BufferedReader(new FileReader(f.getPath()));
            ArffLoader.ArffReader d = new ArffLoader.ArffReader(reader);
            Instances instances = d.getData();




//            Normalize normalize = new Normalize();
//            normalize.setNorm(500.0);
//            normalize.setInputFormat(instances);
//
//            instances = Filter.useFilter(instances, normalize);
//
            setDataSet(instances);

            drawSimplePicture(520, 520, "/home/sergey/anew_pictures/" + f.getName() + ".png");
            System.out.println(f.getName());


        }
    }
}
