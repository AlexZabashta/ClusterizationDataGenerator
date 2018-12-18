package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering.metric;

import weka.clusterers.Clusterer;
import weka.core.EuclideanDistance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by sergey on 06.03.16.
 */
public class SV extends ClusterMetric {

    public SV(ArrayList<Instances> instances, Clusterer clusterer) {
        super(instances, clusterer);
    }

    @Override
    public String getName() {
        return "SV";
    }

    @Override
    public Double evaluate() throws Exception {
        Double numerator = 0.0;

        EuclideanDistance e = new EuclideanDistance(centroids);

        Double minCentrDist = Double.POSITIVE_INFINITY;
        for (int i = 0; i < numOfClusters; i++) {
            for (int j = i + 1; j < numOfClusters; j++) {
                minCentrDist = Double.min(minCentrDist, e.distance(centroids.instance(i), centroids.instance(j)));
            }
        }
        numerator = minCentrDist;

        Double denominator = 0.0;

        for (int i = 0; i < numOfClusters; i++){
            Instances currCluster = clusters.get(i);
            Double sum = 0.0;
            //Double maxToCentrDist = Double.NEGATIVE_INFINITY;

            currCluster.add(centroids.instance(i));
            EuclideanDistance ecl = new EuclideanDistance(currCluster);

            ArrayList<Double> dist = new ArrayList<>();
            for (int j = 0; j < currCluster.numInstances() - 1; j++) {
                dist.add(ecl.distance(currCluster.instance(j), currCluster.lastInstance()));
                //maxToCentrDist = Double.max(maxToCentrDist, ecl.distance(currCluster.instance(j), currCluster.lastInstance()));
            }
            currCluster.delete(currCluster.numInstances() - 1);
            Collections.sort(dist);
            Collections.reverse(dist);

            for (int j = 0; j < 0.1 * (currCluster.numInstances()); j++){
                sum += dist.get(j);
            }
            sum *= 10;
            sum /= currCluster.numInstances();
            denominator += sum;
        }

        return numerator / denominator;
    }
}
