package features_inversion.classification.dataset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ifmo.recommendersystem.metafeatures.MetaFeatureExtractor;

import features_inversion.classification.ArffConverter;
import features_inversion.classification.dataset.BinDataset;
import features_inversion.util.MetaFeaturesExtractor;
import weka.core.Instances;

public class DAMetaFeatureExtractor implements MetaFeaturesExtractor<BinDataset> {

    private final List<MetaFeatureExtractor> list = new ArrayList<MetaFeatureExtractor>();
    private final int n;

    public DAMetaFeatureExtractor(List<MetaFeatureExtractor> list) {
        this.list.addAll(list);
        this.n = this.list.size();
    }

    @Override
    public int numberOfFeatures() {
        return n;
    }

    @Override
    public double[] extract(BinDataset object) throws Exception {
        double[] features = new double[n];

        for (int i = 0; i < n; i++) {
            double value = list.get(i).extractValue(object);
            if (Double.isInfinite(value)) {
                throw new RuntimeException(i + " is INF");
            }
            if (Double.isNaN(value)) {
                throw new RuntimeException(i + " is NAN");
            }
            features[i] = value;
        }

        return features;
    }

}