package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering;

import ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering.metric.ClustererResult;
import ru.ifmo.ctddev.FSSARecSys.alternative.internal.Clusterisation;
import ru.ifmo.ctddev.FSSARecSys.utils.DrawPicture;
import ru.ifmo.ctddev.FSSARecSys.utils.PictureManagement;
import weka.clusterers.*;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.attribute.StringToNominal;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Администратор on 12.08.2015.
 */
public class ClustererEvaluator {
    private final String name;
    private final Clusterer clusterer;

    private Clusterisation clusterisation;

    private Double DBIndex = Double.NEGATIVE_INFINITY;
    private Double DunnIndex  = Double.NEGATIVE_INFINITY;
    private Double SilhouetteIndex = Double.NEGATIVE_INFINITY;
    private Double CHIndex = Double.NEGATIVE_INFINITY;
    private Double SDbwIndex = Double.NEGATIVE_INFINITY;
    private Double SFIndex = Double.NEGATIVE_INFINITY;
    private Double CSIndex = Double.NEGATIVE_INFINITY;
    private Double COP = Double.NEGATIVE_INFINITY;
    private Double SV = Double.NEGATIVE_INFINITY;
    private Double OS = Double.NEGATIVE_INFINITY;
    private Double SymIndex = Double.NEGATIVE_INFINITY;
    private Double Gamma = Double.NEGATIVE_INFINITY;
    private Double CI = Double.NEGATIVE_INFINITY;
    private Double DaviesBouldinStarIndex = Double.NEGATIVE_INFINITY;
    private Double SymDB = Double.NEGATIVE_INFINITY;
    private Double GD31 = Double.NEGATIVE_INFINITY;
    private Double GD41 = Double.NEGATIVE_INFINITY;
    private Double GD51 = Double.NEGATIVE_INFINITY;
    private Double GD33 = Double.NEGATIVE_INFINITY;
    private Double GD43 = Double.NEGATIVE_INFINITY;
    private Double GD53 = Double.NEGATIVE_INFINITY;


    public ClustererEvaluator(String name, Clusterer clusterer) {
        this.name = Objects.requireNonNull(name);
        this.clusterer = Objects.requireNonNull(clusterer);
    }

    public String getName() {
        return name;
    }

    public Double getDBIndex() throws Exception {
        if (DBIndex == Double.NEGATIVE_INFINITY)
            return clusterisation.DaviesBouldinIndex();
        else
            return DBIndex;
    }

    public Double getDunnIndex(){
        if (DunnIndex == Double.NEGATIVE_INFINITY)
            DunnIndex = clusterisation.DunnIndex();
        return DunnIndex;
    }

    public Double getSilhouetteIndex(){
        if (SilhouetteIndex == Double.NEGATIVE_INFINITY)
            SilhouetteIndex = clusterisation.silhouetteIndex();
        return SilhouetteIndex;
    }

    public Double getCHIndex(){
        if (CHIndex == Double.NEGATIVE_INFINITY)
            CHIndex =  clusterisation.CalinskiHarabaszIndex();
        return CHIndex;
    }

    public Double  getSDbw() throws Exception {
        if (SDbwIndex == Double.NEGATIVE_INFINITY)
            SDbwIndex = clusterisation.SDbw();
        return SDbwIndex;
    }

    public Double getSF(){
        if (SFIndex ==  Double.NEGATIVE_INFINITY)
            SFIndex = clusterisation.ScoreFunction();
        return SFIndex;
    }

    public Double getCS() throws Exception{
        if (CSIndex == Double.NEGATIVE_INFINITY)
            CSIndex = clusterisation.CS();
        return CSIndex;
    }

    public Double getCOP(){
        if (COP == Double.NEGATIVE_INFINITY)
            COP = clusterisation.COP();
        return COP;
    }

    public Double getSV(){
        if (SV == Double.NEGATIVE_INFINITY)
            SV = clusterisation.SV();
        return SV;
    }

    public Double getOS() {
        if (OS == Double.NEGATIVE_INFINITY)
            OS = clusterisation.OS();
        return OS;
    }

    public Double getSymIndex() {
        if (SymIndex == Double.NEGATIVE_INFINITY)
            SymIndex = clusterisation.SymIndex();
        return SymIndex;
    }

    public Double getGamma() {
        if (Gamma == Double.NEGATIVE_INFINITY)
            Gamma = clusterisation.Gamma();
        return Gamma;
    }

    public Double getCI() {
        if (CI == Double.NEGATIVE_INFINITY)
            CI = clusterisation.CI();
        return CI;
    }

    public Double getDaviesBouldinStarIndex() throws Exception {
        if (DaviesBouldinStarIndex == Double.NEGATIVE_INFINITY)
            DaviesBouldinStarIndex = clusterisation.DaviesBouldinStarIndex();
        return DaviesBouldinStarIndex;
    }

    public Double getSymDB() throws Exception {
        if (SymDB == Double.NEGATIVE_INFINITY)
            SymDB = clusterisation.SymDB();
        return SymDB;
    }

    public Double getGD31() {
        if (GD31 == Double.NEGATIVE_INFINITY)
            GD31 = clusterisation.gD31();
        return GD31;
    }

    public Double getGD41() {
        if (GD41 == Double.NEGATIVE_INFINITY)
            GD41 = clusterisation.gD41();
        return GD41;
    }

    public Double getGD51() {
        if (GD51 == Double.NEGATIVE_INFINITY)
            GD51 = clusterisation.gD51();
        return GD51;
    }

    public Double getGD33() {
        if (GD33 == Double.NEGATIVE_INFINITY)
            GD33 = clusterisation.gD33();
        return GD33;
    }

    public Double getGD43() {
        if (GD43 == Double.NEGATIVE_INFINITY)
            GD43 = clusterisation.gD43();
        return GD43;
    }

    public Double getGD53() {
        if (GD53 == Double.NEGATIVE_INFINITY)
            GD53 = clusterisation.gD53();
        return GD53;
    }


    public Double getCombinedMetric() throws Exception {
        return Math.sqrt(Math.pow(getDBIndex(), 2.0) + Math.pow((1 / getDunnIndex()), 2.0) + Math.pow((1 / getSilhouetteIndex()), 2.0));
    }

    public ClustererResult evaluate(String datasetName, Instances data) {

        try {
            //Clusterer abstractClusterer = AbstractClusterer.makeCopy(clusterer);
            ReplaceMissingValues repl = new ReplaceMissingValues();
            repl.setInputFormat(data);
            data = Filter.useFilter(data, repl);

            StringToNominal s2n = new StringToNominal();
            s2n.setInputFormat(data);
            data = Filter.useFilter(data, s2n);

            NominalToBinary n2b = new NominalToBinary();
            n2b.setInputFormat(data);
            data = Filter.useFilter(data, n2b);


            clusterer.buildClusterer(data);

            ClusterEvaluation eval = new ClusterEvaluation();
            eval.setClusterer(clusterer);
            eval.getClusterAssignments();
            eval.evaluateClusterer(new Instances(data));
            double[] tmpDistribution = eval.getClusterAssignments();
            int[] clusterAssignments = new int[data.numInstances()];

//            Double xmax = Double.NEGATIVE_INFINITY;
//            Double ymax = Double.NEGATIVE_INFINITY;
            boolean succ = false;
            for (int i = 0; i < data.numInstances(); i++) {
                clusterAssignments[i] = (int) tmpDistribution[i];
                if (tmpDistribution[i] != -1) succ = true;
//                xmax = Double.max(xmax, data.instance(i).value(0));
//                ymax = Double.max(ymax, data.instance(i).value(1));
            }

            int numClusters = succ ? eval.getNumClusters() : 1;

//            String path = "";
//            File file = new File("/home/sergey/anew_pictures/" + datasetName);
//            if (!file.exists()) {
//                file.mkdir();
//            }
//            path = "/home/sergey/anew_pictures/" + datasetName + "/" +
//                    datasetName + "_" + name + ".png";
//
//            DrawPicture.setDataSet(data);
//            DrawPicture.setCapacities(clusterAssignments);
//            DrawPicture.drawSimplePicture(520, 520, path);

//            PictureManagement pm = new PictureManagement(numClusters, data, clusterAssignments);
//            pm.DrawPicture(datasetName, name, xmax.intValue() + 50, ymax.intValue() + 70);

            // Instances in = new Instances()
            ArrayList<Instances> clusters = new ArrayList<>(numClusters);

            for (int i = 0; i < numClusters; i++)
                clusters.add(new Instances(data, 0));

            for (int i = 0; i < data.numInstances(); i++) {
                if (succ) {
                    if (clusterAssignments[i] != -1)
                        clusters.get(clusterAssignments[i]).add(data.instance(i));
                } else {
                    clusters.get(0).add(data.instance(i));
                }

            }

            clusterisation = new Clusterisation(clusters, clusterer);

            //DrawPicture.setDataSet(data);

//            DBIndex = clusterisation.DaviesBouldinIndex();
//            DunnIndex = clusterisation.DunnIndex();
//            SilhouetteIndex = clusterisation.silhouetteIndex();

//            System.out.println("Davies-Bouldin [min]: " + getDBIndex());
//            System.out.println("Dunn index [max]: " + getD-XX:+AggressiveOptsunnIndex());
//            System.out.println("Silhouette [max]: " + getSilhouetteIndex());
//            System.out.println("Calinski-Harabasz [min]: " + getCHIndex());
//            System.out.println("SDbw [min]: " + getSDbw());
//            System.out.println("Score func [max]: " + getSF());
//            System.out.println("Combination index (new) [min]: " + getCombinedMetric());

            return new ClustererResult(0.0, 0.0, 0.0);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
