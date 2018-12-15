package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.classification;

import ru.ifmo.ctddev.FSSARecSys.ClassifierResult;
import ru.ifmo.ctddev.FSSARecSys.utils.Pair;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Objects;

/**
 * Created by Администратор on 10.08.2015.
 */
public class ClassifierEvaluator {
    private final String name;
    private final Classifier classifier;

    public ClassifierEvaluator(String name, Classifier classifier) {
        this.name = Objects.requireNonNull(name);
        this.classifier = Objects.requireNonNull(classifier);
    }

    public String getName() {
        return name;
    }

    public ClassifierResult evaluate(String dataSetName, Instances train, Instances test) {
        int[][] confusionMatrix = new int[train.numClasses()][train.numClasses()];

        try {
            Classifier localClassifier = Classifier.makeCopy(classifier);
            localClassifier.buildClassifier(train);
            for (int i = 0; i < test.numInstances(); i++) {
                Instance instance = test.instance(i);
                if (!instance.classIsMissing()) {
                    int classIndex = (int) localClassifier.classifyInstance(instance);
                    confusionMatrix[classIndex][(int) instance.classValue()]++;
                }
            }
            Pair<Double, Double> result = computeAccuracyAndF1Measure(confusionMatrix);
            return new ClassifierResult(dataSetName, name, result.first, result.second);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ClassifierResult(dataSetName, name, 0, 0);
    }

    /**
     * @param confusionMatrix result confusion matrix after classifier evaluation
     * @return pair of accuracy and f1 measure
     */
    public static Pair<Double, Double> computeAccuracyAndF1Measure(int[][] confusionMatrix) {
        if (confusionMatrix == null) {
            throw new IllegalArgumentException("confusionMatrix must be not null");
        }
        if (confusionMatrix.length != confusionMatrix[0].length) {
            throw new IllegalArgumentException("confusionMatrix must be square");
        }
        int size = confusionMatrix.length;
        int precisionCount = 0;
        int recallCount = 0;
        double precision = 0;
        double recall = 0;
        int correct = 0;
        int sumAll = 0;
        for (int i = 0; i < size; i++) {
            correct += confusionMatrix[i][i];
            int sum = 0;
            for (int j = 0; j < size; j++) {
                sum += confusionMatrix[i][j];
            }
            sumAll += sum;
            if (sum != 0) {
                precision += confusionMatrix[i][i] / (double) sum;
                precisionCount++;
            }
            sum = 0;
            for (int j = 0; j < size; j++) {
                sum += confusionMatrix[j][i];
            }
            if (sum != 0) {
                recall += confusionMatrix[i][i] / (double) sum;
                recallCount++;
            }

        }
        if (precisionCount != 0) {
            precision /= precisionCount;
        }
        if (recallCount != 0) {
            recall /= recallCount;
        }
        double accuracy = 0;
        if (sumAll != 0) {
            accuracy = correct / (double) sumAll;
        }
        return Pair.of(accuracy, computeF1Measure(precision, recall));

    }

    public static double computeF1Measure(double precision, double recall) {
        if (precision + recall == 0) {
            return 0;
        }
        return 2 * precision * recall / (precision + recall);
    }
}
