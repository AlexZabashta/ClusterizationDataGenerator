package ru.ifmo.ctddev.FSSARecSys.calculators.mfextraction.informationtheoretic;

import ru.ifmo.ctddev.FSSARecSys.calculators.mfextraction.MetaFeatureExtractor;
import ru.ifmo.ctddev.FSSARecSys.db.DataSet;
import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;

/**
 * Created by warrior on 23.03.15.
 */
public abstract class AbstractDiscretizeExtractor extends MetaFeatureExtractor {

    @Override
    public double extract(DataSet dataSet) {
        Instances instances = dataSet.getInstances();
        Discretize discretize = new Discretize();
        discretize.setUseBetterEncoding(true);
        try {
            discretize.setInputFormat(instances);
            instances = Filter.useFilter(instances, discretize);
            return extractInternal(instances);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    protected boolean isNonClassNominalAttribute(Instances instances, int attributeIndex) {
        return isNonClassAttributeWithType(instances, attributeIndex, Attribute.NOMINAL);
    }

    protected abstract double extractInternal(Instances instances);
}
