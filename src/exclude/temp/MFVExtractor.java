package exclude.temp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import exclude.features_inversion.classification.dataset.BinDataset;

public class MFVExtractor {
    final List<ScalarExtractor> extractors = new ArrayList<>();

    public final int lenght;

    public MFVExtractor(Collection<ScalarExtractor> extractors) {
        for (ScalarExtractor extractor : extractors) {
            this.extractors.add(Objects.requireNonNull(extractor));
        }
        lenght = this.extractors.size();
    }

    public List<ScalarExtractor> getExtractors() {
        return new ArrayList<ScalarExtractor>(extractors);
    }

    public double[] extract(BinDataset dataset) {
        Cache cache = new Cache(dataset);
        double[] features = new double[lenght];

        for (int featureId = 0; featureId < lenght; featureId++) {
            features[featureId] = extractors.get(featureId).extract(cache);
        }

        return features;
    }

}
