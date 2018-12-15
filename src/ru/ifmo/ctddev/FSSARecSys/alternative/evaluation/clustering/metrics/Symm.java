package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering.metrics;

import weka.clusterers.Clusterer;
import weka.core.DenseInstance;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by sergey on 06.03.16.
 */
public class Symm extends ClusterMetric {

    public Symm(ArrayList<Instances> instances, Clusterer clusterer) {
        super(instances, clusterer);
    }

    @Override
    public String getName() {
        return "Symm";
    }

    private Instance getSpecialInstance(int xi, int ck){
        Instance curr = clusters.get(ck).instance(xi);
        Instance centroid = centroids.instance(ck);
        int numOfAttributes = curr.numAttributes();

        double [] first = curr.toDoubleArray();
        double [] second = centroid.toDoubleArray();
        double [] result = new double[numOfAttributes];

        for (int i = 0; i < numOfAttributes; i++) {
            result[i] = 2 * second[i] - first[i];
        }
        return new DenseInstance(1.0, result);
    }

    private Double dps(int xi, int ck) {
        Instances cluster = clusters.get(ck);
        ArrayList<Double> dist = new ArrayList<>();

        for (int i = 0; i < cluster.numInstances(); i++) {
            Instance spec = getSpecialInstance(xi, ck);
            cluster.add(spec);
            EuclideanDistance e = new EuclideanDistance(cluster);
            Double distance = e.distance(cluster.instance(i), cluster.lastInstance());
            cluster.delete(cluster.numInstances() - 1);
            dist.add(distance);
        }
        Collections.sort(dist);
        return 0.5 * (dist.get(0) + dist.get(1));
    }

    @Override
    public Double evaluate() throws Exception {
        Double numerator = 0.0;
        EuclideanDistance e = new EuclideanDistance(centroids);
        Double maxCentrDist = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < numOfClusters; i++)
            for (int j = 0; j < numOfClusters; j++) {
                maxCentrDist = Double.max(maxCentrDist, e.distance(centroids.instance(i), centroids.instance(j)));
            }
        numerator = maxCentrDist;

        Double denominator = 0.0;
        for (int i = 0; i < numOfClusters; i++) {
            Instances currCluster = clusters.get(i);
            for (int j = 0; j < currCluster.numInstances(); j++) {
                denominator += dps(j, i);
            }
        }
        denominator *= numOfClusters;

        return numerator / denominator;
    }
}
