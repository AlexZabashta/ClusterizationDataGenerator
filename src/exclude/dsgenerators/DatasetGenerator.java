package exclude.dsgenerators;

import java.util.List;

import exclude.features_inversion.classification.dataset.BinDataset;

public interface DatasetGenerator {
    public List<BinDataset> generate(int numAttributes, int numPosInstances, int numNegInstances, ErrorFunction error, int numDatasets, int limit);
}
