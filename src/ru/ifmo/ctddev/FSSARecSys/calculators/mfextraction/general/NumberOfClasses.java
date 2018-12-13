package ru.ifmo.ctddev.FSSARecSys.calculators.mfextraction.general;

import ru.ifmo.ctddev.FSSARecSys.calculators.mfextraction.MetaFeatureExtractor;
import ru.ifmo.ctddev.FSSARecSys.db.DataSet;
import weka.core.Instances;

/**
 * Created by warrior on 22.03.15.
 */
public class NumberOfClasses extends MetaFeatureExtractor {

    public static final String NAME = "Number of classes";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double extract(DataSet dataSet) {
        Instances instances = dataSet.getInstances();
        if (instances.classIndex() >= 0) {
            return dataSet.getInstances().numClasses();
        }
        return 0;
    }
}
