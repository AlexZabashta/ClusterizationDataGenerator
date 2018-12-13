package wtf.clsf;

import com.ifmo.recommendersystem.metafeatures.MetaFeatureExtractor;

import weka.core.Instances;
import wtf.MetaFeaturesExtractor;

public class DataSetMetaFeatures implements MetaFeaturesExtractor<Dataset> {

    private final MetaFeatureExtractor[] extractors;

    public DataSetMetaFeatures(MetaFeatureExtractor[] extractors) {
        this.extractors = extractors.clone();
    }

    @Override
    public double[] apply(Dataset dataset) {
        int len = length();

        Instances instances = WekaConverter.convert(dataset);

        double[] vector = new double[len];
        for (int i = 0; i < len; i++) {
            try {
                vector[i] = extractors[i].extractValue(instances);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return vector;
    }

    @Override
    public int length() {
        return extractors.length;
    }

}
