package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering.metrics;

import weka.clusterers.Clusterer;
import weka.core.EuclideanDistance;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Created by sergey on 06.03.16.
 */
public class COP extends ClusterMetric{

    public COP(ArrayList<Instances> instances, Clusterer clusterer) {
        super(instances, clusterer);
    }

    @Override
    public String getName() {
        return "COP";
    }

    @Override
    public Double evaluate() throws Exception {
        Double result = 0.0;
        EuclideanDistance eclAll = new EuclideanDistance(unitedClusters);

        for (int i = 0; i < numOfClusters; i++){

            Double numerator = 0.0;

            Instances currCluster = clusters.get(i);
            currCluster.add(centroids.instance(i));
            Double sum = 0.0;
            EuclideanDistance e = new EuclideanDistance(currCluster);
            for (int j = 0; j < currCluster.numInstances() - 1; j++) {
                sum += e.distance(currCluster.instance(j), currCluster.lastInstance());
            }
            sum /= (currCluster.numInstances() - 1);
            currCluster.delete(currCluster.numInstances() - 1);
            numerator = sum;

            Double denominator = 0.0;

            Double minDist = Double.POSITIVE_INFINITY;
            for (int j = 0; j < numOfClusters; j++) {
                if (i != j) {
                    Instances comparedCluster = clusters.get(j);
                    for (int k = 0; k < comparedCluster.numInstances(); k++) {
                        Double maxDist = Double.NEGATIVE_INFINITY;
                        for (int p = 0; p < currCluster.numInstances(); p++)
                            maxDist = Double.max(maxDist, eclAll.distance(comparedCluster. instance(k), currCluster.instance(p)));
                        minDist = Double.min(minDist, maxDist);
                    }
                }
            }
            denominator = minDist;
            result += (numerator / denominator) * currCluster.numInstances();
        }
        return result / unitedClusters.numInstances();
    }
}
