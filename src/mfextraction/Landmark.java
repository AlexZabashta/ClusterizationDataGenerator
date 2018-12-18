package mfextraction;

import java.util.ArrayList;
import java.util.List;

import utils.ClusterCentroid;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;

public abstract class Landmark extends MetaFeatureExtractor {

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
        return evaluate(numOfClusters, unitedClusters, datasetCentroid, clusters, centroids);
    }

    public abstract double evaluate(int numOfClusters, Instances unitedClusters, Instance datasetCentroid, List<Instances> clusters, Instances centroids) throws Exception;

}
