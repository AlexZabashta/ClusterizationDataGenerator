package ru.ifmo.ctddev.FSSARecSys.utils;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Created by Администратор on 21.09.2015.
 */
public class DatasetFromPicture {

    public static BufferedImage image = null;

    public static boolean checkForBlack(int pixel) {
        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;
        if (red == 0 && blue == 0 && green == 0)
            return true;
        return false;
    }


    public static void main(String[] args) throws IOException {

        String name = "circle_in_star";

        File sourceimage = new File(name + ".jpg");
        image = ImageIO.read(sourceimage);

        List<Pair<Integer, Integer>> result = new ArrayList<>();

        int height = image.getHeight();
        int width = image.getWidth();

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                if(checkForBlack(image.getRGB(i, j))) {
                    result.add(new Pair<>(i, j));
                }
            }
        PrintWriter writer = new PrintWriter(name + ".csv", "UTF-8");
        writer.println("x,y");
        for (Pair<Integer, Integer> p : result)
        writer.println(p.first + "," + p.second);
        writer.close();

        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(name + ".csv"));
        Instances data = loader.getDataSet();

        // save ARFF
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        saver.setFile(new File("/home/sergey/masters/FSSARecSys/test/" + name + ".arff"));
        //saver.setDestination(new File(name + ".arff"));
        saver.writeBatch();
    }
}
