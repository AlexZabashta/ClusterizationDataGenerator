package ru.ifmo.ctddev.FSSARecSys.calculators.mfextraction.clustermf.landmarks;

import java.util.ArrayList;

import mfextraction.MetaFeatureExtractor;
import utils.ClusterCentroid;
import weka.clusterers.SimpleKMeans;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by sergey on 24.02.16.
 */
public class KMDBLandmark extends MetaFeatureExtractor {
    @Override
    public String getName() {
        return "KM-DB-LandMark";
    }

    @Override
    public double extract(Instances instances) throws Exception {
        SimpleKMeans clusterer = new SimpleKMeans();
        clusterer.setOptions(weka.core.Utils.splitOptions("\"-N 5 -A \\\"weka.core.EuclideanDistance -R first-last\\\" -I 500 -S 10\", \"clusterisation\""));

        clusterer.buildClusterer(instances);

        int numOfClusters = clusterer.getNumClusters();

        ClusterCentroid ct = new ClusterCentroid();
        Instance datasetCentroid = ct.findCentroid(0, instances);
        Instances centroids = clusterer.getClusterCentroids();

        Instances unitedClusters = new Instances(instances, 0);
        ArrayList<Instances> clusters = new ArrayList<>(numOfClusters);

        for (int i = 0; i < numOfClusters; i++) {
            clusters.add(new Instances(instances, 0));
        }

        for (Instance instance : instances) {
            int c = clusterer.clusterInstance(instance);
            if (0 <= c && c < numOfClusters) {
                clusters.get(c).add(instance);
                unitedClusters.add(instance);
            }
        }

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
