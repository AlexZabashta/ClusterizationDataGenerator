package mfextraction.general;

import clusterization.Dataset;
import mfextraction.CacheMF;
import mfextraction.MetaFeatureExtractor;

/**
 * Created by warrior on 22.03.15.
 */
public class DataSetDimensionality extends MetaFeatureExtractor {

    public static final String NAME = "Data set dimensionality";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double extract(CacheMF cache) {
        Dataset dataset = cache.dataset();
        return (double) dataset.numObjects / dataset.numFeatures;
    }
}
