package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering.metric;

import weka.clusterers.Clusterer;
import weka.core.EuclideanDistance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by sergey on 06.03.16.
 */
public class OS extends ClusterMetric{

    public OS(ArrayList<Instances> instances, Clusterer clusterer) {
        super(instances, clusterer);
    }

    @Override
    public String getName() {
        return "OS";
    }

    private Double aOS(int xi, int ck){
        Instances cluster = clusters.get(ck);
        EuclideanDistance e = new EuclideanDistance(cluster);
        Double sum = 0.0;
        for (int i = 0; i < cluster.numInstances(); i++) {
            sum += e.distance(cluster.instance(xi), cluster.instance(i));
        }
        return sum / cluster.numInstances();
    }

    private Double bOS(int xi, int ck){
        Instances cluster = clusters.get(ck);
        EuclideanDistance e = new EuclideanDistance(unitedClusters);

        Double sum = 0.0;
        ArrayList<Double> dist = new ArrayList<>();
        for (int i = 0; i < numOfClusters; i++) {
            if (i != ck) {
                Instances currCluster = clusters.get(i);
                for (int j = 0; j < currCluster.numInstances(); j++) {
                    dist.add(e.distance(currCluster.instance(j), cluster.instance(xi)));
                }
                Collections.sort(dist);
                for (int j = 0; j < currCluster.numInstances(); j++){
                    sum += dist.get(j);
                }
            }

        }
        return sum /= cluster.numInstances();
    }

    private Double ovOS(int xi, int ck){
        Double a = aOS(xi, ck);
        Double b = bOS(xi, ck);
        if (((b - a) / (b + a)) < 0.4) {
            return a / b;
        } else {
            return 0.0;
        }
    }

    @Override
    public Double evaluate() throws Exception {
        Double numerator = 0.0;

        for (int i = 0; i < numOfClusters; i++) {
            Instances currCluster = clusters.get(i);
            for (int j = 0; j < currCluster.numInstances(); j++) {
                numerator += ovOS(j, i);
            }
        }

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
