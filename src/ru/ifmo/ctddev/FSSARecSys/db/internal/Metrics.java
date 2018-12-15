package ru.ifmo.ctddev.FSSARecSys.db.internal;

import java.util.List;

/**
 * Created by sergey on 27.03.15.
 */
public class Metrics {
    FSSAlgorithm fssAlgorithm;
    String datasetName;
    List<String> listOfFeatures;

    public Metrics (FSSAlgorithm algo, String datasetName, List<String> listOfFeatures){
        this.fssAlgorithm = algo;
        this.datasetName = datasetName;
        this.listOfFeatures = listOfFeatures;
    }

    public FSSAlgorithm getFssAlgorithm() {
        return fssAlgorithm;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public List<String> getListOfFeatures() {
        return listOfFeatures;
    }
}
