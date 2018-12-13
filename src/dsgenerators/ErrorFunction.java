package dsgenerators;

import features_inversion.classification.dataset.BinDataset;

public interface ErrorFunction {

    public double[] componentwise(BinDataset dataset) throws EndSearch;

    public double aggregate(double[] vector);

    public int length();

}
