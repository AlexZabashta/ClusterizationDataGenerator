package dsgenerators;

import java.util.Random;

import com.ifmo.recommendersystem.metafeatures.MetaFeatureExtractor;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instances;

public class SMOF extends MetaFeatureExtractor {

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public double extractValue(Instances instances) throws Exception {
        SMO smo = new SMO();
        smo.buildClassifier(instances);
        Evaluation evaluation = new Evaluation(instances);
        evaluation.crossValidateModel(smo, instances, 5, new Random(42));
        return evaluation.weightedFMeasure();
    }

}
