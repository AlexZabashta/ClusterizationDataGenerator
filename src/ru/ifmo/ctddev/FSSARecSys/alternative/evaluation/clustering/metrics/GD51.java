package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering.metrics;

import weka.clusterers.Clusterer;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Created by sergey on 06.03.16.
 */
public class GD51 extends ClusterMetric {
    public GD51(ArrayList<Instances> clusters, Clusterer clusterer) {
        super(clusters, clusterer);
    }

    @Override
    public String getName() {
        return "GD51";
    }

    @Override
    public Double evaluate() throws Exception {
        //get max inter-cluster distance

        EuclideanDistance allInstancedDist = new EuclideanDistance(unitedClusters);

        Double maxTotal = Double.NEGATIVE_INFINITY;
        Double maxForCluster = Double.MIN_VALUE;
        for (int i = 0; i < numOfClusters; i++) {
            Instances currCluster = clusters.get(i);
            //maxTotal = Double.NEGATIVE_INFINITY;
            maxForCluster = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < currCluster.numInstances(); j++) {
                Instance currInstance = currCluster.instance(j);
                maxForCluster = Double.NEGATIVE_INFINITY;
                for (int k = 0; k < currCluster.numInstances(); k++) {
                    if (j != k)
                        maxForCluster = Double.max(maxForCluster,
                                allInstancedDist.distance(currInstance, currCluster.instance(k)));
                }
            }
            maxTotal = Double.max(maxTotal, maxForCluster);
        }

        //get min intra-cluster distance

        Double minIntraclusterDistance = Double.POSITIVE_INFINITY;
        Double minLocalDistance = Double.POSITIVE_INFINITY;
        for (int i = 0; i < numOfClusters - 1; i++) {
            minLocalDistance = Double.POSITIVE_INFINITY;
            for (int j = i + 1; j < numOfClusters; j++) {
                minLocalDistance = Double.min(minLocalDistance, delta5(i, j));
            }
            minIntraclusterDistance = Double.min(minIntraclusterDistance, minLocalDistance);
        }

        return minIntraclusterDistance / maxTotal;
    }
}
