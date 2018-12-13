package dsgenerators;

import java.util.Random;

import com.ifmo.recommendersystem.metafeatures.MetaFeatureExtractor;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class NaiveBayesF extends MetaFeatureExtractor {

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public double extractValue(Instances instances) throws Exception {
        NaiveBayes naiveBayes = new NaiveBayes();
        naiveBayes.buildClassifier(instances);
        Evaluation evaluation = new Evaluation(instances);
        evaluation.crossValidateModel(naiveBayes, instances, 5, new Random(42));
        return evaluation.weightedFMeasure();
    }

}
