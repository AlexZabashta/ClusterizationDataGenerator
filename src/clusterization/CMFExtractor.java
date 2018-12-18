package clusterization;

import java.util.ArrayList;
import java.util.List;

import mfextraction.MetaFeatureExtractor;
import weka.core.Instances;

public class CMFExtractor implements MetaFeaturesExtractor {

    List<MetaFeatureExtractor> extractors = new ArrayList<>();

    @Override
    public int lenght() {
        return extractors.size();
    }

    @Override
    public double[] extract(Dataset dataset) {

        try {

            int n = lenght();
            double[] values = new double[n];

            for (int i = 0; i < n; i++) {
                values[i] = extractors.get(i).extract(dataset.toInstances());
            }

            return values;

        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public String name(int index) {
        return MetaFeaturesExtractor.super.name(index);
    }

}
