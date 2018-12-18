package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation;

import ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.classification.*;
import ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering.ClustererEvaluator;
import ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering.metric.ClustererResult;
import ru.ifmo.ctddev.FSSARecSys.alternative.internal.FSSClustering.FSSClusteringAlgorithm;
import ru.ifmo.ctddev.FSSARecSys.alternative.internal.FSSClustering.LaplasianScore;
import ru.ifmo.ctddev.FSSARecSys.alternative.internal.FSSClustering.SPEC;
import ru.ifmo.ctddev.FSSARecSys.db.internal.*;
import ru.ifmo.ctddev.FSSARecSys.db.manager.*;
import weka.clusterers.*;
import weka.core.EuclideanDistance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Сергей on 03.08.2015.
 */
public class Evaluator {
    private QueryManager queryManager;
    private List<String> listOfMetaFeatures;

    //for classification tasks
    private float alpha;
    private float betta;

    public Evaluator() {

    }

    public Evaluator(QueryManager queryManager, List<String> listOfFeatures) {
        this.queryManager = queryManager;
        this.listOfMetaFeatures = listOfFeatures;
    }

    public void setAlphaAndBeta(float alpha, float betta) {
        this.alpha = alpha;
        this.betta = betta;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V>
    sortByValue( Map<K, V> map )
    {
        Map<K,V> result = new LinkedHashMap<>();
        Stream<Map.Entry<K,V>> st = map.entrySet().stream();

        st.sorted(Comparator.comparing(e -> e.getValue()))
                .forEach(e ->result.put(e.getKey(),e.getValue()));

        return result;
    }

    public Double evaluate(FSSAlgorithm fssAlgorithm, Dataset dataset, MLAlgorithm mlAlgorithm) throws Exception {
        switch (dataset.getTaskType()){
            case "classification":
                if (queryManager.getDataset(dataset.getName()) == null) {
                    queryManager.addDataset(dataset);
                }
                ArrayList<FSSAlgorithm> availableFSSAlgorithms = queryManager.getAvailableFssAlgorithms();

                EARREvaluation  earrEvaluation = new EARREvaluation(alpha, betta, queryManager);
                return earrEvaluation.evaluate(fssAlgorithm, availableFSSAlgorithms, dataset, listOfMetaFeatures, mlAlgorithm);
            case "clusterisation":
                if (queryManager.getDataset(dataset.getName()) == null) {
                    queryManager.addDataset(dataset);
                }

                //dataset.getInstances().

                Clusterer as = AbstractClusterer.forName(mlAlgorithm.getClassPath(), weka.core.Utils.splitOptions(mlAlgorithm.getOptions()));
                ClustererEvaluator clustererEvaluator = new ClustererEvaluator(mlAlgorithm.getName(), as);
                ClustererResult clustererResult = clustererEvaluator.evaluate(dataset.getName(), dataset.getInstances());

                /*todo: add fss-shit for clustering (orly?)*/

                getEvaluation(dataset, clustererEvaluator, new LaplasianScore(dataset.getInstances(), new EuclideanDistance()));
                System.out.println();
                System.out.println("==================================================");
                System.out.println();
                getEvaluation(dataset, clustererEvaluator, new SPEC(dataset.getInstances(), new EuclideanDistance()));


                return clustererResult.countSquareDistance();

        }
        return null;
    }

    private void getEvaluation(Dataset dataset, ClustererEvaluator clustererEvaluator, FSSClusteringAlgorithm fssClusteringAlgorithm) throws Exception {
        System.out.println(fssClusteringAlgorithm.getName());
        //LaplasianScore laplasianScore = new LaplasianScore(dataset.getInstances(), new EuclideanDistance());

//        Map<Double, Integer> weightedFeatures = new TreeMap<>();
//        for (int i = 0; i < dataset.getInstances().numAttributes(); i++) {
//            Double val = fssClusteringAlgorithm.getFeatureWeight(i);
//            //System.out.println(val);
//            weightedFeatures.put(val, i);
//        }

        Map<Integer, Double> weightedFeatures = new TreeMap<>();
        for (int i = 0; i < dataset.getInstances().numAttributes(); i++) {
            Double val = fssClusteringAlgorithm.getFeatureWeight(i);
            //System.out.println(val);
            weightedFeatures.put(i, val);
        }

        weightedFeatures = sortByValue(weightedFeatures);

        for (Map.Entry<Integer, Double> entry : weightedFeatures.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

//        for (Map.Entry<Double, Integer> entry : weightedFeatures.entrySet()) {
//            System.out.println(entry.getKey() + " -> " + entry.getValue());
//        }

        ClustererResult clustererResult = clustererEvaluator.evaluate(dataset.getName(), dataset.getInstances());
        List<Integer> selectedFeatures = new ArrayList<>(weightedFeatures.keySet());

        for (int i = 1; i < selectedFeatures.size(); i++) {
            Instances localTest;

            //sets the array of features to be selected from in the dataset
            int numRemFeatures = i + 1;
            int [] deletedFeaturesArray = new int[numRemFeatures];

            for (int j = 0; j < numRemFeatures; j++) {
                deletedFeaturesArray[j] = selectedFeatures.get(j);
            }

            //Removal filter
            Remove rem = new Remove();
            rem.setAttributeIndicesArray(deletedFeaturesArray);
            rem.setInvertSelection(true);
            rem.setInputFormat(dataset.getInstances());
            localTest = Filter.useFilter(dataset.getInstances(), rem);

            clustererResult = clustererEvaluator.evaluate(dataset.getName(), localTest);
            System.out.print("first " + (i + 1) + " features: ");
            for (int j = 0; j < numRemFeatures; j++)
                System.out.print(deletedFeaturesArray[j] + ", ");
            System.out.println("");
            System.out.println(clustererResult.countSquareDistance());
        }
    }

    public static void main(String [] args)
    {
        File f = new File("simple.arff");
        Dataset dataset = new Dataset("car", f, "clusterisation");
//        Instances tmp = dataset.getInstances();
//        tmp.deleteStringAttributes();
//        dataset.


        MLAlgorithm ml = new MLAlgorithm("first_blood", "weka.clusterers.SimpleKMeans", "", "clusterisation");

        //QueryManager queryManager = null;
        //List<String> listOfFeatures = new ArrayList<String>();

        Evaluator e = new Evaluator();

        try {
            System.out.print("My evaluator: " + e.evaluate(null, dataset, ml));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
