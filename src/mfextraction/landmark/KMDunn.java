package mfextraction.landmark;

import java.util.List;

import mfextraction.Landmark;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by sergey on 01.03.16.
 */
public class KMDunn extends Landmark {
    @Override
    public String getName() {
        return "KM-Dunn-LandMark";
    }

  

    @Override
    public double evaluate(int numOfClusters, Instances unitedClusters, Instance datasetCentroid, List<Instances> clusters, Instances centroids) throws Exception {
        EuclideanDistance allInstancedDist = new EuclideanDistance(unitedClusters);

        double maxTotal = Double.NEGATIVE_INFINITY;
        double maxForCluster = Double.MIN_VALUE;
        for (int i = 0; i < numOfClusters; i++) {
            Instances currCluster = clusters.get(i);
            // maxTotal = Double.NEGATIVE_INFINITY;
            maxForCluster = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < currCluster.numInstances(); j++) {
                Instance currInstance = currCluster.instance(j);
                maxForCluster = Double.NEGATIVE_INFINITY;
                for (int k = 0; k < currCluster.numInstances(); k++) {
                    if (j != k)
                        maxForCluster = Double.max(maxForCluster, allInstancedDist.distance(currInstance, currCluster.instance(k)));
                }
            }
            maxTotal = Double.max(maxTotal, maxForCluster);
        }

        // get min intra-cluster distance

        double minIntraclusterDistance = Double.POSITIVE_INFINITY;
        double minLocalDistance = Double.POSITIVE_INFINITY;
        for (int i = 0; i < numOfClusters - 1; i++) {
            for (int j = i + 1; j < numOfClusters; j++) {
                Instances clusterI = clusters.get(i);
                Instances clusterJ = clusters.get(j);

                minLocalDistance = Double.POSITIVE_INFINITY;
                for (int k = 0; k < clusterI.numInstances(); k++) {
                    for (int p = 0; p < clusterJ.numInstances(); p++) {
                        Instance first = clusterI.instance(k);
                        Instance second = clusterJ.instance(p);
                        minLocalDistance = Double.min(minLocalDistance, allInstancedDist.distance(first, second));
                    }
                }
            }
            minIntraclusterDistance = Double.min(minIntraclusterDistance, minLocalDistance);
        }
        return minIntraclusterDistance / maxTotal;
    }
}
