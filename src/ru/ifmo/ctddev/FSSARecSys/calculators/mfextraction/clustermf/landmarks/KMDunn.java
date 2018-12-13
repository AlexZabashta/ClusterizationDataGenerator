package ru.ifmo.ctddev.FSSARecSys.calculators.mfextraction.clustermf.landmarks;

import ru.ifmo.ctddev.FSSARecSys.alternative.internal.Clusterisation;
import ru.ifmo.ctddev.FSSARecSys.calculators.mfextraction.MetaFeatureExtractor;
import ru.ifmo.ctddev.FSSARecSys.db.DataSet;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Created by sergey on 01.03.16.
 */
public class KMDunn extends MetaFeatureExtractor {
    @Override
    public String getName() {
        return "KM-Dunn-LandMark";
    }

    @Override
    public double extract(DataSet dataSet) throws Exception {
        Instances data = dataSet.getInstances();

        SimpleKMeans clusterer = new SimpleKMeans();
        clusterer.setOptions(weka.core.Utils.splitOptions("\"-N 5 -A \\\"weka.core.EuclideanDistance -R first-last\\\" -I 500 -S 10\", \"clusterisation\""));

        try {
            clusterer.buildClusterer(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ClusterEvaluation eval = new ClusterEvaluation();
        eval.setClusterer(clusterer);
        eval.getClusterAssignments();
        try {
            eval.evaluateClusterer(new Instances(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
        double[] tmpDistribution = eval.getClusterAssignments();
        int[] clusterAssignments = new int[data.numInstances()];
        int numClusters = clusterAssignments.length;

        ArrayList<Instances> clusters = new ArrayList<>(numClusters);

        for (int i = 0; i < numClusters; i++)
            clusters.add(new Instances(data, 0));

        for (int i = 0; i < data.numInstances(); i++) {
            if (clusterAssignments[i] != -1)
                clusters.get(clusterAssignments[i]).add(data.instance(i));
        }

        Clusterisation cl = new Clusterisation(clusters, clusterer);

        return cl.DunnIndex();
    }
}
