package ru.ifmo.ctddev.FSSARecSys.utils;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by sergey on 07.05.16.
 */
public class RealAggregation {
    private String resultMetrics = "result_real_original.xlsx";
    private String resultAss = "partition.xlsx";

    public HSSFWorkbook myWBMetrics;
    public HSSFWorkbook myWBAsessors;
    public static ArrayList<Integer> metricsWithBestMin = new ArrayList<>();

    public List<ArrayList<Double>> metrics;
    public List<ArrayList<Double>> asessors;
    public List<Integer> avgAs;
    private Double distWorstBest;

    public ArrayList<ArrayList<Double>> adeqAVG = new ArrayList<>();
    public ArrayList<ArrayList<String>> adeqBest = new ArrayList<>();

    public ArrayList<ArrayList<Double>> rang = new ArrayList<>();
    public ArrayList<ArrayList<Double>> weightedAdeq = new ArrayList<>();

    public void setMetrics(FileInputStream myWorkBook) throws IOException {
        this.myWBMetrics = new HSSFWorkbook(myWorkBook);
    }
    public void setAsessor(FileInputStream myWorkBook) throws IOException {
        this.myWBAsessors = new HSSFWorkbook(myWorkBook);
    }

    private int distanceToBest(List<Integer> permutation, int curdistance) {
        for (int i = 1; i < permutation.size(); i++) {
            if (permutation.get(i - 1) < permutation.get(i)) {
                Collections.swap(permutation, i - 1, i);
                curdistance = distanceToBest(permutation, curdistance + 1);
                break;
            }
        }
        return curdistance;
    }

    public void sheetProcessing(int sheetNum) {
        HSSFSheet currentSheet = myWBMetrics.getSheetAt(sheetNum);
        metrics = new ArrayList<>();
        asessors = new ArrayList<>();

        Double min = Double.POSITIVE_INFINITY;

        for (int i = 1; i < 20; i++) {
            ArrayList<Double> currentMetrics = new ArrayList<>();
            for (int j = 1; j < 15; j++) {
                HSSFRow row = currentSheet.getRow(j);
                Cell cell = row.getCell(i);
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING: {
                        String infinity = "Infinity";
                        String nan = "NaN";
                        if (cell.getStringCellValue().equals(infinity)) {
                            if (i == 9)
                                currentMetrics.add(Double.NEGATIVE_INFINITY);
                            else
                                currentMetrics.add(Double.POSITIVE_INFINITY);
                        }
                        if (cell.getStringCellValue().equals(nan)) {
                            currentMetrics.add(Double.NEGATIVE_INFINITY);
                        }
                        break;
                    }
                    case Cell.CELL_TYPE_NUMERIC:
                        currentMetrics.add(cell.getNumericCellValue());
                        //System.out.print(cell.getNumericCellValue() + "\t");
                        break;
                    default:
                        ;
                }
            }
            metrics.add(currentMetrics);
        }

        for (int i = 0; i < 5; i++) {
            HSSFSheet currSheet = myWBAsessors.getSheetAt(i);
            HSSFRow row = currSheet.getRow(sheetNum + 1);
            ArrayList<Double> currentAsess = new ArrayList<>();
            for (int j = 1; j < 15; j++) {
                Cell cell = row.getCell(j);
                currentAsess.add(cell.getNumericCellValue());
//                switch (cell.getCellType()) {
//                    case Cell.CELL_TYPE_STRING: {
//                        String s = cell.getStringCellValue();
//                        currentAsess.add(Double.parseDouble(s));
//                        break;
//                    }
//                    case Cell.CELL_TYPE_NUMERIC:
//                        currentAsess.add(cell.getNumericCellValue());
//                        break;
//                    default :;
//                }
            }
            asessors.add(currentAsess);
        }

        // adequate / inadequate

        avgAs = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            int count = 0;
            for (int j = 0; j < 5; j++) {
                if (asessors.get(j).get(i) > 0)
                    count++;
            }
            if (count <= 2) {
                avgAs.add(-1);
            } else {
                avgAs.add(1);
            }

//            if (count < 2)
//                avgAs.add(-1);
//            else {
//                if (count == 2)
//                    avgAs.add(0);
//                else
//                    avgAs.add(1);
//            }
        }

        boolean allNegative = true;

        for (int k = 0; k < avgAs.size(); k++) {
            if (avgAs.get(k) == 1) {
                allNegative = false;
                System.out.print('+');
            }
            if (avgAs.get(k) == 0) {
                allNegative = false;
                System.out.print('*');
            }
            if (avgAs.get(k) == -1) System.out.print('-');
        }
        System.out.println();

        ArrayList<Integer> avgSorted = new ArrayList<>(avgAs);
        Collections.sort(avgSorted);

        ArrayList<Integer> avgSortedRev = new ArrayList<>(avgSorted);
        Collections.reverse(avgSortedRev);

        distWorstBest = (double)distanceToBest(avgSorted, 0);

        double [][] asessArrays = new double [5][14];
        double [][] metricsArrays = new double [19][14];

        for (int i = 0; i < asessArrays.length; i++) {
            for (int j = 0; j < asessArrays[i].length; j++) {
                asessArrays[i][j] = asessors.get(i).get(j);
            }
        }

        for (int i = 0; i < metricsArrays.length; i++) {
            for (int j = 0; j < metricsArrays[i].length; j++) {
                metricsArrays[i][j] = metrics.get(i).get(j);
            }
        }

        double[] wA = SoftRanking.aggregate(asessArrays);
        double[] negWA = Arrays.copyOf(wA, wA.length);

        double currMin = Double.POSITIVE_INFINITY;
        for (int j = 0; j < negWA.length; j++) {
            if(negWA[j] < currMin)
                currMin = negWA[j];
        }

        double epsilon = currMin / 1000;
        for (int j = 0; j < negWA.length -1; j++) {
            ArrayList<Integer> flag = new ArrayList<>();
            flag.add(j);
            for (int k = j + 1; k < negWA.length; k++) {
                if (negWA[j] == negWA[k])
                    flag.add(k);
            }
            if (flag.size() > 1) {
                for (int k = 0; k < flag.size(); k++) {
                    negWA[flag.get(k)] += epsilon * k;
                }
            }
        }

        for (int j = 0; j < negWA.length; j++) {
            negWA[j] *= -1.0;
        }

        double dWorstBest = SoftRanking.distance(wA, negWA);

        for (int i = 0; i < metrics.size(); i++) {

            if (allNegative){
                adeqAVG.get(i).add(1.0);
                adeqBest.get(i).add("-");
            } else {
                ArrayList<Pair<Double, Integer>> permutation = new ArrayList<>();
                List<Integer> copy = new ArrayList<>(avgAs);

                for (int j = 0; j < metrics.get(i).size(); j++) {
                    permutation.add(new Pair<Double, Integer>(metrics.get(i).get(j), copy.get(j)));
                }

                if (metricsWithBestMin.contains(i + 1)) {
                    Collections.sort(permutation, new Comparator<Pair<Double, Integer>>() {
                        @Override
                        public int compare(Pair<Double, Integer> o1, Pair<Double, Integer> o2) {
                            if ((Double) o1.first < (Double) o2.first)
                                return -1;
                            if ((Double) o1.first == (Double) o2.first) {
                                if ((Integer) o1.second < (Integer) o2.second)
                                    return 1;
                                if ((Integer) o1.second == (Integer) o2.second)
                                    return 0;
                                else return -1;
                            } else {
                                return 1;
                            }
                        }
                    });
                } else {
                    Collections.sort(permutation, new Comparator<Pair<Double, Integer>>() {
                        @Override
                        public int compare(Pair<Double, Integer> o1, Pair<Double, Integer> o2) {
                            if ((Double) o1.first < (Double) o2.first)
                                return 1;
                            if ((Double) o1.first == (Double) o2.first) {
                                if ((Integer) o1.second < (Integer) o2.second)
                                    return 1;
                                if ((Integer) o1.second == (Integer) o2.second)
                                    return 0;
                                else return -1;
                            } else {
                                return -1;
                            }
                        }
                    });
                }

                for (int k = 0; k < permutation.size(); k++) {
                    copy.set(k, permutation.get(k).second);
                }

                Integer adeq = permutation.get(0).second;

                if (adeq == 1) adeqBest.get(i).add("+");
                if (adeq == 0) adeqBest.get(i).add("*");
                if (adeq == -1) adeqBest.get(i).add("-");

                Double distCurrBest = (double)distanceToBest(copy, 0);
                Double result = distCurrBest / distWorstBest;

                adeqAVG.get(i).add(result);
            }

            // ranging

            double dBestCurr = SoftRanking.distance(wA, metricsArrays[i]);

            Double resRange = dBestCurr / dWorstBest;

            rang.get(i).add(resRange);

        }
        //System.out.println(best + 1);
    }

    public static void main(String [] args) throws IOException {
        File myFile = new File("results_real_original.xls");
        FileInputStream fis = new FileInputStream(myFile);
        File myMetric = new File("partition.xls");
        FileInputStream fms = new FileInputStream(myMetric);

        RealAggregation ma = new RealAggregation();
        ma.myWBMetrics = new HSSFWorkbook(fis);
        ma.myWBAsessors = new HSSFWorkbook(fms);
        Collections.addAll(metricsWithBestMin, 1, 5, 7, 8, 12, 13);

        for (int i = 0; i < 19; i++) {
            ma.adeqAVG.add(new ArrayList<>());
            ma.adeqBest.add(new ArrayList<>());
            ma.rang.add(new ArrayList<>());
            ma.weightedAdeq.add(new ArrayList<>());
        }

        for(int i = 0; i < ma.myWBMetrics.getNumberOfSheets(); i++) {
           System.out.println(ma.myWBMetrics.getSheetAt(i).getSheetName());
           ma.sheetProcessing(i);
            //System.out.println("========================================");
        }

        for (int i = 0; i < ma.adeqAVG.size(); i++) {
            for (int j = 0; j < ma.adeqAVG.get(i).size(); j++) {
                System.out.printf("%-20s", ma.adeqAVG.get(i).get(j));
            }
            System.out.println();
        }

        System.out.println();
        System.out.println();

        for (int i = 0; i < ma.adeqBest.size(); i++) {
            for (int j = 0; j < ma.adeqBest.get(i).size(); j++) {
                System.out.printf("%-20s", ma.adeqBest.get(i).get(j));
            }
            System.out.println();
        }

        System.out.println();
        System.out.println();

        for (int i = 0; i < ma.rang.size(); i++) {
            for (int j = 0; j < ma.rang.get(i).size(); j++) {
                System.out.printf("%-20s", ma.rang.get(i).get(j));
            }
            System.out.println();
        }

        System.out.println();
        System.out.println();

        int [][] ranger = new int[ma.rang.size()][ma.rang.get(0).size()];
        boolean [] validDatasets = new boolean[ma.rang.get(0).size()];

        for (int i = 0; i < ma.rang.get(0).size(); i++)
            validDatasets[i] = true;

        int countTotalDatasets = ma.rang.get(0).size();

        ArrayList<ArrayList<Double>> minmax = new ArrayList<>();

        for (int i = 0; i < ma.rang.get(0).size(); i++){
            HashSet<Double> minVals = new HashSet<>();
            for (int j = 0; j < ma.rang.size(); j++) {
                minVals.add(ma.rang.get(j).get(i));
            }
            ArrayList<Double> l = new ArrayList();
            l.addAll(minVals);
            Collections.sort(l);

            HashSet<Double> topFiveVals = new HashSet<>();
            for (int k = 0; k < 1; k++){
                topFiveVals.add(l.get(k));
            }
            ArrayList<Double> t = new ArrayList<>();
            t.add(l.get(0));
            t.add(l.get(2));
            minmax.add(t);

            for (int j = 0; j < ma.rang.size(); j++) {
                if (validDatasets[i]){
                    if (topFiveVals.contains(ma.rang.get(j).get(i))){
                        ranger[j][i] = 1;
                    } else {
                        ranger[j][i] = 0;
                    }
                    if (ma.rang.get(j).get(i) == l.get(0)) {
                        if (ma.adeqBest.get(j).get(i) == "+") {
                            validDatasets[i] = true;
                        } else {
                            validDatasets[i] = false;
                            for (int k = 0; k < ma.rang.size(); k++)
                                ranger[k][i] = 0;
                            countTotalDatasets--;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < minmax.get(0).size(); i++) {
            for (int j = 0; j < minmax.size(); j++) {
                System.out.printf("%-20s",  minmax.get(j).get(i));
            }
            System.out.println();
        }



        for (int i = 0; i < ma.rang.size(); i++) {
            for (int j = 0; j < ma.rang.get(i).size(); j++) {
                System.out.printf("%-20s", ranger[i][j]);
            }
            System.out.println();
        }

        System.out.println();
        System.out.println();

        ArrayList<Double> finalResults = new ArrayList<>();

        for (int i = 0; i < ma.adeqAVG.size(); i++) {
            double sum = 0;
            for (int j = 0; j < ma.adeqAVG.get(i).size(); j++) {
                sum += ranger[i][j];
            }
            finalResults.add(sum / countTotalDatasets);
            System.out.println(sum / countTotalDatasets);
        }

        System.out.println();
        System.out.println();

        ArrayList<Double> copyFinalResults = new ArrayList<>(finalResults);

        Collections.sort(copyFinalResults);
        Collections.reverse(copyFinalResults);

        HashSet<Double> rangs = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            rangs.add(copyFinalResults.get(i));
        }

        for (int i = 0; i < finalResults.size(); i++){
            if (rangs.contains(finalResults.get(i))){
                System.out.print(i + 1 + " ");
            }
        }

//        for (int i = 0; i < ma.adeqAVG.size(); i++) {
//            System.out.println(finalResults.get(i));
//        }



//        for (int i = 0; i < ma.rang.size(); i++) {
//            double sum = 0;
//            for (int j = 0; j < ma.rang.get(i).size(); j++) {
//                sum += ranger[i][j];
//            }
//            System.out.println(sum / 41.0);
//        }
//
//        System.out.println();
//        System.out.println();


        ///фигы
//
//        for (int i = 0; i < ma.myWBMetrics.getNumberOfSheets(); i++) {
//            ma.sheetProcessingExtra(i);
//        }

//        for (int i = 0; i < ma.weightedAdeq.size(); i++) {
//            for (int j = 0; j < ma.weightedAdeq.get(i).size(); j++) {
//                if (ma.weightedAdeq.get(i).get(j) == Double.NaN)
//                    ma.weightedAdeq.get(i).set(j, 1.0);
//                System.out.printf("%-20s", " " + ma.weightedAdeq.get(i).get(j));
//            }
//            System.out.println();
//        }
//
//        System.out.println();
//        System.out.println();

//        int[][] ranger = new int[ma.adeqAVG.size()][ma.adeqAVG.get(0).size()];
//
//        for (int i = 0; i < ma.adeqAVG.get(0).size(); i++){
//            HashSet<Double> minVals = new HashSet<>();
//            for (int j = 0; j < ma.adeqAVG.size(); j++) {
//                minVals.add(ma.adeqAVG.get(j).get(i));
//            }
//
//            ArrayList<Double> l = new ArrayList();
//            l.addAll(minVals);
//            Collections.sort(l);
//
//            for (int j = 0; j < ma.adeqAVG.size(); j++) {
//                //System.out.println(i + " " + j);
//
//                if (l.size() == 1) {
//                    ranger[j][i] = 0;
//                }
//                else {
//                    if (ma.adeqAVG.get(j).get(i) == l.get(0) ||
//                            ma.adeqAVG.get(j).get(i) == l.get(1) ||
//                            ma.adeqAVG.get(j).get(i) == l.get(2)) {
//                        ranger[j][i] = 1;
//                    } else {
//                        ranger[j][i] = 0;
//                    }
//                }
//            }
//        }
//
//        for (int i = 0; i < ma.adeqAVG.size(); i++) {
//            double sum = 0;
//            for (int j = 0; j < ma.adeqAVG.get(i).size(); j++) {
//                sum += ranger[i][j];
//            }
//            System.out.println(sum / 41.0);
//        }
//
//        System.out.println();
//        System.out.println();
//
//        ranger = new int[ma.rang.size()][ma.rang.get(0).size()];
//
//        for (int i = 0; i < ma.rang.get(0).size(); i++){
//            HashSet<Double> minVals = new HashSet<>();
//            for (int j = 0; j < ma.rang.size(); j++) {
//                minVals.add(ma.rang.get(j).get(i));
//            }
//
//            ArrayList<Double> l = new ArrayList();
//            l.addAll(minVals);
//            Collections.sort(l);
//
//            for (int j = 0; j < ma.rang.size(); j++) {
//                //System.out.println(i + " " + j);
//
//                if (l.size() == 1) {
//                    ranger[j][i] = 0;
//                }
//                else {
//                    if (ma.rang.get(j).get(i) == l.get(0) ||
//                            ma.rang.get(j).get(i) == l.get(1) ||
//                            ma.rang.get(j).get(i) == l.get(2)) {
//                        ranger[j][i] = 1;
//                    } else {
//                        ranger[j][i] = 0;
//                    }
//                }
//            }
//        }
//
//        for (int i = 0; i < ma.rang.size(); i++) {
//            double sum = 0;
//            for (int j = 0; j < ma.rang.get(i).size(); j++) {
//                sum += ranger[i][j];
//            }
//            System.out.println(sum / 41.0);
//        }
//
//        System.out.println();
//        System.out.println();
//
//        ranger = new int[ma.weightedAdeq.size()][ma.weightedAdeq.get(0).size()];
//
//        for (int i = 0; i < ma.weightedAdeq.get(0).size(); i++){
//            HashSet<Double> minVals = new HashSet<>();
//            for (int j = 0; j < ma.weightedAdeq.size(); j++) {
//                minVals.add(ma.weightedAdeq.get(j).get(i));
//            }
//
//            ArrayList<Double> l = new ArrayList();
//            l.addAll(minVals);
//            Collections.sort(l);
//
//            for (int j = 0; j < ma.weightedAdeq.size(); j++) {
//                //System.out.println(i + " " + j);
//
//                if (l.size() == 1) {
//                    ranger[j][i] = 0;
//                }
//                else {
//                    if (ma.weightedAdeq.get(j).get(i) == l.get(0) ||
//                            ma.weightedAdeq.get(j).get(i) == l.get(1) ||
//                            ma.weightedAdeq.get(j).get(i) == l.get(2)) {
//                        ranger[j][i] = 1;
//                    } else {
//                        ranger[j][i] = 0;
//                    }
//                }
//            }
//        }
//
//        for (int i = 0; i < ma.weightedAdeq.size(); i++) {
//            double sum = 0;
//            for (int j = 0; j < ma.weightedAdeq.get(i).size(); j++) {
//                sum += ranger[i][j];
//            }
//            System.out.println(sum / 39.0);
//        }

    }
}
