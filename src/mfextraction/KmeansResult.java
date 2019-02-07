package mfextraction;

import java.util.ArrayList;
import java.util.List;

import utils.ClusterCentroid;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;

class KmeansResult {
    public final int numOfClusters;
    public final Instances unitedClusters;
    public final Instance datasetCentroid;
    public final List<Instances> clusters;
    public final Instances centroids;

    public KmeansResult(CacheMF cache) throws Exception {
        SimpleKMeans clusterer = new SimpleKMeans();

        clusterer.setNumClusters(5);
        clusterer.setDistanceFunction(new weka.core.EuclideanDistance());
        clusterer.setMaxIterations(500);

        Instances instances = cache.instances();

        clusterer.buildClusterer(instances);

        this.numOfClusters = clusterer.getNumClusters();

        ClusterCentroid ct = new ClusterCentroid();
        this.datasetCentroid = ct.findCentroid(0, instances);
        this.centroids = clusterer.getClusterCentroids();

        this.unitedClusters = new Instances(instances, 0);
        this.clusters = new ArrayList<>(numOfClusters);

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
    }

}