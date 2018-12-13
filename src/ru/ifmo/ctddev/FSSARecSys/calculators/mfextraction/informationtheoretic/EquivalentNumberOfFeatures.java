package ru.ifmo.ctddev.FSSARecSys.calculators.mfextraction.informationtheoretic;

import ru.ifmo.ctddev.FSSARecSys.db.DataSet;
import weka.core.Instances;

import static ru.ifmo.ctddev.FSSARecSys.utils.InformationTheoreticUtils.EntropyResult;
import static ru.ifmo.ctddev.FSSARecSys.utils.InformationTheoreticUtils.entropy;

/**
 * Created by warrior on 23.03.15.
 */
public class EquivalentNumberOfFeatures extends AbstractDiscretizeExtractor {

    public static final String NAME = "Equivalent number of features";

    private double meanMutualInformation;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double extract(DataSet dataSet) {
        meanMutualInformation = new MeanMutualInformation().extract(dataSet);
        return super.extract(dataSet);
    }

    @Override
    protected double extractInternal(Instances instances) {
        int classIndex = instances.classIndex();
        if (classIndex < 0) {
            throw new IllegalArgumentException("dataset hasn't class attribute");
        }
        double[] values = instances.attributeToDoubleArray(classIndex);
        EntropyResult result = entropy(values, instances.classAttribute().numValues());
        return result.entropy / meanMutualInformation;
    }
}
