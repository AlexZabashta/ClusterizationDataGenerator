package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering;

import ru.ifmo.ctddev.FSSARecSys.db.internal.Dataset;
import ru.ifmo.ctddev.FSSARecSys.db.internal.MLAlgorithm;
import weka.clusterers.AbstractClusterer;
import weka.clusterers.Clusterer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Администратор on 21.09.2015.
 */
public class TestMetrics {

    public static ArrayList<ArrayList<Double>> resultMatrix = new ArrayList<ArrayList<Double>>();
    public static Map<Integer, String> algoMap = new HashMap<Integer, String>();
    public static Map<Integer, String> metricsMap = new HashMap<Integer, String>();

    public static ClustererEvaluator clustererEvaluator = null;

    public static ArrayList<Double> evaluateWithMetrics(Dataset dataset, MLAlgorithm mlAlgorithm) throws Exception {

//        System.out.println("==================================================");
//        System.out.println(mlAlgorithm.getName());
//        System.out.println("--------------------------------------------------");

        Clusterer as = AbstractClusterer.forName(mlAlgorithm.getClassPath(), weka.core.Utils.splitOptions(mlAlgorithm.getOptions()));
        clustererEvaluator = new ClustererEvaluator(mlAlgorithm.getName(), as);
        ClustererResult clustererResult = clustererEvaluator.evaluate(dataset.getName(), dataset.getInstances());

        ArrayList<Double> t = new ArrayList<>();

        t.add(clustererEvaluator.getDBIndex());
        metricsMap.put(t.size() - 1, "Davies-Bouldin");

        t.add(clustererEvaluator.getDunnIndex());
        metricsMap.put(t.size() - 1, "Dunn");

        t.add(clustererEvaluator.getSilhouetteIndex());
        metricsMap.put(t.size() - 1, "Silhouette");

        t.add(clustererEvaluator.getCHIndex());
        metricsMap.put(t.size() - 1, "CH");

        t.add(clustererEvaluator.getSDbw());
        metricsMap.put(t.size() - 1, "SDbw");

        t.add(clustererEvaluator.getSF());
        metricsMap.put(t.size() - 1, "SF");

//        t.add(clustererEvaluator.getCombinedMetric());
//        metricsMap.put(t.size() - 1, "Combined");

        t.add(clustererEvaluator.getCS());
        metricsMap.put(t.size() - 1, "CS");

        t.add(clustererEvaluator.getCOP());
        metricsMap.put(t.size() - 1, "COP");

        t.add(clustererEvaluator.getSV());
        metricsMap.put(t.size() - 1, "SV");

        t.add(clustererEvaluator.getOS());
        metricsMap.put(t.size() - 1, "OS");

        t.add(clustererEvaluator.getSymIndex());
        metricsMap.put(t.size() - 1, "SymIndex");

//        t.add(clustererEvaluator.getGamma());
//        metricsMap.put(t.size() - 1, "Gamma");
//
        t.add(clustererEvaluator.getCI());
        metricsMap.put(t.size() - 1, "CI");

        t.add(clustererEvaluator.getDaviesBouldinStarIndex());
        metricsMap.put(t.size() - 1, "DB*");

        t.add(clustererEvaluator.getGD31());
        metricsMap.put(t.size() - 1, "GD31");

        t.add(clustererEvaluator.getGD41());
        metricsMap.put(t.size() - 1, "GD41");

        t.add(clustererEvaluator.getGD51());
        metricsMap.put(t.size() - 1, "GD51");

        t.add(clustererEvaluator.getGD33());
        metricsMap.put(t.size() - 1, "GD33");

        t.add(clustererEvaluator.getGD43());
        metricsMap.put(t.size() - 1, "GD43");

        t.add(clustererEvaluator.getGD53());
        metricsMap.put(t.size() - 1, "GD53");

        return t;
    }

    public static void main(String [] args) throws Exception {
        File f = new File("simple.arff");
        File f2 = new File("simple_noisy.arff");

        List<File> simples = new ArrayList<>();
        simples.add(f);
        simples.add(f2);

        ArrayList<Double> tmp = new ArrayList<>();

        for (File fi: simples) {
            Dataset dataset = new Dataset(fi.getName(), fi, "clusterisation");
            //System.out.println(fi.getName());

            System.out.println("******************************************************");
            System.out.println(fi.getName());

            resultMatrix = new ArrayList<ArrayList<Double>>();

            MLAlgorithm mlkMeans1 = new MLAlgorithm("K-means-1", "weka.clusterers.SimpleKMeans", "-N 5 -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -S 10", "clusterisation");
            MLAlgorithm mlkMeans2 = new MLAlgorithm("K-means-2", "weka.clusterers.SimpleKMeans", "-N 5 -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -S 100", "clusterisation");
            MLAlgorithm mlkMeans3 = new MLAlgorithm("K-means-3", "weka.clusterers.SimpleKMeans", "-N 5 -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -S 200", "clusterisation");
            MLAlgorithm mlDBSCAN = new MLAlgorithm("DBSCAN", "weka.clusterers.DBSCAN", "-E 0.1 -M 6 -I weka.clusterers.forOPTICSAndDBScan.Databases.SequentialDatabase -D weka.clusterers.forOPTICSAndDBScan.DataObjects.EuclideanDataObject", "clusterisation");
            MLAlgorithm mlEM1 = new MLAlgorithm("EM-1", "weka.clusterers.EM", "-I 100 -N -1 -M 1.0E-6 -S 100", "clusterisation");
            MLAlgorithm mlEM2 = new MLAlgorithm("EM-2", "weka.clusterers.EM", "-I 100 -N -1 -M 1.0E-6 -S 200", "clusterisation");
            MLAlgorithm mlEM3 = new MLAlgorithm("EM-3", "weka.clusterers.EM", "-I 100 -N -1 -M 1.0E-6 -S 400", "clusterisation");
            MLAlgorithm mlFarthestFirst1 = new MLAlgorithm("FarthestFirst-1", "weka.clusterers.FarthestFirst", "-N 5 -S 1", "clusterisation");
            MLAlgorithm mlFarthestFirst2 = new MLAlgorithm("FarthestFirst-2", "weka.clusterers.FarthestFirst", "-N 5 -S 10", "clusterisation");
            MLAlgorithm mlFarthestFirst3 = new MLAlgorithm("FarthestFirst-3", "weka.clusterers.FarthestFirst", "-N 5 -S 100", "clusterisation");
            MLAlgorithm mlHieracical = new MLAlgorithm("Hieracical", "weka.clusterers.HierarchicalClusterer", "-N 5 -L MEAN -P -A \"weka.core.EuclideanDistance -R first-last\"", "clusterisation");
            MLAlgorithm mlXmeans1 = new MLAlgorithm("X-Means-1", "weka.clusterers.XMeans", "-I 1 -M 1000 -J 1000 -L 2 -H 6 -B 1.0 -C 0.5 -D \"weka.core.EuclideanDistance -R first-last\" -S 10", "clusterisation");
            MLAlgorithm mlXmeans2 = new MLAlgorithm("X-Means-2", "weka.clusterers.XMeans", "-I 1 -M 1000 -J 1000 -L 2 -H 6 -B 1.0 -C 0.5 -D \"weka.core.EuclideanDistance -R first-last\" -S 100", "clusterisation");
            MLAlgorithm mlXmeans3 = new MLAlgorithm("X-Means-3", "weka.clusterers.XMeans", "-I 1 -M 1000 -J 1000 -L 2 -H 6 -B 1.0 -C 0.5 -D \"weka.core.EuclideanDistance -R first-last\" -S 200", "clusterisation");

            tmp = evaluateWithMetrics(dataset, mlDBSCAN);
            algoMap.put(resultMatrix.size(), mlDBSCAN.getName());
            resultMatrix.add(tmp);

            tmp = evaluateWithMetrics(dataset, mlEM1);
            algoMap.put(resultMatrix.size(), mlEM1.getName());
            resultMatrix.add(tmp);

            tmp = evaluateWithMetrics(dataset, mlEM2);
            algoMap.put(resultMatrix.size(), mlEM2.getName());
            resultMatrix.add(tmp);

            tmp = evaluateWithMetrics(dataset, mlEM3);
            algoMap.put(resultMatrix.size(), mlEM3.getName());
            resultMatrix.add(tmp);

            tmp = evaluateWithMetrics(dataset, mlFarthestFirst1);
            algoMap.put(resultMatrix.size(), mlFarthestFirst1.getName());
            resultMatrix.add(tmp);

            tmp = evaluateWithMetrics(dataset, mlFarthestFirst2);
            algoMap.put(resultMatrix.size(), mlFarthestFirst2.getName());
            resultMatrix.add(tmp);

            tmp = evaluateWithMetrics(dataset, mlFarthestFirst3);
            algoMap.put(resultMatrix.size(), mlFarthestFirst3.getName());
            resultMatrix.add(tmp);

            tmp = evaluateWithMetrics(dataset, mlHieracical);
            algoMap.put(resultMatrix.size(), mlHieracical.getName());
            resultMatrix.add(tmp);

            tmp = evaluateWithMetrics(dataset, mlkMeans1);
            algoMap.put(resultMatrix.size(), mlkMeans1.getName());
            resultMatrix.add(tmp);

            tmp = evaluateWithMetrics(dataset, mlkMeans2);
            algoMap.put(resultMatrix.size(), mlkMeans2.getName());
            resultMatrix.add(tmp);

            tmp = evaluateWithMetrics(dataset, mlkMeans3);
            algoMap.put(resultMatrix.size(), mlkMeans3.getName());
            resultMatrix.add(tmp);

            tmp = evaluateWithMetrics(dataset, mlXmeans1);
            algoMap.put(resultMatrix.size(), mlXmeans1.getName());
            resultMatrix.add(tmp);

            tmp = evaluateWithMetrics(dataset, mlXmeans2);
            algoMap.put(resultMatrix.size(), mlXmeans2.getName());
            resultMatrix.add(tmp);

            tmp = evaluateWithMetrics(dataset, mlXmeans3);
            algoMap.put(resultMatrix.size(), mlXmeans3.getName());
            resultMatrix.add(tmp);


            System.out.printf("%-20s", "               ");
            for (int i = 0; i < metricsMap.size(); i++)
                System.out.printf("%-20s", metricsMap.get(i));
            System.out.println();
            for (int i = 0; i < resultMatrix.size(); i++) {
                ArrayList<Double> a = resultMatrix.get(i);
                System.out.printf( "%-20s", algoMap.get(i));
                for (Double v : a) {
                    System.out.printf("%-20s", v);
                }
                System.out.println();
            }
        }


        ///////////////////ovals//////////////////////////////

//        File of = new File("triple_folded.arff");
//
//        List<File> triple = new ArrayList<>();
//        triple.add(of);
//
//
//        for (File fi: triple) {
//            Dataset dataset = new Dataset(fi.getName(), fi, "clusterisation");
//            //System.out.println(fi.getName());
//
//            System.out.println("******************************************************");
//            System.out.println(fi.getName());
//
//            MLAlgorithm mlkMeans1 = new MLAlgorithm("K-means-1", "weka.clusterers.SimpleKMeans", "-N 3 -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -S 10", "clusterisation");
//            MLAlgorithm mlkMeans2 = new MLAlgorithm("K-means-2", "weka.clusterers.SimpleKMeans", "-N 3 -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -S 100", "clusterisation");
//            MLAlgorithm mlkMeans3 = new MLAlgorithm("K-means-3", "weka.clusterers.SimpleKMeans", "-N 3 -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -S 200", "clusterisation");
//            MLAlgorithm mlDBSCAN = new MLAlgorithm("DBSCAN", "weka.clusterers.DBSCAN", "-E 0.1 -M 6 -I weka.clusterers.forOPTICSAndDBScan.Databases.SequentialDatabase -D weka.clusterers.forOPTICSAndDBScan.DataObjects.EuclideanDataObject", "clusterisation");
//            MLAlgorithm mlEM1 = new MLAlgorithm("EM-1", "weka.clusterers.EM", "-I 100 -N 3 -M 1.0E-6 -S 100", "clusterisation");
//            MLAlgorithm mlEM2 = new MLAlgorithm("EM-2", "weka.clusterers.EM", "-I 100 -N 3 -M 1.0E-6 -S 200", "clusterisation");
//            MLAlgorithm mlEM3 = new MLAlgorithm("EM-3", "weka.clusterers.EM", "-I 100 -N 3 -M 1.0E-6 -S 400", "clusterisation");
//            MLAlgorithm mlFarthestFirst1 = new MLAlgorithm("FarthestFirst-1", "weka.clusterers.FarthestFirst", "-N 3 -S 1", "clusterisation");
//            MLAlgorithm mlFarthestFirst2 = new MLAlgorithm("FarthestFirst-2", "weka.clusterers.FarthestFirst", "-N 3 -S 10", "clusterisation");
//            MLAlgorithm mlFarthestFirst3 = new MLAlgorithm("FarthestFirst-3", "weka.clusterers.FarthestFirst", "-N 3 -S 100", "clusterisation");
//            MLAlgorithm mlHieracical = new MLAlgorithm("Hieracical", "weka.clusterers.HierarchicalClusterer", "-N 3 -L MEAN -P -A \"weka.core.EuclideanDistance -R first-last\"", "clusterisation");
//            MLAlgorithm mlXmeans1 = new MLAlgorithm("X-Means-1", "weka.clusterers.XMeans", "-I 1 -M 1000 -J 1000 -L 2 -H 6 -B 1.0 -C 0.5 -D \"weka.core.EuclideanDistance -R first-last\" -S 10", "clusterisation");
//            MLAlgorithm mlXmeans2 = new MLAlgorithm("X-Means-2", "weka.clusterers.XMeans", "-I 1 -M 1000 -J 1000 -L 2 -H 6 -B 1.0 -C 0.5 -D \"weka.core.EuclideanDistance -R first-last\" -S 100", "clusterisation");
//            MLAlgorithm mlXmeans3 = new MLAlgorithm("X-Means-3", "weka.clusterers.XMeans", "-I 1 -M 1000 -J 1000 -L 2 -H 6 -B 1.0 -C 0.5 -D \"weka.core.EuclideanDistance -R first-last\" -S 200", "clusterisation");
//
//            tmp = evaluateWithMetrics(dataset, mlkMeans1);
//            algoMap.put(resultMatrix.size(), mlkMeans1.getName());
//            resultMatrix.add(tmp);
//
//            tmp = evaluateWithMetrics(dataset, mlkMeans2);
//            algoMap.put(resultMatrix.size(), mlkMeans2.getName());
//            resultMatrix.add(tmp);
//
//            tmp = evaluateWithMetrics(dataset, mlkMeans3);
//            algoMap.put(resultMatrix.size(), mlkMeans3.getName());
//            resultMatrix.add(tmp);
//
//            tmp = evaluateWithMetrics(dataset, mlEM1);
//            algoMap.put(resultMatrix.size(), mlEM1.getName());
//            resultMatrix.add(tmp);
//
//            tmp = evaluateWithMetrics(dataset, mlEM2);
//            algoMap.put(resultMatrix.size(), mlEM2.getName());
//            resultMatrix.add(tmp);
//
//            tmp = evaluateWithMetrics(dataset, mlEM3);
//            algoMap.put(resultMatrix.size(), mlEM3.getName());
//            resultMatrix.add(tmp);
//
//            tmp = evaluateWithMetrics(dataset, mlDBSCAN);
//            algoMap.put(resultMatrix.size(), mlDBSCAN.getName());
//            resultMatrix.add(tmp);
//
//            tmp = evaluateWithMetrics(dataset, mlFarthestFirst1);
//            algoMap.put(resultMatrix.size(), mlFarthestFirst1.getName());
//            resultMatrix.add(tmp);
//
//            tmp = evaluateWithMetrics(dataset, mlFarthestFirst2);
//            algoMap.put(resultMatrix.size(), mlFarthestFirst2.getName());
//            resultMatrix.add(tmp);
//
//            tmp = evaluateWithMetrics(dataset, mlFarthestFirst3);
//            algoMap.put(resultMatrix.size(), mlFarthestFirst3.getName());
//            resultMatrix.add(tmp);
//
//            tmp = evaluateWithMetrics(dataset, mlHieracical);
//            algoMap.put(resultMatrix.size(), mlHieracical.getName());
//            resultMatrix.add(tmp);
//
//            tmp = evaluateWithMetrics(dataset, mlXmeans1);
//            algoMap.put(resultMatrix.size(), mlXmeans1.getName());
//            resultMatrix.add(tmp);
//
//            tmp = evaluateWithMetrics(dataset, mlXmeans2);
//            algoMap.put(resultMatrix.size(), mlXmeans2.getName());
//            resultMatrix.add(tmp);
//
//            tmp = evaluateWithMetrics(dataset, mlXmeans3);
//            algoMap.put(resultMatrix.size(), mlXmeans3.getName());
//            resultMatrix.add(tmp);
//
//
//            System.out.printf("%-20s", "               ");
//            for (int i = 0; i < metricsMap.size(); i++)
//                System.out.printf("%-20s", metricsMap.get(i));
//            System.out.println();
//            for (int i = 0; i < resultMatrix.size(); i++) {
//                ArrayList<Double> a = resultMatrix.get(i);
//                System.out.printf( "%-20s", algoMap.get(i));
//                for (Double v : a) {
//                    System.out.printf("%-20s", v);
//                }
//                System.out.println();
//            }
//        }


        List<Dataset> datasetArray = new ArrayList<>();
        List<File> fileSet = new ArrayList<>();

        Files.walk(Paths.get("/home/sergey/masters/FSSARecSys/test")).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                //System.out.println(filePath);
                File file = new File(filePath.toString());
                fileSet.add(file);
            }
        });
//
//        File stripesFile = new File("stripes.arff");
//        fileSet.add(stripesFile);
//
//        File centerFile = new File("center.arff");
//        fileSet.add(centerFile);
//
//        File linkedFile = new File("linked.arff");
//        fileSet.add(linkedFile);
//
//        File overlapFile = new File("overlap.arff");
//        fileSet.add(overlapFile);
//
//        File sparseFile = new File("sparse.arff");
//        fileSet.add(sparseFile);

        //** add more files for different structures

        for (File fs: fileSet)
            datasetArray.add(new Dataset(fs.getName(), fs, "clusterisation"));

        for (Dataset d : datasetArray) {
            System.out.println("******************************************************");
            System.out.println(d.getName());

            resultMatrix = new ArrayList<ArrayList<Double>>();

            MLAlgorithm imlkMeans1 = new MLAlgorithm("K-means-1", "weka.clusterers.SimpleKMeans", "-N 2 -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -S 10", "clusterisation");
            MLAlgorithm imlkMeans2 = new MLAlgorithm("K-means-2", "weka.clusterers.SimpleKMeans", "-N 2 -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -S 100", "clusterisation");
            MLAlgorithm imlkMeans3 = new MLAlgorithm("K-means-3", "weka.clusterers.SimpleKMeans", "-N 2 -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -S 200", "clusterisation");
            MLAlgorithm imlDBSCAN = new MLAlgorithm("DBSCAN", "weka.clusterers.DBSCAN", "-E 0.1 -M 6 -I weka.clusterers.forOPTICSAndDBScan.Databases.SequentialDatabase -D weka.clusterers.forOPTICSAndDBScan.DataObjects.EuclideanDataObject", "clusterisation");
            MLAlgorithm imlEM1 = new MLAlgorithm("EM-1", "weka.clusterers.EM", "-I 100 -N 2 -M 1.0E-6 -S 100", "clusterisation");
            MLAlgorithm imlEM2 = new MLAlgorithm("EM-2", "weka.clusterers.EM", "-I 100 -N 2 -M 1.0E-6 -S 200", "clusterisation");
            MLAlgorithm imlEM3 = new MLAlgorithm("EM-3", "weka.clusterers.EM", "-I 100 -N 2 -M 1.0E-6 -S 400", "clusterisation");
            MLAlgorithm imlFarthestFirst1 = new MLAlgorithm("FarthestFirst-1", "weka.clusterers.FarthestFirst", "-N 2 -S 1", "clusterisation");
            MLAlgorithm imlFarthestFirst2 = new MLAlgorithm("FarthestFirst-2", "weka.clusterers.FarthestFirst", "-N 2 -S 10", "clusterisation");
            MLAlgorithm imlFarthestFirst3 = new MLAlgorithm("FarthestFirst-3", "weka.clusterers.FarthestFirst", "-N 2 -S 100", "clusterisation");
            MLAlgorithm imlHieracical = new MLAlgorithm("Hieracical", "weka.clusterers.HierarchicalClusterer", "-N 2 -L AVERAGE -P -A \"weka.core.EuclideanDistance -R first-last\"", "clusterisation");
            MLAlgorithm imlXmeans1 = new MLAlgorithm("X-Means-1", "weka.clusterers.XMeans", "-I 1 -M 1000 -J 1000 -L 2 -H 4 -B 1.0 -C 0.6 -D \"weka.core.EuclideanDistance -R first-last\" -S 10", "clusterisation");
            MLAlgorithm imlXmeans2 = new MLAlgorithm("X-Means-2", "weka.clusterers.XMeans", "-I 1 -M 1000 -J 1000 -L 2 -H 4 -B 1.0 -C 0.6 -D \"weka.core.EuclideanDistance -R first-last\" -S 100", "clusterisation");
            MLAlgorithm imlXmeans3 = new MLAlgorithm("X-Means-3", "weka.clusterers.XMeans", "-I 1 -M 1000 -J 1000 -L 2 -H 4 -B 1.0 -C 0.6 -D \"weka.core.EuclideanDistance -R first-last\" -S 300", "clusterisation");

            //ArrayList<Double> tmp = null;

            if (d.getName() != "triple_folded.arff") {

                tmp = evaluateWithMetrics(d, imlDBSCAN);
                algoMap.put(resultMatrix.size(), imlDBSCAN.getName());
                resultMatrix.add(tmp);


                if (d.getName() != "ovals.arff" && d.getName() != "ovals_noisy.arff") {

                    tmp = evaluateWithMetrics(d, imlEM1);
                    algoMap.put(resultMatrix.size(), imlEM1.getName());
                    resultMatrix.add(tmp);

                    tmp = evaluateWithMetrics(d, imlEM2);
                    algoMap.put(resultMatrix.size(), imlEM2.getName());
                    resultMatrix.add(tmp);

                    tmp = evaluateWithMetrics(d, imlEM3);
                    algoMap.put(resultMatrix.size(), imlEM3.getName());
                    resultMatrix.add(tmp);
                }

                tmp = evaluateWithMetrics(d, imlFarthestFirst1);
                algoMap.put(resultMatrix.size(), imlFarthestFirst1.getName());
                resultMatrix.add(tmp);

                tmp = evaluateWithMetrics(d, imlFarthestFirst2);
                algoMap.put(resultMatrix.size(), imlFarthestFirst2.getName());
                resultMatrix.add(tmp);

                tmp = evaluateWithMetrics(d, imlFarthestFirst3);
                algoMap.put(resultMatrix.size(), imlFarthestFirst3.getName());
                resultMatrix.add(tmp);

                tmp = evaluateWithMetrics(d, imlHieracical);
                algoMap.put(resultMatrix.size(), imlHieracical.getName());
                resultMatrix.add(tmp);

                tmp = evaluateWithMetrics(d, imlkMeans1);
                algoMap.put(resultMatrix.size(), imlkMeans1.getName());
                resultMatrix.add(tmp);

                tmp = evaluateWithMetrics(d, imlkMeans2);
                algoMap.put(resultMatrix.size(), imlkMeans2.getName());
                resultMatrix.add(tmp);

                tmp = evaluateWithMetrics(d, imlkMeans3);
                algoMap.put(resultMatrix.size(), imlkMeans3.getName());
                resultMatrix.add(tmp);

                tmp = evaluateWithMetrics(d, imlXmeans1);
                algoMap.put(tmp.size() - 1, imlXmeans1.getName());
                resultMatrix.add(tmp);

                tmp = evaluateWithMetrics(d, imlXmeans2);
                algoMap.put(tmp.size() - 1, imlXmeans2.getName());
                resultMatrix.add(tmp);

                tmp = evaluateWithMetrics(d, imlXmeans3);
                algoMap.put(tmp.size() - 1, imlXmeans3.getName());
                resultMatrix.add(tmp);


                System.out.printf("%-20s", "               ");
                for (int i = 0; i < metricsMap.size(); i++)
                    System.out.printf("%-20s", metricsMap.get(i));
                System.out.println();
                for (int i = 0; i < resultMatrix.size(); i++) {
                    ArrayList<Double> a = resultMatrix.get(i);
                    System.out.printf("%-20s", algoMap.get(i));
                    for (Double v : a) {
                        System.out.printf("%-20s", v);
                    }
                    System.out.println();
                }
            }
        }
    }
}
