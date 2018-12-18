package mfextraction;

import weka.core.Attribute;
import weka.core.Instances;

import java.util.Arrays;

public abstract class MetaFeatureExtractor {
    public abstract String getName();

    /**
     * This method should return the result of computing value for meta feature with name.
     * Any specific arguments?
     */
    public abstract double extract(Instances instances) throws Exception;

}
