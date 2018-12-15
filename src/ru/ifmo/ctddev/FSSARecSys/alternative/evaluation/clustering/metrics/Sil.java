package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering.metrics;

import weka.clusterers.Clusterer;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Created by sergey on 06.03.16.
 */
public class Sil extends ClusterMetric {

    public Sil(ArrayList<Instances> instances, Clusterer clusterer) {
        super(instances, clusterer);
    }

    @Override
    public String getName() {
        return "Sil";
    }

    private Double avgInterClusterDistance(int instanceIndex, Instances cluster) {
        Instance current = cluster.instance(instanceIndex);
        EuclideanDistance ed = new EuclideanDistance(cluster);
        Double result = 0.0;
        for (int i = 0; i < cluster.numInstances(); i++) {
            if (instanceIndex != i)
                result += ed.distance(current, cluster.instance(i));
        }
        return result / cluster.numInstances();
    }

    private Double avgIntraClusterDistace(int clusterIndex, Instance x) {
        EuclideanDistance ed = new EuclideanDistance(unitedClusters);

        Double avgDistance = 0.0;
        Double minVal = Double.MAX_VALUE;
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
    public Double evaluate() throws Exception {
        Double result = 0.0;
        Double silhoetteOfInstanse = 0.0;
        for (int i = 0; i < numOfClusters; i++) {
            Instances currentCluster = clusters.get(i);
            silhoetteOfInstanse = 0.0;
            for (int j = 0; j < currentCluster.numInstances(); j++) {
                Double a = avgInterClusterDistance(j, currentCluster);
                Double b = avgIntraClusterDistace(i, currentCluster.instance(j));
                silhoetteOfInstanse += (b - a) / Double.max(a, b);
            }
            result += silhoetteOfInstanse / currentCluster.numInstances();
        }

        return result / numOfClusters;
    }
}
