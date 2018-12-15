package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.classification;

import ru.ifmo.ctddev.FSSARecSys.ClassifierResult;
import ru.ifmo.ctddev.FSSARecSys.FSSResult;
import ru.ifmo.ctddev.FSSARecSys.db.internal.*;
import ru.ifmo.ctddev.FSSARecSys.db.manager.QueryManager;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Администратор on 10.08.2015.
 */
public class EARREvaluation {
    private FSSResult fssResult;
    private ClassifierResult classifierResult;
    private float alpha;
    private float betta;
    private QueryManager queryManager;

    public EARREvaluation(float alpha, float betta, QueryManager queryManager) {
        this.alpha = alpha;
        this.betta = betta;
        this.queryManager = queryManager;
    }

    private Double EARR_ij(EARRParams params1, EARRParams params2) {
        return (params1.getAccuracy() / params2.getAccuracy()) / (1.0 +
                alpha * Math.log(params1.getSelectionTime() / params2.getSelectionTime()) +
                betta * (params1.getNumberOfSelectedFeatures() / params2.getNumberOfSelectedFeatures()));
    }

    public Double evaluate(FSSAlgorithm algo, ArrayList<FSSAlgorithm> allFSSAlgorithms, Dataset dataset, List<String> listOfFeatures, MLAlgorithm mlAlgorithm) throws Exception {
        Double sum = 0.0;
        String datasetName = dataset.getName();

        // if params for current fss algorithm is counted
        EARRParams newParams = queryManager.getEARRParams(new Metrics(algo, datasetName, listOfFeatures), mlAlgorithm);
        if (newParams == null){

            Instances datasetInstances = dataset.getInstances();

            ASSearch asSearch = ASSearch.forName(algo.getSearchClass(), weka.core.Utils.splitOptions(algo.getSearchOptions()));
            ASEvaluation asEvaluation = ASEvaluation.forName(algo.getEvalClass(), weka.core.Utils.splitOptions(algo.getEvalOptions()));

            FSSClassificationEvaluator fssClassificationEvaluator = new FSSClassificationEvaluator(algo.getFssAlgoName(), asSearch, asEvaluation);
            FSSResult fssResult = fssClassificationEvaluator.run(datasetName, datasetInstances);

            Integer numberOfSelectedFeatures = fssResult.numberOfSelectedFeatures;
            Long selectionTime = fssResult.selectionTime;

            ArrayList<Integer> unusedFeatures = new ArrayList<>();
            for (int i = 0; i < dataset.getInstances().numAttributes(); i++){
                unusedFeatures.add(i);
            }

            for (Integer i: fssResult.selectedFeatures) {
                unusedFeatures.remove(i);
            }

            for (Integer i: unusedFeatures){
                datasetInstances.deleteAttributeAt(i);
            }

            int trainSize = (int) Math.round(datasetInstances.numInstances() * 0.8);
            int testSize = datasetInstances.numInstances() - trainSize;
            Instances train = new Instances(datasetInstances, 0, trainSize);
            Instances test = new Instances(datasetInstances, trainSize, testSize);

            Classifier classifier = Classifier.forName(mlAlgorithm.getClassPath(), weka.core.Utils.splitOptions(mlAlgorithm.getOptions()));
            ClassifierEvaluator classifierEvaluator = new ClassifierEvaluator(mlAlgorithm.getName(), classifier);
            Double accuracy = classifierEvaluator.evaluate(mlAlgorithm.getName(), train, test).accuracy;

            newParams = new EARRParams(accuracy, numberOfSelectedFeatures.doubleValue(), selectionTime.doubleValue());

            queryManager.addMetricParam(new Metrics(algo, datasetName, listOfFeatures), "accuracy", mlAlgorithm, accuracy);
            queryManager.addMetricParam(new Metrics(algo, datasetName, listOfFeatures), "number of selected features", mlAlgorithm, numberOfSelectedFeatures.doubleValue());
            queryManager.addMetricParam(new Metrics(algo, datasetName, listOfFeatures), "selection time", mlAlgorithm, selectionTime.doubleValue());
        }

        for (FSSAlgorithm fssAlgorithm : allFSSAlgorithms) {
            if (fssAlgorithm != algo) {
                EARRParams otherParams = queryManager.getEARRParams(new Metrics(fssAlgorithm, datasetName, listOfFeatures), mlAlgorithm);
                if (otherParams != null){
                    sum += EARR_ij(newParams, otherParams);
                }
            }
        }
        return sum / (allFSSAlgorithms.size() - 1);
    }
}
