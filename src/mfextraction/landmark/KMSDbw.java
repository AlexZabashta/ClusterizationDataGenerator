package mfextraction.landmark;

import java.util.List;

import mfextraction.Landmark;
import weka.core.DenseInstance;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by sergey on 01.03.16.
 */
public class KMSDbw extends Landmark {
    @Override
    public String getName() {
        return "KM-SDbw-Landmark";
    }

    private double normedSigma(Instances x, Instance centroid) throws Exception {

        double[] centroidArr = centroid.toDoubleArray();

        Instances copyX = new Instances(x);
        copyX.add(centroid);

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
        double norm = 0.0;
        for (int i = 0; i < sum.length; i++) {
            norm += Math.pow(sum[i], 2.0);
        }
        norm = Math.sqrt(norm);
        int clusterSize = x.numInstances();

        return (norm / clusterSize);
    }

    private double stdev(int numOfClusters, List<Instances> clusters, Instances centroids) throws Exception {
        double result = 0.0;
        for (int i = 0; i < numOfClusters; i++) {
            result += normedSigma(clusters.get(i), centroids.get(i));
        }
        return (1 / numOfClusters) * Math.sqrt(result);
    }

    private double func(Instance a, Instance b, double stdevVal, Instances centroids) {
        Instances x = new Instances(centroids, 0);
        x.add(a);
        x.add(b);
        EuclideanDistance e = new EuclideanDistance(x);
        return e.distance(a, b) > stdevVal ? 0.0 : 1.0;
    }

    private double den1(int clusterNum, double stdevVal, List<Instances> clusters, Instances centroids) throws Exception {
        Instances cluster = clusters.get(clusterNum);
        Instance centroid = centroids.instance(clusterNum);

        double result = 0.0;
        for (int i = 0; i < cluster.numInstances(); i++) {
            Instance curr = cluster.instance(i);
            result += func(centroid, curr, stdevVal, centroids);
        }
        return result;
    }

    private double den2(int clusterNum1, int clusterNum2, double stdevVal, List<Instances> clusters, Instances centroids) {
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

        double result = 0.0;
        for (int i = 0; i < union.numInstances(); i++) {
            Instance curr = union.instance(i);
            result += func(meanCentroid, curr, stdevVal, centroids);
        }
        return result;

    }

    @Override
    public double evaluate(int numOfClusters, Instances unitedClusters, Instance datasetCentroid, List<Instances> clusters, Instances centroids) throws Exception {
        double scat = 0.0;
        double nsTotal = normedSigma(unitedClusters, datasetCentroid);

        for (int i = 0; i < numOfClusters; i++) {
            double nsCurr = normedSigma(clusters.get(i), centroids.get(i));
            scat += nsCurr / nsTotal;
        }

        scat /= numOfClusters;

        double stdDevVal = stdev(numOfClusters, clusters, centroids);
        double dens = 0.0;

        for (int i = 0; i < numOfClusters - 1; i++) {
            for (int j = i + 1; j < numOfClusters; j++) {
                dens += den2(i, j, stdDevVal, clusters, centroids) / Math.max(den1(i, stdDevVal, clusters, centroids), den1(j, stdDevVal, clusters, centroids));
            }
        }
        dens /= numOfClusters * (numOfClusters - 1);

        return scat + dens;
    }
}
