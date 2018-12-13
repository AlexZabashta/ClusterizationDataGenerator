package ru.ifmo.ctddev.FSSARecSys.utils;

import smile.feature.Nominal2Binary;
import smile.mds.MDS;
import weka.core.*;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.instance.Normalize;
import weka.gui.arffviewer.ArffTable;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by sergey on 16.04.16.
 */
public class DimesionGenerator {

    public static void MultiDimScale(Instances instances, String name) throws IOException {
        EuclideanDistance e = new EuclideanDistance(instances);
        int datasetSize = instances.numInstances();

        double[][] distanses = new double[datasetSize][datasetSize];
        for (int i = 0; i < datasetSize; i++) {
            for (int j = i; j < datasetSize; j++) {
                if (i == j) {
                    distanses[i][j] = 0.0;
                } else {
                    distanses[i][j] = e.distance(instances.instance(i), instances.instance(j));
                    distanses[j][i] = distanses[i][j];
                }
            }
        }

        MDS mds = new MDS(distanses);
        double[][] newDataset = mds.getCoordinates();

        PrintWriter writer = new PrintWriter(name + ".csv", "UTF-8");
        writer.println("x,y");
        for (int p = 0; p < newDataset.length; p++) {
            writer.println(newDataset[p][0] + "," + newDataset[p][1]);
        }
        writer.close();

        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(name + ".csv"));
        Instances data = loader.getDataSet();

        // save ARFF
        ArffSaver asaver = new ArffSaver();
        asaver.setInstances(data);
        asaver.setFile(new File("/home/sergey/anew_datasets/" + name + ".arff"));
        //saver.setDestination(new File(name + ".arff"));
        asaver.writeBatch();
    }

    public static void main(String[] args) throws Exception {
        ArrayList<File> fileSet = new ArrayList<>();

        Files.walk(Paths.get("/home/sergey/areal_artificial")).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                //System.out.println(filePath);
                File file = new File(filePath.toString());
                fileSet.add(file);
            }
        });

        for (File f: fileSet){
//            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource(f.getPath());
//            Instances instances = dataSource.getDataSet();

          //  System.out.printf("%-20s", f.getName());
            BufferedReader reader =
                    new BufferedReader(new FileReader(f.getPath()));
            ArffLoader.ArffReader d = new ArffLoader.ArffReader(reader);
            Instances instancesRaw = d.getData();

            ReplaceMissingValues filter = new ReplaceMissingValues();
            filter.setInputFormat(instancesRaw);
            Instances instances = Filter.useFilter(instancesRaw, filter);

            Normalize normalize = new Normalize();
            normalize.setInputFormat(instances);
            instances = Filter.useFilter(instances, normalize);

            System.out.printf("%-20s", f.getName() + "   ");
            System.out.println(instances.numInstances());

            if (instances.numInstances() > 1500){
                int ammount = instances.numInstances() / 1000;

                FastVector attrs = new FastVector();
                for (int i = 0; i < instances.numAttributes(); i++) {
                    attrs.addElement(instances.attribute(i));
                }

                int a = 1000;

                if (f.getName().startsWith("birch"))
                    a = 2000;

                for (int i = 0; i < 20; i++) {
                    Instances dividedInstances = new Instances(f.getName() + (i + 1), attrs, a + 1);
                    for (int j = a * i; ((j < a * (i + 1))
                            && (j < instances.numInstances())); j++) {
                        dividedInstances.add(instances.instance(j));
                    }

                    if (dividedInstances.numInstances() > 150) {
                        ArffSaver saver = new ArffSaver();
                        saver.setInstances(dividedInstances);
                        saver.setFile(new File("/home/sergey/areal_datasets_final/" + f.getName() + (i + 1) + ".arff"));
                        //saver.setDestination(new File("./data/test.arff"));   // **not** necessary in 3.5.4 and later
                        saver.writeBatch();

                        if (dividedInstances.numAttributes() > 2)
                            MultiDimScale(dividedInstances, f.getName() + (i + 1));
                        else {
                            saver.setInstances(instances);
                            saver.setFile(new File("/home/sergey/anew_datasets/" + f.getName() + ".arff"));
                            //saver.setDestination(new File("./data/test.arff"));   // **not** necessary in 3.5.4 and later
                            saver.writeBatch();
                        }
                    }
                }
            } else {
                ArffSaver saver = new ArffSaver();
                saver.setInstances(instances);
                saver.setFile(new File("/home/sergey/areal_datasets_final/" + f.getName() + ".arff"));
                //saver.setDestination(new File("./data/test.arff"));   // **not** necessary in 3.5.4 and later
                saver.writeBatch();

                if (instances.numAttributes() > 2)
                    MultiDimScale(instances, f.getName());
                else {
                    saver.setInstances(instances);
                    saver.setFile(new File("/home/sergey/anew_datasets/" + f.getName() + ".arff"));
                    //saver.setDestination(new File("./data/test.arff"));   // **not** necessary in 3.5.4 and later
                    saver.writeBatch();
                }
                //Instances k = null;

            }
           //weka.datagenerators.clusterers.BIRCHCluster
        }
    }

}
