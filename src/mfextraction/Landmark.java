package mfextraction;

import java.util.List;

import weka.core.Instance;
import weka.core.Instances;

public abstract class Landmark extends MetaFeatureExtractor {

    @Override
    public double extract(CacheMF cache) {
        KmeansResult result = cache.kmeansResult();
        try {
            return evaluate(result.numOfClusters, result.unitedClusters, result.datasetCentroid, result.clusters, result.centroids);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public abstract double evaluate(int numOfClusters, Instances unitedClusters, Instance datasetCentroid, List<Instances> clusters, Instances centroids) throws Exception;

}
