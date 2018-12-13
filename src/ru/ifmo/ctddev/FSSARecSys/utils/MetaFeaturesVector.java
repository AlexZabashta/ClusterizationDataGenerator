package ru.ifmo.ctddev.FSSARecSys.utils;

import ru.ifmo.ctddev.FSSARecSys.db.DataSet;

public interface MetaFeaturesVector {
    default double[] getValues(DataSet dataSet) {
        return dataSet.getMetaFeatures(this);
    }

    default void updateValues(DataSet dataSet, double[] values) {
        dataSet.updateMetaFeatures(this, values);
    }

    /**
     * @return number of meta-features selected by this vector
     */
    int getNumberOfMetaFeatures();
}
