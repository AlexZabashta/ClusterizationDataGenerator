package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering.metric;

import weka.clusterers.Clusterer;
import weka.core.EuclideanDistance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by sergey on 06.03.16.
 */
public class CI extends ClusterMetric{

    public CI(ArrayList<Instances> instances, Clusterer clusterer) {
        super(instances, clusterer);
    }

    @Override
    public String getName() {
        return "CI";
    }

    private Double SCI() {
        Double result = 0.0;
        for (int i = 0; i < numOfClusters; i++) {
            Instances cluster = clusters.get(i);
            EuclideanDistance e = new EuclideanDistance(cluster);
            int size = cluster.numInstances();
            for (int j = 0; j < size - 1; j++) {
                for (int k = j + 1; k < size; k++) {
                    result += e.distance(cluster.instance(j), cluster.instance(k));
                }
            }
        }
        return result;
    }

    private ArrayList<Double> getAllDistances() {
        ArrayList<Double> dist = new ArrayList<>();
        EuclideanDistance e = new EuclideanDistance(unitedClusters);
        for (int i = 0; i < numOfClusters; i++) {
            for (int j = i; j < numOfClusters; j++) {
                Instances clusterFirst = clusters.get(i);
                Instances clusterSecond = clusters.get(j);

                int sizeFirst = clusterFirst.numInstances();
                int sizeSecond = clusterSecond.numInstances();

                for (int k = 0; k < sizeFirst; k++) {
                    for (int p = 0; p < sizeSecond; p++) {
                        dist.add(e.distance(clusterFirst.instance(k), clusterSecond.instance(p)));
                    }
                }

            }
        }
        return dist;
    }

    private Double SCIMin(ArrayList<Double> dist){
        Double result = 0.0;
        ArrayList<Double> localDist = dist;
        Collections.sort(localDist);
        for (int i = 0; i < NW; i++) {
            result += localDist.get(i);
        }
        return result;
    }

    private Double SCIMax(ArrayList<Double> dist){
        Double result = 0.0;
        ArrayList<Double> localDist = dist;
        Collections.sort(localDist);
        Collections.reverse(localDist);
        for (int i = 0; i < NW; i++) {
            result += localDist.get(i);
        }
        return result;
    }

    @Override
    public Double evaluate() throws Exception {
        Double S = SCI();
        ArrayList<Double> dist = getAllDistances();

        Double Smin = SCIMin(dist);
        Double Smax = SCIMax(dist);

        return (S - Smin) / (Smax - Smin);
    }
}
