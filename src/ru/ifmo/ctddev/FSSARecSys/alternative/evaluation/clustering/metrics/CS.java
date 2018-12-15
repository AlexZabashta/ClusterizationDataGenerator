package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering.metrics;

import weka.clusterers.Clusterer;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Created by sergey on 06.03.16.
 */
public class CS extends ClusterMetric{

    public CS(ArrayList<Instances> instances, Clusterer clusterer) {
        super(instances, clusterer);
    }

    @Override
    public String getName() {
        return "CS";
    }

    @Override
    public Double evaluate() throws Exception {
        Double numerator = 0.0;

        for (int i = 0; i < numOfClusters; i++) {
            Instances currCluster = clusters.get(i);
            int currSize = currCluster.numInstances();
            EuclideanDistance e = new EuclideanDistance(currCluster);
            Double maxDist = Double.NEGATIVE_INFINITY;
            Double sum = 0.0;
            for (int j = 0; j < currSize - 1; j++) {
                for (int k = j; k < currSize; k++) {
                    Instance first = currCluster.instance(j);
                    Instance second = currCluster.instance(k);
                    maxDist = Double.max(maxDist, e.distance(first, second));
                }
                sum += maxDist;
            }
            sum /= currSize;
            numerator += sum;
        }

        Double denominator = 0.0;

        EuclideanDistance eCent = new EuclideanDistance(centroids);
        for (int i = 0; i < numOfClusters - 1; i++) {
            Double minVal = Double.POSITIVE_INFINITY;
            for (int j = i + 1; j < numOfClusters; j++) {
                minVal = Double.min(minVal, eCent.distance(centroids.instance(i), centroids.instance(j)));
            }
            denominator += minVal;
        }

        return numerator / denominator;
    }
}
