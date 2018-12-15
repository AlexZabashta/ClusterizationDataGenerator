package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.classification;

import ru.ifmo.ctddev.FSSARecSys.FSSResult;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.core.Instances;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Created by Администратор on 10.08.2015.
 */
public class FSSClassificationEvaluator {

    private final String name;
    private final ASSearch search;
    private final ASEvaluation evaluation;

    public FSSClassificationEvaluator(String name, ASSearch search, ASEvaluation evaluation) {
        this.name = Objects.requireNonNull(name);
        this.search = Objects.requireNonNull(search);
        this.evaluation = Objects.requireNonNull(evaluation);
//        try {
//            Object a = Class.forName("weka.attributeSelection.ASSearch");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    public String getName() {
        return name;
    }

    public FSSResult run(String dataSetName, Instances instances) {
        ASSearch localSearch = null;
        ASEvaluation localEvaluation = null;
        try {
            localSearch = ASSearch.makeCopies(search, 1)[0];
            localEvaluation = ASEvaluation.makeCopies(evaluation, 1)[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (localSearch != null && localEvaluation != null) {
            try {
                Instant start = Instant.now();
                int[] selectedAttrs = localSearch.search(localEvaluation, instances);
                Instant end = Instant.now();
                return new FSSResult(dataSetName, name, selectedAttrs, Duration.between(start, end).toMillis());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new FSSResult(dataSetName, name, new int[0], 0);
    }
}
