package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering.metrics;

import weka.clusterers.Clusterer;
import weka.core.DenseInstance;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Created by sergey on 06.03.16.
 */
public class SDbw extends ClusterMetric {

    public SDbw(ArrayList<Instances> instances, Clusterer clusterer) {
        super(instances, clusterer);
    }

    @Override
    public String getName() {
        return "SDbw";
    }

    private Double normedSigma(Instances x, int clusterNum) throws Exception {
        Instance centroid = new DenseInstance(centroids.numAttributes());
        if (clusterNum == -1) {
            centroid = getDatasetCentroid();
        } else {
            centroid = getClusterCentroid(clusterNum);
        }

        double[] centroidArr = centroid.toDoubleArray();

        Instances copyX = new Instances(x);
        copyX.add(centroid);
        EuclideanDistance e = new EuclideanDistance(copyX);

        double[] sum = new double[centroidArr.length];
        for (int i = 0; i < x.numInstances(); i++) {
            Instance current = x.instance(i);
            double[] currArr = current.toDoubleArray();

            for (int j = 0; j < sum.length; j++) {
                if (x.attribute(j).isNumeric())
                    sum[j] += Math.pow(currArr[j] - centroidArr[j], 2.0);
                else {
                    if (x.attribute(j).isNominal()) {
                        sum[j] = currArr[j] == centroidArr[j] ? 0 : 1;
                    }
                }
            }
        }
        Double norm = 0.0;
        for (int i = 0; i < sum.length; i++) {
            norm += Math.pow(sum[i], 2.0);
        }
        norm = Math.sqrt(norm);
        int clusterSize = x.numInstances();

        return (norm / clusterSize);
    }

    private Double stdev() throws Exception {
        Double result = 0.0;
        for (int i = 0; i < numOfClusters; i++) {
            result += normedSigma(clusters.get(i), i);
        }
        return (1 / numOfClusters) * Math.sqrt(result);
    }

    private Double func(Instance a, Instance b, Double stdevVal) {
        Instances x = new Instances(centroids, 0);
        x.add(a);
        x.add(b);
        EuclideanDistance e = new EuclideanDistance(x);
        return e.distance(a, b) > stdevVal ? 0.0 : 1.0;
    }

    private Double den1(int clusterNum, Double stdevVal) throws Exception {
        Instances cluster = clusters.get(clusterNum);
        Instance centroid = centroids.instance(clusterNum);

        Double result = 0.0;
        for (int i = 0; i < cluster.numInstances(); i++) {
            Instance curr = cluster.instance(i);
            result += func(centroid, curr, stdevVal);
        }
        return result;
    }

    private Double den2(int clusterNum1, int clusterNum2, Double stdevVal) {
        Instances cluster1 = clusters.get(clusterNum1);
        Instances cluster2 = clusters.get(clusterNum2);

        Instances union = new Instances(cluster1);
        for (int i = 0; i < cluster2.numInstances(); i++) {
            union.add(cluster2.instance(i));
        }

        Instance centroid1 = centroids.instance(clusterNum1);
        Instance centroid2 = centroids.instance(clusterNum2);

        double[] meanCentrArr = new double[centroids.numAttributes()];
        double[] centr1 = centroid1.toDoubleArray();
        double[] centr2 = centroid2.toDoubleArray();

        for (int i = 0; i < meanCentrArr.length; i++) {
            meanCentrArr[i] = (centr1[i] + centr2[i]) / 2;
        }

        Instance meanCentroid = new DenseInstance(1.0, meanCentrArr);

        Double result = 0.0;
        for (int i = 0; i < union.numInstances(); i++) {
            Instance curr = union.instance(i);
            result += func(meanCentroid, curr, stdevVal);
        }
        return result;

    }

    @Override
    public Double evaluate() throws Exception {
        Double scat = 0.0;
        Double nsTotal = normedSigma(unitedClusters, -1);

        for (int i = 0; i < numOfClusters; i++) {
            Double nsCurr = normedSigma(clusters.get(i), i);
            scat += nsCurr / nsTotal;
        }

        scat /= numOfClusters;

        Double stdDevVal = stdev();
        Double dens = 0.0;

        for (int i = 0; i < numOfClusters - 1; i++) {
            for (int j = i + 1; j < numOfClusters; j++) {
                dens += den2(i, j, stdDevVal) / Math.max(den1(i, stdDevVal), den1(j, stdDevVal));
            }
        }
        dens /= numOfClusters * (numOfClusters - 1);

        return scat + dens;
    }
}
