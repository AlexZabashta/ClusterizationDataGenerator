package ru.ifmo.ctddev.FSSARecSys.utils;

import ru.ifmo.ctddev.FSSARecSys.db.internal.Dataset;
import ru.ifmo.ctddev.FSSARecSys.db.manager.QueryManager;

import java.util.*;

/**
 * Created by Сергей on 03.08.2015.
 */
public class MFVector {
    //MF_name, value
    private Map<String, Double> metaFeatures;
    private Dataset dataset;

    public MFVector(List<String> mfNames, QueryManager queryManager, Dataset dataset) throws Exception {
        metaFeatures = new TreeMap<>();
        this.dataset = dataset;
        for (String mfName: mfNames) {
            metaFeatures.put(mfName, queryManager.getMFforDataset(dataset.getName(), mfName));
        }
    }

    public Map<String, Double> getValues(){
        return metaFeatures;
    }

}
