package exclude.dsgenerators;

import exclude.features_inversion.classification.dataset.BinDataset;

public class EuclideanDist implements ErrorFunction {

    final int[] mfIndices;
    final double[] target;
    final double[] weight;
    final int length;
    final boolean multiObjective;

    public EuclideanDist(double[] target, double[] weight, int[] mfIndices, boolean multiObjective) {
        this.mfIndices = mfIndices.clone();
        this.target = target.clone();
        this.weight = weight.clone();
        this.length = mfIndices.length;
        this.multiObjective = multiObjective;

        if (target.length != length || weight.length != length) {
            throw new IllegalArgumentException("The target, weight, and mfIndices should have the same length");
        }

    }

    @Override
    public double[] componentwise(BinDataset dataset) throws EndSearch {
        double[] vector = new double[length];
        for (int i = 0; i < length; i++) {
            vector[i] = Math.abs(target[i] - dataset.getMetaFeature(mfIndices[i]));
        }

        if (multiObjective) {
            return vector;
        } else {
            return new double[] { aggregate(vector) };
        }
    }

    @Override
    public double aggregate(double[] vector) {
        double sumOfSquares = 0;
        for (int i = 0; i < length; i++) {
            double diff = vector[i] * weight[i];
            sumOfSquares += diff * diff;
        }
        return sumOfSquares;
    }

    @Override
    public int length() {
        if (multiObjective) {
            return length;
        } else {
            return 1;
        }
    }

}
