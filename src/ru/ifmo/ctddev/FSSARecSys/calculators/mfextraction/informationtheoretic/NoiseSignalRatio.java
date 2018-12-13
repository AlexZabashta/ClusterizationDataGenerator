package ru.ifmo.ctddev.FSSARecSys.calculators.mfextraction.informationtheoretic;

import ru.ifmo.ctddev.FSSARecSys.db.DataSet;
import weka.core.Instances;

import static ru.ifmo.ctddev.FSSARecSys.utils.InformationTheoreticUtils.EntropyResult;
import static ru.ifmo.ctddev.FSSARecSys.utils.InformationTheoreticUtils.entropy;

/**
 * Created by warrior on 23.03.15.
 */
public class NoiseSignalRatio extends AbstractDiscretizeExtractor {

    public static final String NAME = "Noise-signal ratio";

    private double meanMutualInformation;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public double extract(DataSet dataSet) {
        meanMutualInformation = new MeanMutualInformation().extract(dataSet);
        return super.extract(dataSet);
    }

    @Override
    protected double extractInternal(Instances instances) {
        double sum = 0;
        int count = 0;
        for (int i = 0; i < instances.numAttributes(); i++) {
            if (isNonClassNominalAttribute(instances, i)) {
                count++;
                double[] values = instances.attributeToDoubleArray(i);
                EntropyResult result = entropy(values, instances.attribute(i).numValues());
                sum += result.entropy;
            }
        }
        double meanEntropy = sum / count;
        return (meanEntropy - meanMutualInformation) / meanMutualInformation;
    }
}
