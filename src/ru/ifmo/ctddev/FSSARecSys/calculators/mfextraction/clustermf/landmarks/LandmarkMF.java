package ru.ifmo.ctddev.FSSARecSys.calculators.mfextraction.clustermf.landmarks;

import ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering.metric.ClusterMetric;
import ru.ifmo.ctddev.FSSARecSys.alternative.internal.Clusterisation;
import ru.ifmo.ctddev.FSSARecSys.db.internal.MLAlgorithm;
import weka.clusterers.AbstractClusterer;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

import java.util.ArrayList;

import mfextraction.MetaFeatureExtractor;

/**
 * Created by sergey on 02.03.16.
 */
public class LandmarkMF extends MetaFeatureExtractor {

    private MLAlgorithm mlAlgorithm;
    private ClusterMetric clusterMetric;

    public void setParams(MLAlgorithm mlAlgorithm, ClusterMetric clMetric){
        this.mlAlgorithm = mlAlgorithm;
        this.clusterMetric = clMetric;
    }

    @Override
    public String getName() {
        return mlAlgorithm.getName() + " " +  clusterMetric.getName();
    }

    @Override
    public double extract(Instances instances) throws Exception {
//        Instances data = dataSet.getInstances();
//
//        Clusterer clusterer = null;
//        try {
//            clusterer = AbstractClusterer.forName(mlAlgorithm.getClassPath(), weka.core.Utils.splitOptions(mlAlgorithm.getOptions()));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            clusterer.buildClusterer(data);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        ClusterEvaluation eval = new ClusterEvaluation();
//        eval.setClusterer(clusterer);
//        eval.getClusterAssignments();
//        try {
//            eval.evaluateClusterer(new Instances(data));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        double[] tmpDistribution = eval.getClusterAssignments();
//        int[] clusterAssignments = new int[data.numInstances()];
//        int numClusters = clusterAssignments.length;
//
//        ArrayList<Instances> clusters = new ArrayList<>(numClusters);
//
//        for (int i = 0; i < numClusters; i++)
//            clusters.add(new Instances(data, 0));
//
//        for (int i = 0; i < data.numInstances(); i++) {
//            if (clusterAssignments[i] != -1)
//                clusters.get(clusterAssignments[i]).add(data.instance(i));
//        }
//
//        //clusterMetric.
//
//        //Clusterisation cl = new Clusterisation(clusters, clusterer);

        return clusterMetric.evaluate();
    }
}
