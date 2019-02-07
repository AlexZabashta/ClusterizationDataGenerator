package mfextraction.landmark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mfextraction.Landmark;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by sergey on 02.03.16.
 */
public class KMSV extends Landmark {
    @Override
    public String getName() {
        return "KM-SV-LandMark";
    }

    @Override
    public double evaluate(int numOfClusters, Instances unitedClusters, Instance datasetCentroid, List<Instances> clusters, Instances centroids) throws Exception {
        double numerator = 0.0;

        EuclideanDistance e = new EuclideanDistance(centroids);

        double minCentrDist = Double.POSITIVE_INFINITY;
        for (int i = 0; i < numOfClusters; i++) {
            for (int j = i + 1; j < numOfClusters; j++) {
                minCentrDist = Double.min(minCentrDist, e.distance(centroids.instance(i), centroids.instance(j)));
            }
        }
        numerator = minCentrDist;

        double denominator = 0.0;

        for (int i = 0; i < numOfClusters; i++) {
            Instances currCluster = new Instances(clusters.get(i));
            double sum = 0.0;
            // double maxToCentrDist = Double.NEGATIVE_INFINITY;

            currCluster.add(centroids.instance(i));
            EuclideanDistance ecl = new EuclideanDistance(currCluster);

            ArrayList<Double> dist = new ArrayList<>();
            for (int j = 0; j < currCluster.numInstances() - 1; j++) {
                dist.add(ecl.distance(currCluster.instance(j), currCluster.lastInstance()));
                // maxToCentrDist = Double.max(maxToCentrDist, ecl.distance(currCluster.instance(j), currCluster.lastInstance()));
            }
            currCluster.delete(currCluster.numInstances() - 1);
            Collections.sort(dist);
            Collections.reverse(dist);

            for (int j = 0; j < 0.1 * (currCluster.numInstances()); j++) {
                sum += dist.get(j);
            }
            sum *= 10;
            sum /= currCluster.numInstances();
            denominator += sum;
        }

        return numerator / denominator;
    }
}
