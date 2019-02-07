package mfextraction.general;

import mfextraction.CacheMF;
import mfextraction.MetaFeatureExtractor;

/**
 * Created by warrior on 22.03.15.
 */
public class NumberOfFeatures extends MetaFeatureExtractor {

    public static final String NAME = "Number of features";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double extract(CacheMF cache) {
        return cache.dataset().numFeatures;
    }
}
