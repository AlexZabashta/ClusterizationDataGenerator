package ru.ifmo.ctddev.FSSARecSys.calculators.mfextraction.general;

import ru.ifmo.ctddev.FSSARecSys.calculators.mfextraction.MetaFeatureExtractor;
import ru.ifmo.ctddev.FSSARecSys.db.DataSet;

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
    public double extract(DataSet dataSet) {
        return dataSet.getInstances().numInstances();
    }
}
