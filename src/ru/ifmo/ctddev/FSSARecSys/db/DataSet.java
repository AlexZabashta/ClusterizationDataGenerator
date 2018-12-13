package ru.ifmo.ctddev.FSSARecSys.db;

import ru.ifmo.ctddev.FSSARecSys.ClassifierResult;
import ru.ifmo.ctddev.FSSARecSys.FSSResult;
import ru.ifmo.ctddev.FSSARecSys.calculators.ClassifierEvaluator;
import ru.ifmo.ctddev.FSSARecSys.calculators.FSSAlgorithm;
import ru.ifmo.ctddev.FSSARecSys.utils.MetaFeaturesVector;
import weka.core.Instances;

public interface DataSet {
    public String getName();

    /**
     * if it has class index it should be already applied
     */
    public Instances getInstances();

    public String[] getAvailableMeatFeatureNames();
    public double getMetaFeature(String name);

    public double[] getMetaFeatures(MetaFeaturesVector metaFeatures);
    public void updateMetaFeature(String name, double value);

    public void updateMetaFeatures(MetaFeaturesVector metaFeatures, double[] values);

    /**
     * extract and save using results:
     * selection time
     * count of selected features
     * selected features in order
     */
    public void selectFeatures(FSSAlgorithm algorithm);
    public FSSResult getFSSResult(FSSAlgorithm algorithm);
    public void updateSelectionResult(FSSAlgorithm algorithm, FSSResult result);

    /**
     * evaluate(classify or cluster) selected by algorithm features and store result
     */
    public void evaluate(FSSAlgorithm algorithm, ClassifierEvaluator classifierEvaluator);
    public ClassifierResult getEvaluationResult(ClassifierEvaluator classifierEvaluator, FSSAlgorithm algorithm);
    public void updateEvaluationResult(ClassifierEvaluator classifierEvaluator, FSSAlgorithm algorithm, ClassifierResult result);
}
