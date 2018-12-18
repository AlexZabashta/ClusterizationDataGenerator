package mfextraction.landmark;

import java.util.List;

import mfextraction.Landmark;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by sergey on 02.03.16.
 */
public class KMCOP extends Landmark {
    @Override
    public String getName() {
        return "KM-COP-LandMark";
    }

    @Override
    public double evaluate(int numOfClusters, Instances unitedClusters, Instance datasetCentroid, List<Instances> clusters, Instances centroids) throws Exception {
        double result = 0.0;
        EuclideanDistance eclAll = new EuclideanDistance(unitedClusters);

        for (int i = 0; i < numOfClusters; i++) {

            double numerator = 0.0;

            Instances currCluster = new Instances(clusters.get(i));
            currCluster.add(centroids.instance(i));
            double sum = 0.0;
            EuclideanDistance e = new EuclideanDistance(currCluster);
            for (int j = 0; j < currCluster.numInstances() - 1; j++) {
                sum += e.distance(currCluster.instance(j), currCluster.lastInstance());
            }
            sum /= (currCluster.numInstances() - 1);
            currCluster.delete(currCluster.numInstances() - 1);
            numerator = sum;

            double denominator = 0.0;

            double minDist = Double.POSITIVE_INFINITY;
            for (int j = 0; j < numOfClusters; j++) {
                if (i != j) {
                    Instances comparedCluster = clusters.get(j);
                    for (int k = 0; k < comparedCluster.numInstances(); k++) {
                        double maxDist = Double.NEGATIVE_INFINITY;
                        for (int p = 0; p < currCluster.numInstances(); p++)
                            maxDist = Double.max(maxDist, eclAll.distance(comparedCluster.instance(k), currCluster.instance(p)));
                        minDist = Double.min(minDist, maxDist);
                    }
                }
            }
            denominator = minDist;
            result += (numerator / denominator) * currCluster.numInstances();
        }
        return result / unitedClusters.numInstances();
    }
}
