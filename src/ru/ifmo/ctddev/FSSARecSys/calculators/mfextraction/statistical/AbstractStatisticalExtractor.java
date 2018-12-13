package ru.ifmo.ctddev.FSSARecSys.calculators.mfextraction.statistical;

import ru.ifmo.ctddev.FSSARecSys.calculators.mfextraction.MetaFeatureExtractor;
import weka.core.Attribute;
import weka.core.Instances;

/**
 * Created by warrior on 23.03.15.
 */
public abstract class AbstractStatisticalExtractor extends MetaFeatureExtractor {

    protected boolean isNonClassNumericalAttribute(Instances instances, int attributeIndex) {
        return isNonClassAttributeWithType(instances, attributeIndex, Attribute.NOMINAL, Attribute.NUMERIC);
    }
}
