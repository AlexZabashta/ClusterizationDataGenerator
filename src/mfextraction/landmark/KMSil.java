package mfextraction.landmark;

import java.util.List;

import mfextraction.Landmark;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by sergey on 01.03.16.
 */
public class KMSil extends Landmark {
    @Override
    public String getName() {
        return null;
    }

    private double avgInterClusterDistance(int instanceIndex, Instances cluster) {
        Instance current = cluster.instance(instanceIndex);
        EuclideanDistance ed = new EuclideanDistance(cluster);
        double result = 0.0;
        for (int i = 0; i < cluster.numInstances(); i++) {
            if (instanceIndex != i)
                result += ed.distance(current, cluster.instance(i));
        }
        return result / cluster.numInstances();
    }

    private double avgIntraClusterDistace(int clusterIndex, Instance x, Instances unitedClusters, List<Instances> clusters) {
        EuclideanDistance ed = new EuclideanDistance(unitedClusters);

        double avgDistance = 0.0;
        double minVal = Double.MAX_VALUE;
        for (int i = 0; i < clusters.size(); i++) {
            avgDistance = 0.0;
            if (i != clusterIndex) {
                Instances currentCluster = clusters.get(i);
                for (int j = 0; j < currentCluster.numInstances(); j++) {
                    Instance currentInstance = currentCluster.instance(j);
                    avgDistance += ed.distance(x, currentInstance);
                }
                minVal = Double.min(minVal, avgDistance);
            }
        }
        return minVal;
    }

    @Override
    public double evaluate(int numOfClusters, Instances unitedClusters, Instance datasetCentroid, List<Instances> clusters, Instances centroids) throws Exception {
        double result = 0.0;
        double silhoetteOfInstanse = 0.0;
        for (int i = 0; i < numOfClusters; i++) {
            Instances currentCluster = clusters.get(i);
            silhoetteOfInstanse = 0.0;
            for (int j = 0; j < currentCluster.numInstances(); j++) {
                double a = avgInterClusterDistance(j, currentCluster);
                double b = avgIntraClusterDistace(i, currentCluster.instance(j), unitedClusters, clusters);
                silhoetteOfInstanse += (b - a) / Double.max(a, b);
            }
            result += silhoetteOfInstanse / currentCluster.numInstances();
        }

        return result / numOfClusters;
    }
}
