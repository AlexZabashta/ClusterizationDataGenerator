package ru.ifmo.ctddev.FSSARecSys.calculators.mfextraction.general;

import ru.ifmo.ctddev.FSSARecSys.calculators.mfextraction.MetaFeatureExtractor;
import ru.ifmo.ctddev.FSSARecSys.db.DataSet;
import weka.core.Instances;

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
    public double extract(DataSet dataSet) {
        Instances instances = dataSet.getInstances();
        int instanceNumber = instances.numInstances();
        int attributeNumber = instances.classIndex() >= 0 ? instances.numAttributes() - 1 : instances.numAttributes();
        return (double) instanceNumber / attributeNumber;
    }
}
