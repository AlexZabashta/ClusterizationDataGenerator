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
public class SymmDB extends ClusterMetric {
    public SymmDB(ArrayList<Instances> clusters, Clusterer clusterer) {
        super(clusters, clusterer);
    }

    @Override
    public String getName() {
        return "SymmDB";
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
        ArrayList<Double> sTemp = new ArrayList<>(numOfClusters);
        ArrayList<EuclideanDistance> euclideanDistances = new ArrayList<>(numOfClusters);

        //count C_i = (1 / |c_k|) * sum (x in c_k) {dps(x, centr_k)}

        Double tmpSum = 0.0;
        for (int i = 0; i < numOfClusters; i++) {
            Instances currCluster = clusters.get(i);
            for (int j = 0; j < currCluster.numInstances(); j++) {
                tmpSum += dps(j, i);
            }
            sTemp.set(i, tmpSum / currCluster.numInstances());
        }

        //count D_i = max_j (i!=j) {(S_i + S_j) / dist(centr_i, centr_j)}

        ArrayList<Double> dTemp = new ArrayList<>(numOfClusters);
        for (int i = 0; i < numOfClusters; i++){
            dTemp.add(Double.NEGATIVE_INFINITY);
        }

        EuclideanDistance centroidDist = new EuclideanDistance(centroids);

        Double maxVal = Double.NEGATIVE_INFINITY;
        Double minVal = Double.POSITIVE_INFINITY;
        for (int i = 0; i < clusters.size(); i++) {
            maxVal = Double.NEGATIVE_INFINITY;
            minVal = Double.POSITIVE_INFINITY;
            for (int j = 0; j < clusters.size(); j++)
                if (i != j) {
                    maxVal = Double.max(maxVal, (sTemp.get(i) + sTemp.get(j)));
                    minVal = Double.min(minVal, centroidDist.distance(centroids.instance(i), centroids.instance(j)));
                }
            dTemp.set(i, maxVal / minVal);
        }
        Double result = 0.0;

        for (Double i: dTemp) {
            result += i;
        }

        return result / numOfClusters;
    }
}
