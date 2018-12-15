package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering.metrics;

import weka.clusterers.Clusterer;
import weka.core.EuclideanDistance;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Created by sergey on 06.03.16.
 */
public class GD53 extends ClusterMetric {
    public GD53(ArrayList<Instances> clusters, Clusterer clusterer) {
        super(clusters, clusterer);
    }

    @Override
    public String getName() {
        return "GD53";
    }

    @Override
    public Double evaluate() throws Exception {

        Double maxTotal = Double.NEGATIVE_INFINITY;
        Double maxForCluster = Double.MIN_VALUE;
        for (int i = 0; i < numOfClusters; i++) {
            Instances currCluster = clusters.get(i);
            Double result;

            int size1 = currCluster.numInstances();
            currCluster.add(centroids.instance(i));
            EuclideanDistance d1 = new EuclideanDistance(currCluster);
            Double sumTmp = 0.0;
            for (int j = 0; j < size1; j++) {
                sumTmp += d1.distance(currCluster.instance(i), currCluster.lastInstance());
            }
            currCluster.delete(currCluster.numInstances() - 1);
            sumTmp *= 2;
            sumTmp /=  size1;
            maxTotal = Double.max(maxTotal, sumTmp);
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
