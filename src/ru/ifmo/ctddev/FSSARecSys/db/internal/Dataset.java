package ru.ifmo.ctddev.FSSARecSys.db.internal;
import ru.ifmo.ctddev.FSSARecSys.ClassifierResult;
import ru.ifmo.ctddev.FSSARecSys.FSSResult;
import ru.ifmo.ctddev.FSSARecSys.calculators.*;
import ru.ifmo.ctddev.FSSARecSys.calculators.FSSAlgorithm;
import ru.ifmo.ctddev.FSSARecSys.db.DataSet;
import ru.ifmo.ctddev.FSSARecSys.utils.MetaFeaturesVector;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

import java.io.File;
import java.io.IOException;

/**
 * Created by sergey on 26.03.15.
 */
public class Dataset implements DataSet{
    private String name;
    private File file;
    private String taskType;

    public Dataset(String name, File file, String taskType){
        this.file = file;
        this.name = name;
        this.taskType = taskType;
    }

    public String getName() {
        return name;
    }

    @Override
    public Instances getInstances() {
        try {
            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource(file.getPath());

            Instances instances = dataSource.getDataSet();

//            Filter filter = new Normalize();
//            filter.setInputFormat(instances);
//            instances = Filter.useFilter(instances, filter);

            return instances;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String[] getAvailableMeatFeatureNames() {
        return new String[0];
    }

    @Override
    public double getMetaFeature(String name) {
        return 0;
    }

    @Override
    public double[] getMetaFeatures(MetaFeaturesVector metaFeatures) {
        return new double[0];
    }

    @Override
    public void updateMetaFeature(String name, double value) {

    }

    @Override
    public void updateMetaFeatures(MetaFeaturesVector metaFeatures, double[] values) {

    }

    @Override
    public void selectFeatures(FSSAlgorithm algorithm) {

    }

    @Override
    public FSSResult getFSSResult(FSSAlgorithm algorithm) {
        return null;
    }

    @Override
    public void updateSelectionResult(FSSAlgorithm algorithm, FSSResult result) {

    }

    @Override
    public void evaluate(FSSAlgorithm algorithm, ClassifierEvaluator classifierEvaluator) {

    }

    @Override
    public ClassifierResult getEvaluationResult(ClassifierEvaluator classifierEvaluator, FSSAlgorithm algorithm) {
        return null;
    }

    @Override
    public void updateEvaluationResult(ClassifierEvaluator classifierEvaluator, FSSAlgorithm algorithm, ClassifierResult result) {

    }

    public File getFile() {
        return file;
    }

    public String getTaskType() {
        return taskType;
    }
}
