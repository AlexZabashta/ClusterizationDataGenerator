package mfextraction.landmark;

import java.util.List;

import mfextraction.Landmark;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by sergey on 02.03.16.
 */
public class KMCS extends Landmark {
    @Override
    public String getName() {
        return "KM-CS-Landmark";
    }

    @Override
    public double evaluate(int numOfClusters, Instances unitedClusters, Instance datasetCentroid, List<Instances> clusters, Instances centroids) throws Exception {
        double numerator = 0.0;

        for (int i = 0; i < numOfClusters; i++) {
            Instances currCluster = clusters.get(i);
            int currSize = currCluster.numInstances();
            EuclideanDistance e = new EuclideanDistance(currCluster);
            double maxDist = Double.NEGATIVE_INFINITY;
            double sum = 0.0;
            for (int j = 0; j < currSize - 1; j++) {
                for (int k = j; k < currSize; k++) {
                    Instance first = currCluster.instance(j);
                    Instance second = currCluster.instance(k);
                    maxDist = Double.max(maxDist, e.distance(first, second));
                }
                sum += maxDist;
            }
            sum /= currSize;
            numerator += sum;
        }

        double denominator = 0.0;

        EuclideanDistance eCent = new EuclideanDistance(centroids);
        for (int i = 0; i < numOfClusters - 1; i++) {
            double minVal = Double.POSITIVE_INFINITY;
            for (int j = i + 1; j < numOfClusters; j++) {
                minVal = Double.min(minVal, eCent.distance(centroids.instance(i), centroids.instance(j)));
            }
            denominator += minVal;
        }

        return numerator / denominator;
    }
}
