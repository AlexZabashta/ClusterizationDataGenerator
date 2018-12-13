package ru.ifmo.ctddev.FSSARecSys.calculators.mfextraction.informationtheoretic;

import ru.ifmo.ctddev.FSSARecSys.calculators.mfextraction.MetaFeatureExtractor;
import ru.ifmo.ctddev.FSSARecSys.db.DataSet;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.core.Instances;

/**
 * Created by warrior on 23.03.15.
 */
public class MeanMutualInformation extends MetaFeatureExtractor {

    public static final String NAME = "Mean mutual information of class and attribute";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double extract(DataSet dataSet) {
        Instances instances = dataSet.getInstances();
        InfoGainAttributeEval infoGain = new InfoGainAttributeEval();
        try {
            infoGain.buildEvaluator(instances);
        } catch (Exception e) {
            e.printStackTrace();
        }
        double meanMutualInformation = 0;
        for (int i = 0; i < instances.numAttributes(); i++) {
            if (i != instances.classIndex()) {
                try {
                    meanMutualInformation += infoGain.evaluateAttribute(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return meanMutualInformation;
    }
}
