package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.ifmo.ctddev.FSSARecSys.calculators.mfextraction.distances.*;
import ru.ifmo.ctddev.FSSARecSys.db.internal.Dataset;
import ru.ifmo.ctddev.FSSARecSys.db.internal.MLAlgorithm;
import ru.ifmo.ctddev.FSSARecSys.utils.DrawPicture;
import weka.clusterers.AbstractClusterer;
import weka.clusterers.Clusterer;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.instance.Normalize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by sergey on 26.04.16.
 */
public class TestRealMetrcis {
    private static String resultXLS = "results_real_original.xlsx";
    public static XSSFWorkbook myWorkBook;

    public static ArrayList<Integer> metricsWithBestMin = new ArrayList<>();

    public static ArrayList<ArrayList<Double>> resultMatrix = new ArrayList<ArrayList<Double>>();
    public static Map<Integer, String> algoMap = new HashMap<Integer, String>();
    public static Map<Integer, String> metricsMap = new HashMap<Integer, String>();

    public static ClustererEvaluator clustererEvaluator = null;

    public static Double evaluateWithCOP(Dataset dataset, MLAlgorithm mlAlgorithm) throws Exception {
        Clusterer as = AbstractClusterer.forName(mlAlgorithm.getClassPath(), weka.core.Utils.splitOptions(mlAlgorithm.getOptions()));
        clustererEvaluator = new ClustererEvaluator(mlAlgorithm.getName(), as);
        ClustererResult clustererResult = clustererEvaluator.evaluate(dataset.getName(), dataset.getInstances());

        return clustererEvaluator.getDBIndex();
    }

    public static ArrayList<Double> evaluateWithMetrics(Dataset dataset, MLAlgorithm mlAlgorithm) throws Exception {
        Clusterer as = AbstractClusterer.forName(mlAlgorithm.getClassPath(), weka.core.Utils.splitOptions(mlAlgorithm.getOptions()));
        clustererEvaluator = new ClustererEvaluator(mlAlgorithm.getName(), as);
        ClustererResult clustererResult = clustererEvaluator.evaluate(dataset.getName(), dataset.getInstances());

        ArrayList<Double> t = new ArrayList<>();
//
        t.add(clustererEvaluator.getDBIndex());
        metricsMap.put(t.size() - 1, "Davies-Bouldin");
        System.out.print(1 + " ");

        t.add(clustererEvaluator.getDunnIndex());
        metricsMap.put(t.size() - 1, "Dunn");
        System.out.print(2 + " ");

        t.add(clustererEvaluator.getSilhouetteIndex());
        metricsMap.put(t.size() - 1, "Silhouette");
        System.out.print(3 + " ");

        t.add(clustererEvaluator.getCHIndex());
        metricsMap.put(t.size() - 1, "CH");
        System.out.print(4 + " ");

        t.add(clustererEvaluator.getSDbw());
        metricsMap.put(t.size() - 1, "SDbw");
        System.out.print(5 + " ");

        t.add(clustererEvaluator.getSF());
        metricsMap.put(t.size() - 1, "SF");
        System.out.print(6 + " ");

        t.add(clustererEvaluator.getCS());
        metricsMap.put(t.size() - 1, "CS");
        System.out.print(7 + " ");

        t.add(clustererEvaluator.getCOP());
        metricsMap.put(t.size() - 1, "COP");
        System.out.print(8 + " ");

        t.add(clustererEvaluator.getSV());
        metricsMap.put(t.size() - 1, "SV");
        System.out.print(9 + " ");

        t.add(clustererEvaluator.getOS());
        metricsMap.put(t.size() - 1, "OS");
        System.out.print(10 + " ");

        t.add(clustererEvaluator.getSymIndex());
        metricsMap.put(t.size() - 1, "SymIndex");
        System.out.print(11 + " ");

        t.add(clustererEvaluator.getCI());
        metricsMap.put(t.size() - 1, "CI");
        System.out.print(12 + " ");

        t.add(clustererEvaluator.getDaviesBouldinStarIndex());
        metricsMap.put(t.size() - 1, "DB*");
        System.out.print(13 + " ");

        t.add(clustererEvaluator.getGD31());
        metricsMap.put(t.size() - 1, "GD31");
        System.out.print(14 + " ");

        t.add(clustererEvaluator.getGD41());
        metricsMap.put(t.size() - 1, "GD41");
        System.out.print(15 + " ");


        t.add(clustererEvaluator.getGD51());
        metricsMap.put(t.size() - 1, "GD51");
        System.out.print(16 + " ");

        t.add(clustererEvaluator.getGD33());
        metricsMap.put(t.size() - 1, "GD33");
        System.out.print(17 + " ");

        t.add(clustererEvaluator.getGD43());
        metricsMap.put(t.size() - 1, "GD43");
        System.out.print(18 + " ");

        t.add(clustererEvaluator.getGD53());
        metricsMap.put(t.size() - 1, "GD53");
        System.out.println(19);

        return t;
    }

    public static void main(String [] args) throws Exception {
        List<Dataset> datasetArray = new ArrayList<>();
        List<File> fileSet = new ArrayList<>();

        ArrayList<String> fileNames = new ArrayList<>();
        Collections.addAll(metricsWithBestMin, 1, 5, 9, 10, 12, 13);

//        File myFile = new File(resultXLS);
//        FileInputStream fis = new FileInputStream(myFile);
//
//        myWorkBook = new XSSFWorkbook(fis);

//        HashMap<String, Integer> clusterNums = new HashMap<>();
//        Scanner input = new Scanner(new File("clusterNums.txt"));
//        while (input.hasNext()){
//            String nextLine = input.nextLine();
//            String[] tokenize = nextLine.split(" ");
//            //System.out.println(tokenize[0]);
//            clusterNums.put(tokenize[0], Integer.parseInt(tokenize[1]));
//
//        }
//        input.close();

        HashMap<String, ArrayList<Integer>> clusterNums = new HashMap<>();
        Scanner input = new Scanner(new File("clusterDistr"));
        while (input.hasNext()){
            String nextLine = input.nextLine();
            String[] tokenize = nextLine.split(" ");
            ArrayList<Integer> tmp = new ArrayList<>();
            for(int i = 2; i < 12; i++){
                tmp.add(Integer.parseInt(tokenize[i]));
            }
            //System.out.println(tokenize[0]);
            clusterNums.put(tokenize[1], tmp);

        }
        input.close();


        Files.walk(Paths.get("/home/sergey/anew_original_datasets/")).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                //System.out.println(filePath);
                File file = new File(filePath.toString());
                fileSet.add(file);
                //System.out.println(file.getName());
            }
        });

        for (File fs: fileSet)
            datasetArray.add(new Dataset(fs.getName(), fs, "clusterisation"));

        int k = 1;

        System.out.println(new Date(System.currentTimeMillis()));

        for (Dataset d : datasetArray) {

            File myFile = new File(resultXLS);
            FileInputStream fis = new FileInputStream(myFile);

            myWorkBook = new XSSFWorkbook(fis);

            System.out.println(k++ + " " + d.getName() + " ");

            if (myWorkBook.getSheet(d.getName()) == null && k >= 40) {
                //System.out.print(new Date(System.currentTimeMillis()) + " "+ d.getName() + " ");

                resultMatrix = new ArrayList<ArrayList<Double>>();

                ArrayList<MLAlgorithm> algorithms = new ArrayList<>();
                //String.format("%03d", k) +
                myWorkBook.createSheet(d.getName());
                XSSFSheet currentSheet = myWorkBook.getSheetAt(myWorkBook.getNumberOfSheets() - 1);

//            String path = "";
//            File file = new File("/home/sergey/anew_pictures/" + d.getName());
//            if (!file.exists()) {
//                file.mkdir();
//            }
//            path = file.getPath() + "/" +
//                    d.getName() + ".png";
//
//            DrawPicture.setDataSet(d.getInstances());
//            DrawPicture.setCapacities(null);
//            DrawPicture.drawSimplePicture(520, 520, path);

                ArrayList<Integer> clAmmount = clusterNums.get(d.getName());

                algorithms.add(new MLAlgorithm("K-means-1", "weka.clusterers.SimpleKMeans", "-N " + clAmmount.get(0) + " -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -S 10", "clusterisation"));
                algorithms.add(new MLAlgorithm("K-means-2", "weka.clusterers.SimpleKMeans", "-N " + clAmmount.get(1) + " -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -S 159", "clusterisation"));
                algorithms.add(new MLAlgorithm("K-means-3", "weka.clusterers.SimpleKMeans", "-N " + clAmmount.get(2) + " -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -S 171", "clusterisation"));
                algorithms.add(new MLAlgorithm("EM-1", "weka.clusterers.EM", "-I 100 -N " + clAmmount.get(3) + " -M 1.0E-6 -S 100", "clusterisation"));
                algorithms.add(new MLAlgorithm("EM-2", "weka.clusterers.EM", "-I 100 -N " + clAmmount.get(4) + " -M 1.0E-6 -S 201", "clusterisation"));
                algorithms.add(new MLAlgorithm("EM-3", "weka.clusterers.EM", "-I 100 -N " + clAmmount.get(5) + " -M 1.0E-6 -S 32", "clusterisation"));
                algorithms.add(new MLAlgorithm("FarthestFirst-1", "weka.clusterers.FarthestFirst", "-N " + clAmmount.get(6) + " -S 1", "clusterisation"));
                algorithms.add(new MLAlgorithm("FarthestFirst-2", "weka.clusterers.FarthestFirst", "-N " + clAmmount.get(7) + " -S 15", "clusterisation"));
                algorithms.add(new MLAlgorithm("FarthestFirst-3", "weka.clusterers.FarthestFirst", "-N " + clAmmount.get(8) + " -S 185", "clusterisation"));
                algorithms.add(new MLAlgorithm("Hieracical", "weka.clusterers.HierarchicalClusterer", "-N " + clAmmount.get(9) + " -L AVERAGE -P -A \"weka.core.EuclideanDistance -R first-last\"", "clusterisation"));
                algorithms.add(new MLAlgorithm("DBSCAN", "weka.clusterers.DBSCAN", "-E 0.1 -M 6 -I weka.clusterers.forOPTICSAndDBScan.Databases.SequentialDatabase -D weka.clusterers.forOPTICSAndDBScan.DataObjects.EuclideanDataObject", "clusterisation"));
                algorithms.add(new MLAlgorithm("X-Means-1", "weka.clusterers.XMeans", "-I 1 -M 1000 -J 1000 -L 2 -H 20 -B 1.0 -C 0.5 -D \"weka.core.EuclideanDistance -R first-last\" -S 10", "clusterisation"));
                algorithms.add(new MLAlgorithm("X-Means-2", "weka.clusterers.XMeans", "-I 1 -M 1000 -J 1000 -L 2 -H 20 -B 1.0 -C 0.5 -D \"weka.core.EuclideanDistance -R first-last\" -S 102", "clusterisation"));
                algorithms.add(new MLAlgorithm("X-Means-3", "weka.clusterers.XMeans", "-I 1 -M 1000 -J 1000 -L 2 -H 20 -B 1.0 -C 0.5 -D \"weka.core.EuclideanDistance -R first-last\" -S 204", "clusterisation"));

                for (int i = 0; i < algorithms.size(); i++){
                    ArrayList<Double> p = evaluateWithMetrics(d, algorithms.get(i));
                    algoMap.put(resultMatrix.size(), algorithms.get(i).getName());
                    resultMatrix.add(p);
                }

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

                Row rHead = currentSheet.createRow(0);
                rHead.createCell(0);
                for (int i = 0; i < metricsMap.size(); i++) {
                    rHead.createCell(i + 1);
                    rHead.getCell(i + 1).setCellValue(metricsMap.get(i));
                }
                for (int i = 0; i < resultMatrix.size(); i++){
                    Row r = currentSheet.createRow(i + 1);
                    r.createCell(0);
                    r.getCell(0).setCellValue(algoMap.get(i));
                    for (int j = 0; j < resultMatrix.get(i).size(); j++){
                        r.createCell(j + 1);

                        Double num = resultMatrix.get(i).get(j);
                        if (num.toString() == "NaN")
                            r.getCell(j + 1).setCellValue("NaN");
                        if (num == Double.NEGATIVE_INFINITY || num == Double.POSITIVE_INFINITY) {
                            r.getCell(j + 1).setCellValue("Infinity");
                        } else {
                            r.getCell(j + 1).setCellValue(num);
                        }
                    }
                }


//            double minx = Double.MAX_VALUE;
//            double miny = Double.MAX_VALUE;
//
//            //Instances instances = d.getInstances();
//
//            for(int i = 0; i < d.getInstances().numInstances(); i++) {
//                if (minx > d.getInstances().instance(i).value(0))
//                    minx = d.getInstances().instance(i).value(0);
//                if (miny > d.getInstances().instance(i).value(1))
//                    miny = d.getInstances().instance(i).value(1);
//            }
//
//            for (int i = 0; i < d.getInstances().numInstances(); i++) {
//                double currX = d.getInstances().instance(i).value(0);
//                double currY = d.getInstances().instance(i).value(1);
//
//                d.getInstances().instance(i).setValue(0, currX + Math.abs(minx));
//                d.getInstances().instance(i).setValue(1, currY + Math.abs(miny));
//            }
//
//            double maxX = Double.MIN_VALUE;
//            double maxY = Double.MIN_VALUE;
//
//            for(int i = 0; i < d.getInstances().numInstances(); i++) {
//                if (maxX < d.getInstances().instance(i).value(0))
//                    maxX = d.getInstances().instance(i).value(0);
//                if (maxY < d.getInstances().instance(i).value(1))
//                    maxY = d.getInstances().instance(i).value(1);
//            }
//
//            for (int i = 0; i < d.getInstances().numInstances(); i++) {
//                double currX = d.getInstances().instance(i).value(0);
//                double currY = d.getInstances().instance(i).value(1);
//
//                d.getInstances().instance(i).setValue(0, currX / maxX);
//                d.getInstances().instance(i).setValue(1, currY / maxY);
//            }

//            int start = 0, end = 0;
//            int ammount = clusterNums.get(d.getName());
//            if (ammount == -1) {
//                start = 8;
//                end = 21;
//            } else {
//                start = ammount;
//                end = ammount + 3;
//            }
//
//            int maxValCluster[] = new int[10];
//            double maxVals[] = new double[10];
//            for (int i = start; i < end; i++){
//                //ArrayList<Double> tmp = new ArrayList<>();
//
//                ArrayList<MLAlgorithm> clusterOptimizationAlgorightms = new ArrayList<>();
//
//                clusterOptimizationAlgorightms.add(new MLAlgorithm("K-means-1", "weka.clusterers.SimpleKMeans", "-N " + i + " -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -S 10", "clusterisation"));
//                clusterOptimizationAlgorightms.add(new MLAlgorithm("K-means-2", "weka.clusterers.SimpleKMeans", "-N " + i + " -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -S 159", "clusterisation"));
//                clusterOptimizationAlgorightms.add(new MLAlgorithm("K-means-3", "weka.clusterers.SimpleKMeans", "-N " + i + " -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -S 171", "clusterisation"));
//                clusterOptimizationAlgorightms.add(new MLAlgorithm("EM-1", "weka.clusterers.EM", "-I 100 -N " + i + " -M 1.0E-6 -S 100", "clusterisation"));
//                clusterOptimizationAlgorightms.add(new MLAlgorithm("EM-2", "weka.clusterers.EM", "-I 100 -N " + i + " -M 1.0E-6 -S 201", "clusterisation"));
//                clusterOptimizationAlgorightms.add(new MLAlgorithm("EM-3", "weka.clusterers.EM", "-I 100 -N " + i + " -M 1.0E-6 -S 32", "clusterisation"));
//                clusterOptimizationAlgorightms.add(new MLAlgorithm("FarthestFirst-1", "weka.clusterers.FarthestFirst", "-N " + i + " -S 1", "clusterisation"));
//                clusterOptimizationAlgorightms.add(new MLAlgorithm("FarthestFirst-2", "weka.clusterers.FarthestFirst", "-N " + i + " -S 15", "clusterisation"));
//                clusterOptimizationAlgorightms.add(new MLAlgorithm("FarthestFirst-3", "weka.clusterers.FarthestFirst", "-N " + i + " -S 185", "clusterisation"));
//                clusterOptimizationAlgorightms.add(new MLAlgorithm("Hieracical", "weka.clusterers.HierarchicalClusterer", "-N " + i + " -L AVERAGE -P -A \"weka.core.EuclideanDistance -R first-last\"", "clusterisation"));
//
//                for (int j = 0; j < clusterOptimizationAlgorightms.size(); j++){
//                    Double val = evaluateWithCOP(d, clusterOptimizationAlgorightms.get(j));
//                    if (val > maxVals[j]) { //COP
//                        maxVals[j] = val;
//                        maxValCluster[j] = i;
//                    }
//                }
//            }
//
//            for (int i = 0; i < maxValCluster.length; i++) {
//                System.out.print(maxValCluster[i] + " ");
//            }
//
//            System.out.println("done");

                try (FileOutputStream outputStream = new FileOutputStream(resultXLS)) {
                    myWorkBook.write(outputStream);
                    outputStream.close();
                }
            }
            fis.close();
            myWorkBook.close();
        }

    }
}
