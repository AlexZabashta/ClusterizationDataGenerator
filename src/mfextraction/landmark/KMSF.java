package mfextraction.landmark;

import java.util.List;

import mfextraction.Landmark;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by sergey on 01.03.16.
 */
public class KMSF extends Landmark {
    @Override
    public String getName() {
        return "KM-SF-Landmark";
    }

    private double bcd(int numOfClusters, Instances unitedClusters, Instance datasetCentroid, List<Instances> clusters, Instances centroids) {

        Instances centroidsCpy = new Instances(centroids);

        centroidsCpy.add(datasetCentroid);

        EuclideanDistance e = new EuclideanDistance(centroids);

        double sum = 0.0;
        for (int i = 0; i < numOfClusters; i++) {
            sum += clusters.get(i).numInstances() * e.distance(centroids.instance(i), centroids.lastInstance());
        }

        return sum / (numOfClusters * unitedClusters.numInstances());

    }

    private double wcd(int numOfClusters, Instances unitedClusters, Instance datasetCentroid, List<Instances> clusters, Instances centroids) {
        double result = 0.0;
        for (int i = 0; i < numOfClusters; i++) {
            double sum = 0.0;
            Instances currCluster = new Instances(clusters.get(i));
            currCluster.add(centroids.instance(i));
            EuclideanDistance ecl = new EuclideanDistance(currCluster);
            for (int j = 0; j < currCluster.numInstances() - 1; j++) {
                sum += ecl.distance(currCluster.instance(j), currCluster.lastInstance());
            }
            result += (1 / clusters.get(i).numInstances()) * sum;
        }
        return result;
    }

    @Override
    public double evaluate(int numOfClusters, Instances unitedClusters, Instance datasetCentroid, List<Instances> clusters, Instances centroids) throws Exception {
        return Math.exp(Math.exp(bcd(numOfClusters, unitedClusters, datasetCentroid, clusters, centroids) + wcd(numOfClusters, unitedClusters, datasetCentroid, clusters, centroids)));
    }
}
