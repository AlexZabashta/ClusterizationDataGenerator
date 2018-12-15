package ru.ifmo.ctddev.FSSARecSys.db.internal;

import ru.ifmo.ctddev.FSSARecSys.calculators.ClassifierEvaluator;
import ru.ifmo.ctddev.FSSARecSys.calculators.internal.ClassifierEvaluatorImpl;
import ru.ifmo.ctddev.FSSARecSys.db.ClassifierManager;
import weka.classifiers.Classifier;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* Created by warrior on 03.03.15.
*/
public class SimpleClassifierManager implements ClassifierManager {

    public static final String NAIVE_BAYES = "NaiveBayes";

    private static final String NAIVE_BAYES_CLASS = "weka.classifiers.bayes.NaiveBayes";

    private static SimpleClassifierManager simpleClassifierManager;

    private final List<String> names = new ArrayList<>();
    private final Map<String, ClassifierEvaluator> nameToClassifier = new HashMap<>();

    private SimpleClassifierManager() {
        register(NAIVE_BAYES, NAIVE_BAYES_CLASS);
    }

    @Override
    public List<String> getAvailableClassifierNames() {
        return new ArrayList<>(names);
    }

    @Override
    public ClassifierEvaluator get(String name) {
        return nameToClassifier.get(name);
    }

    @Override
    public boolean register(String name, String className, String[] options) {
        try {
            Classifier classifier = Classifier.forName(className, options);
            names.add(name);
            nameToClassifier.put(name, new ClassifierEvaluatorImpl(name, classifier));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean register(String name, String className, String[] options, File jarFile) {
        return false;
    }

    public static SimpleClassifierManager getInstance() {
        if (simpleClassifierManager == null) {
            simpleClassifierManager = new SimpleClassifierManager();
        }
        return simpleClassifierManager;
    }
}
