package ru.ifmo.ctddev.FSSARecSys.calculators.mfextraction.general;

import ru.ifmo.ctddev.FSSARecSys.calculators.mfextraction.MetaFeatureExtractor;
import ru.ifmo.ctddev.FSSARecSys.db.DataSet;
import weka.core.Instances;

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
    public double extract(DataSet dataSet) {
        Instances instances = dataSet.getInstances();
        return instances.classIndex() >= 0 ? instances.numAttributes() - 1 : instances.numAttributes();
    }
}
