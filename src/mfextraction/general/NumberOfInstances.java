package mfextraction.general;

import mfextraction.CacheMF;
import mfextraction.MetaFeatureExtractor;

/**
 * Created by warrior on 22.03.15.
 */
public class NumberOfInstances extends MetaFeatureExtractor {

    public static final String NAME = "Number of instances";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double extract(CacheMF cache) {
        return cache.dataset().numObjects;
    }
}
