package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.*;

import javafx.util.Pair;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import org.apache.commons.lang3.StringUtils;
import ru.ifmo.ctddev.FSSARecSys.db.internal.MLAlgorithm;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.neural.NeuralNode;
import weka.classifiers.lazy.IBk;
import weka.classifiers.mi.MINND;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.core.neighboursearch.CoverTree;
import weka.core.neighboursearch.KDTree;
import weka.core.neighboursearch.LinearNNSearch;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.instance.Normalize;

/**
 * Created by sergey on 04.05.16.
 */
public class MetricsClassifier {
    private static ArrayList<ArrayList<Double>> table;
    private static MetaFeatureContainer mfContainer;

    public static ArrayList<Pair<String, Integer>> metricNames = new ArrayList<>();
    public static HashMap<String, Integer> numberByNameMetric = new HashMap<>();
    public static ArrayList<String> labelAR = new ArrayList<>();
    public static ArrayList<ArrayList<String>> labelsBA = new ArrayList<>();

    public static void writeFiles() throws Exception {
        mfContainer = new MetaFeatureContainer();
        //mfContainer.buildMetaFeatures();

        table = new ArrayList<>();

        File file = new File("metafeatures.xls");
        FileInputStream fisMf = new FileInputStream(file);
        HSSFWorkbook mftable = new HSSFWorkbook(fisMf);
        HSSFSheet sheet = mftable.getSheetAt(0);

        File fmetr = new File("results_real_original.xls");
        FileInputStream fisMetr = new FileInputStream(fmetr);
        HSSFWorkbook metrTable = new HSSFWorkbook(fisMetr);

        for (int i = 0; i < 200; i++){
            System.out.println(i);
            Row r = sheet.getRow(i);
            table.add(new ArrayList<>());
//            for (int j = 0; j < 19; j++)
//                table.get(i).add(r.getCell(j).getNumericCellValue());

            HSSFSheet currMetrSheet = metrTable.getSheetAt(i);
            for (int j = 1; j < 15; j++) {
                System.out.print(" " + j + " ");
                if (j != 11) {
                    Row currR = currMetrSheet.getRow(j);
                    for (int k = 1; k < 20; k++){
                        if (k == 10 || k == 11|| k == 15 || k == 17 || k == 18)
                        {
                            System.out.print(" " + k + " ");
                            double val = 0.0;
                            switch (currR.getCell(k).getCellType()) {
                                case Cell.CELL_TYPE_STRING:
                                    val = Double.parseDouble(currR.getCell(k).getStringCellValue());
                                    break;
                                case Cell.CELL_TYPE_NUMERIC:
                                    val = currR.getCell(k).getNumericCellValue();
                                    break;
                            }
                            table.get(i).add(val);
                        }

                    }
                }
                System.out.println();
            }
        }

        //table = mfContainer.getTable();

        for (int i = 0; i < table.size(); i++) {
            for (int j = 0; j < table.get(i).size(); j++) {
                System.out.printf("%-20s", table.get(i).get(j) + " ");
            }
            System.out.println();
        }

        //add best metrics
        //get array of best for every

        //10 11 15 17 18
        File f = new File("aggregation.xls");
        FileInputStream fis = new FileInputStream(f);
        HSSFWorkbook ranks = new HSSFWorkbook(fis);
        HSSFSheet baSheet = ranks.getSheet("BA");
        HSSFSheet arSheet = ranks.getSheet("AR");

        ArrayList<Row> baRows = new ArrayList<>();

        baRows.add(baSheet.getRow(10));
        baRows.add(baSheet.getRow(11));
        baRows.add(baSheet.getRow(15));
        baRows.add(baSheet.getRow(17));
        baRows.add(baSheet.getRow(18));

        ArrayList<Row> arRows = new ArrayList<>();

        arRows.add(arSheet.getRow(10));
        arRows.add(arSheet.getRow(11));
        arRows.add(arSheet.getRow(15));
        arRows.add(arSheet.getRow(17));
        arRows.add(arSheet.getRow(18));

        metricNames.add(new Pair<String, Integer>("OS", 10));
        metricNames.add(new Pair<String, Integer>("SymIndex", 11));
        metricNames.add(new Pair<String, Integer>("GD41", 15));
        metricNames.add(new Pair<String, Integer>("GD33", 17));
        metricNames.add(new Pair<String, Integer>("GD43", 18));

        numberByNameMetric.put("OS", 10);
        numberByNameMetric.put("SymIndex", 11);
        numberByNameMetric.put("GD41", 15);
        numberByNameMetric.put("GD33", 17);
        numberByNameMetric.put("GD43", 18);


        for (int j = 0; j < 5; j++) {
            labelsBA.add(new ArrayList<>());
        }

        for(int i = 0; i < table.size(); i++) {
            System.out.println(i + 1);
            for (int j = 0; j < labelsBA.size(); j++) {
                labelsBA.get(j).add(baRows.get(j).getCell(i + 1).getStringCellValue());
            }

            ArrayList<Double> tmpMax = new ArrayList<>();
            for (int j = 0; j < arRows.size(); j++){
                tmpMax.add(arRows.get(j).getCell(i + 1).getNumericCellValue());
            }
            double max = Collections.max(tmpMax);
            for (int j = 0; j < 5; j++) {
                if (tmpMax.get(j) == max){
                    labelAR.add(metricNames.get(j).getKey());
                }
            }

        }

        PrintWriter writer = new PrintWriter("bestMetric.arff", "UTF-8");
        writer.println("@relation bestmetric");
        writer.println();
        writer.println("@attribute class {OS, SymIndex, GD41, GD33, GD43}"); //range of labels
        for (int i = 1; i <= table.get(0).size(); i++) {
            writer.println("@attribute MD" + i + " REAL");
        }
        writer.println();
        writer.println("@data");

        for (int i = 0; i < table.size(); i++){
            writer.println(labelAR.get(i) + "," + StringUtils.join(table.get(i), ','));
        }
        writer.close();

        //add adequacy first
        //add adequacy second
        //add adequacy third
        //add adequacy fourth
        //add adequacy fifth

        for (int j = 1; j <= 5; j++) {
            writer = new PrintWriter("adequacy" + j +".arff", "UTF-8");
            writer.println("@relation adequacy" + j);
            writer.println();
            writer.println("@attribute class {+,-}");
            for (int i = 1; i <= table.get(0).size(); i++) {
                writer.println("@attribute MD" + i + " numeric");
            }
            writer.println();
            writer.println("@data");

            for (int i = 0; i < table.size(); i++){
                writer.println(labelsBA.get(j - 1).get(i) + "," + StringUtils.join(table.get(i), ','));
            }
            writer.close();
        }
    }


    public static void main(String [] args) throws Exception {
        writeFiles();

        Instances instancesMetrics = null;
        try {
            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource("bestMetric.arff");

            instancesMetrics = dataSource.getDataSet();

        } catch (Exception e) {
            e.printStackTrace();
        }
        instancesMetrics.setClassIndex(0);

//        AttributeSelection filter = new AttributeSelection();  // package weka.filters.supervised.attribute!
//        CfsSubsetEval ev = new CfsSubsetEval();
//        GreedyStepwise search = new GreedyStepwise();
//        search.setSearchBackwards(true);
//        filter.setEvaluator(ev);
//        filter.setSearch(search);
//        filter.setInputFormat(instancesMetrics);

        //KNN
        IBk knn = new IBk();
       // knn.setMeanSquared(true);
        knn.setNearestNeighbourSearchAlgorithm(new LinearNNSearch());
        //knn.setNearestNeighbourSearchAlgorithm();
        knn.buildClassifier(instancesMetrics);

        //
        BayesNet bn = new BayesNet();
      //  bn.setOptions(weka.core.Utils.splitOptions("-D -Q weka.classifiers.bayes.net.search.local.GeneticSearch -- -L 4 -A 4 -U 200 -R 1 -M -C -O -S BAYES -E weka.classifiers.bayes.net.estimate.BMAEstimator -- -A 0.5"));
        bn.setDebug(true);
        bn.buildClassifier(instancesMetrics);

        weka.classifiers.bayes.NaiveBayes nb = new NaiveBayes();
        nb.setUseSupervisedDiscretization(true);
        nb.buildClassifier(instancesMetrics);

        weka.classifiers.trees.RandomForest randomForest = new RandomForest();
        //randomForest.setNumFeatures(19);
        randomForest.setNumTrees(200);
        randomForest.buildClassifier(instancesMetrics);

        //MLP
        MultilayerPerceptron rf = new MultilayerPerceptron();
        rf.buildClassifier(instancesMetrics);

        //J48
        weka.classifiers.trees.J48 j48 = new J48();
        j48.buildClassifier(instancesMetrics);
        j48.setBinarySplits(true);
        //j48.setReducedErrorPruning(true);


        Evaluation eval = new Evaluation(instancesMetrics);
        eval.crossValidateModel(bn, instancesMetrics, 200, new Random(1));
        System.out.print(eval.toSummaryString());

        //smile.classification.NeuralNetwork ml = new smile.classification.NeuralNetwork()
        //NeuralNetwork nn = new NeuralNetwork(new NNParams());
        //nn.bu
        //Neural Network
        //NeuralNetwork

        ///best clusterer


        //get dataset names
        ArrayList<String> datasetNames = new ArrayList<>();
        HashMap<String, ArrayList<Integer>> clusterNums = new HashMap<>();
        Scanner input = new Scanner(new File("clusterDistr"));
        while (input.hasNext()){
            String nextLine = input.nextLine();
            String[] tokenize = nextLine.split(" ");
            datasetNames.add(tokenize[1]);

            ArrayList<Integer> tmp = new ArrayList<>();
            for(int i = 2; i < 12; i++){
                tmp.add(Integer.parseInt(tokenize[i]));
            }
            clusterNums.put(tokenize[1], tmp);
        }
        input.close();

        File myFile = new File("results_real_original.xls");
        FileInputStream fis = new FileInputStream(myFile);
//
//        //get cluster names
        ArrayList<String> clusterers = new ArrayList<>();
        for (int i = 0; i < 3; i++){
            clusterers.add("K-means");
        }
        for (int i = 0; i < 3; i++){
            clusterers.add("EM");
        }
        for (int i = 0; i < 3; i++){
            clusterers.add("FarthestFirst");
        }
        clusterers.add("Hieracical");
        clusterers.add("DBSCAN");
        for (int i = 0; i < 3; i++){
            clusterers.add("X-Means");
        }

//        //parse xls for partition evaluations
        HSSFWorkbook myWorkBook = new HSSFWorkbook(fis);
        Set<Integer> metricsWithBestMin = new HashSet<>();
        Collections.addAll(metricsWithBestMin, 1, 5, 9, 10, 12, 13);

         //= 7; //get the value of best metric;

        ArrayList<String> labels = new ArrayList<>();
        for (int i = 0; i < datasetNames.size(); i++) {
            String name = datasetNames.get(i);
            System.out.println(i + " " +name);

            HSSFSheet sheet = myWorkBook.getSheetAt(i);

            int bestMetric = numberByNameMetric.get(labelAR.get(i));

            double bestPartitionValue = metricsWithBestMin.contains(bestMetric)? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY ;
            int bestClusterer = 0;

            for (int j = 1; j < 15; j++){
                Row s = sheet.getRow(j);
                Cell c = s.getCell(bestMetric); //maybe should change
                if (c != null) {
                    if (c.getCellType() == Cell.CELL_TYPE_STRING){
                        if (c.getStringCellValue() == "Infinity"){
                            if (metricsWithBestMin.contains(bestMetric)) {
                                bestPartitionValue = Double.NEGATIVE_INFINITY;
                            } else {
                                bestPartitionValue = Double.POSITIVE_INFINITY;
                            }
                            bestClusterer = j - 1;
                        }
                    }
                    if (c.getCellType() == Cell.CELL_TYPE_NUMERIC){
                        double val = c.getNumericCellValue();
                        if (metricsWithBestMin.contains(bestMetric)) {
                            if (val < bestPartitionValue) {
                                bestPartitionValue = val;
                                bestClusterer = j - 1;
                            }
                        } else {
                            if (val > bestPartitionValue) {
                                bestPartitionValue = val;
                                bestClusterer = j - 1;
                            }
                        }
                    }
                }
            }
            labels.add(clusterers.get(bestClusterer));

////            ArrayList<Integer> clAmmount = clusterNums.get(sheet.getSheetName());
////            ArrayList<MLAlgorithm> algorithms = new ArrayList<>();
////
////            algorithms.add(new MLAlgorithm("K-means-1", "weka.clusterers.SimpleKMeans", "-N " + clAmmount.get(0) + " -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -S 10", "clusterisation"));
////            algorithms.add(new MLAlgorithm("K-means-2", "weka.clusterers.SimpleKMeans", "-N " + clAmmount.get(1) + " -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -S 159", "clusterisation"));
////            algorithms.add(new MLAlgorithm("K-means-3", "weka.clusterers.SimpleKMeans", "-N " + clAmmount.get(2) + " -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -S 171", "clusterisation"));
////            algorithms.add(new MLAlgorithm("EM-1", "weka.clusterers.EM", "-I 100 -N " + clAmmount.get(3) + " -M 1.0E-6 -S 100", "clusterisation"));
////            algorithms.add(new MLAlgorithm("EM-2", "weka.clusterers.EM", "-I 100 -N " + clAmmount.get(4) + " -M 1.0E-6 -S 201", "clusterisation"));
////            algorithms.add(new MLAlgorithm("EM-3", "weka.clusterers.EM", "-I 100 -N " + clAmmount.get(5) + " -M 1.0E-6 -S 32", "clusterisation"));
////            algorithms.add(new MLAlgorithm("FarthestFirst-1", "weka.clusterers.FarthestFirst", "-N " + clAmmount.get(6) + " -S 1", "clusterisation"));
////            algorithms.add(new MLAlgorithm("FarthestFirst-2", "weka.clusterers.FarthestFirst", "-N " + clAmmount.get(7) + " -S 15", "clusterisation"));
////            algorithms.add(new MLAlgorithm("FarthestFirst-3", "weka.clusterers.FarthestFirst", "-N " + clAmmount.get(8) + " -S 185", "clusterisation"));
////            algorithms.add(new MLAlgorithm("Hieracical", "weka.clusterers.HierarchicalClusterer", "-N " + clAmmount.get(9) + " -L AVERAGE -P -A \"weka.core.EuclideanDistance -R first-last\"", "clusterisation"));
////            algorithms.add(new MLAlgorithm("DBSCAN", "weka.clusterers.DBSCAN", "-E 0.1 -M 6 -I weka.clusterers.forOPTICSAndDBScan.Databases.SequentialDatabase -D weka.clusterers.forOPTICSAndDBScan.DataObjects.EuclideanDataObject", "clusterisation"));
////            algorithms.add(new MLAlgorithm("X-Means-1", "weka.clusterers.XMeans", "-I 1 -M 1000 -J 1000 -L 2 -H 20 -B 1.0 -C 0.5 -D \"weka.core.EuclideanDistance -R first-last\" -S 10", "clusterisation"));
////            algorithms.add(new MLAlgorithm("X-Means-2", "weka.clusterers.XMeans", "-I 1 -M 1000 -J 1000 -L 2 -H 20 -B 1.0 -C 0.5 -D \"weka.core.EuclideanDistance -R first-last\" -S 102", "clusterisation"));
////            algorithms.add(new MLAlgorithm("X-Means-3", "weka.clusterers.XMeans", "-I 1 -M 1000 -J 1000 -L 2 -H 20 -B 1.0 -C 0.5 -D \"weka.core.EuclideanDistance -R first-last\" -S 204", "clusterisation"));
        }
//
//
        PrintWriter writer = new PrintWriter("bestClusterer.arff", "UTF-8");
        writer.println("@relation bestmetric");
        writer.println();
        writer.println("@attribute class {K-means, X-Means, EM, DBSCAN, FarthestFirst, Hieracical}"); //range of labels
        for (int i = 1; i <= table.get(0).size(); i++) {
            writer.println("@attribute MD" + i + " REAL");
        }
        writer.println();
        writer.println("@data");

        for (int i = 0; i < table.size(); i++){
            writer.println(labels.get(i) + "," + StringUtils.join(table.get(i), ','));
        }
        writer.close();

        try {
            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource("bestClusterer.arff");

            instancesMetrics = dataSource.getDataSet();

        } catch (Exception e) {
            e.printStackTrace();
        }
        instancesMetrics.setClassIndex(0);

        knn = new IBk();
        // knn.setMeanSquared(true);
        knn.setNearestNeighbourSearchAlgorithm(new LinearNNSearch());
        //knn.setNearestNeighbourSearchAlgorithm();
        knn.buildClassifier(instancesMetrics);

//        eval = new Evaluation(instancesMetrics);
//        eval.crossValidateModel(bn, instancesMetrics, 200, new Random(1));
//        double [][] cm = eval.confusionMatrix();
//
//        for(int i = 0; i< cm.length; i++){
//            for(int j = 0; j < cm.length; j++){
//                System.out.print(cm[i][j] + " ");
//            }
//            System.out.println();
//        }

        System.out.print(eval.toSummaryString());

        rf = new MultilayerPerceptron();
        rf.buildClassifier(instancesMetrics);

        System.out.println();

        eval = new Evaluation(instancesMetrics);
        eval.crossValidateModel(bn, instancesMetrics, 200, new Random(1));
        System.out.print(eval.toSummaryString());

        //FSS Algorithms


    }
}
