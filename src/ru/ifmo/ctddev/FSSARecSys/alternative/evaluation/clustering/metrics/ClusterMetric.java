package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering.metrics;

import ru.ifmo.ctddev.FSSARecSys.utils.ClusterCentroid;
import weka.clusterers.Clusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by sergey on 02.03.16.
 */
public abstract class ClusterMetric {

    protected ArrayList<Instances> clusters;
    protected Instances unitedClusters;
    protected Instances centroids;
    protected Clusterer clusterer;

    protected Integer numOfClusters;

    protected Double NW;

    public ClusterMetric(){};

    public ClusterMetric(ArrayList<Instances> clusters, Clusterer clusterer){
        this.clusters = clusters;
        this.clusterer = Objects.requireNonNull(clusterer);
        numOfClusters = clusters.size();

        unitedClusters = getAllInstances();

        centroids = new Instances(clusters.get(0), 0);

        for (int i = 0; i < numOfClusters; i++) {
            Instance centroid = null;
            try {
                centroid = getClusterCentroid(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
            centroids.add(centroid);
        }

        NW = nW();
    }

    public Integer getNumClusters(){
        return numOfClusters;
    }

    public Instance getClusterCentroid(int clusterNum) throws Exception {
        if (clusterer instanceof SimpleKMeans) {
            return ((SimpleKMeans) clusterer).getClusterCentroids().instance(clusterNum);
        } else {
            ClusterCentroid ct = new ClusterCentroid();
            return ct.findCentroid(clusterNum, clusters.get(clusterNum));
        }
    }

    public Instance getDatasetCentroid(){
        ClusterCentroid ct = new ClusterCentroid();
        return ct.findCentroid(0, unitedClusters);
    }

    private Instances getAllInstances(){
        Instances all = new Instances(clusters.get(0));
        for (Instances cluster: clusters) {
            for (int i = 1; i < cluster.numInstances(); i++){
                all.add(cluster.instance(i));
            }
        }
        return all;
    }

    private Double fact(Double num) {
        return (num == 0.0) ? 1.0 : num * fact(num - 1.0);
    }

    private Double nW(){
        Double result = 0.0;
        for (int i = 0; i < numOfClusters; i++) {
            int n = clusters.get(i).numInstances();
            Double num = Double.valueOf(n);
            if (n > 2) {
//                Double nf = fact(num);
//                Double kf = fact(2.0) * fact(num - 2.0);
                result += num * (num - 1.0) / 2.0;
            }
        }
        return result;
    }

    public abstract String getName();
    public abstract Double evaluate() throws Exception;

    public static ClusterMetric forName(String className) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        if (!ClusterMetric.class.isAssignableFrom(clazz)) {
            return null;
        }
        Object newInstance;
        try {
            newInstance = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        return (ClusterMetric) newInstance;
    }

    protected Double delta3(int c1, int c2) {
        Instances cluster1 = clusters.get(c1);
        Instances cluster2 = clusters.get(c2);

        int size1 = cluster1.numInstances();
        int size2 = cluster2.numInstances();

        EuclideanDistance allInstancedDist = new EuclideanDistance(unitedClusters);
        Double result = 0.0;
        for (int i = 0; i < size1; i++) {
            for (int j = 0; j < size2; j++) {
                result += allInstancedDist.distance(cluster1.instance(i), cluster2.instance(j));
            }
        }
        result /= (size1 * size2);
        return result;
    }

    protected Double delta5(int c1, int c2) {
        Double result;
        Instances cluster1 = clusters.get(c1);
        Instances cluster2 = clusters.get(c2);

        int size1 = cluster1.numInstances();
        int size2 = cluster2.numInstances();

        cluster1.add(centroids.instance(c1));
        cluster2.add(centroids.instance(c2));

        EuclideanDistance d1 = new EuclideanDistance(cluster1);
        EuclideanDistance d2 = new EuclideanDistance(cluster2);

        Double sum1 = 0.0;
        for (int i = 0; i < size1; i++) {
            sum1 += d1.distance(cluster1.instance(i), cluster1.lastInstance());
        }

        Double sum2 = 0.0;
        for (int i = 0; i < size2; i++) {
            sum2 += d2.distance(cluster2.instance(i), cluster2.lastInstance());
        }

        cluster1.delete(cluster1.numInstances() - 1);
        cluster2.delete(cluster2.numInstances() - 1);

        result = (sum1 + sum2) / (size1 + size2);

        return result;
    }
}
