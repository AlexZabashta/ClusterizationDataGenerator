package mfextraction.landmark;

import java.util.List;

import mfextraction.Landmark;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by sergey on 01.03.16.
 */
public class KMCH extends Landmark {
    @Override
    public String getName() {
        return "KM-CH-LandMark";
    }

    @Override
    public double evaluate(int numOfClusters, Instances unitedClusters, Instance datasetCentroid, List<Instances> clusters, Instances centroids) throws Exception {
        double numerator = 0.0;

        centroids = new Instances(centroids);
        centroids.add(datasetCentroid);

        EuclideanDistance e = new EuclideanDistance(centroids);

        double sum = 0.0;
        for (int i = 0; i < numOfClusters; i++) {
            sum += clusters.get(i).numInstances() * Math.pow(e.distance(centroids.instance(i), centroids.lastInstance()), 2.0);
        }
        numerator = (unitedClusters.numInstances() - numOfClusters) * sum;

        double denominator = 0.0;

        sum = 0.0;
        for (int i = 0; i < numOfClusters; i++) {
            Instances currCluster = new Instances(clusters.get(i));
            currCluster.add(centroids.instance(i));
            EuclideanDistance ecl = new EuclideanDistance(currCluster);
            for (int j = 0; j < currCluster.numInstances() - 1; j++) {
                sum += Math.pow(ecl.distance(currCluster.instance(j), currCluster.lastInstance()), 2.0);
            }
        }
        denominator = sum * (numOfClusters - 1);
        return numerator / denominator;
    }
}
