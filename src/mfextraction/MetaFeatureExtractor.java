package mfextraction;

import clusterization.Dataset;

public abstract class MetaFeatureExtractor {
    public abstract String getName();

    /**
     * This method should return the result of computing value for meta feature with name.
     * Any specific arguments?
     */

    public abstract double extract(CacheMF cache);

    public double extract(Dataset dataset) {
        return extract(new CacheMF(dataset));
    }

}
