package exclude.dsgenerators;

import exclude.features_inversion.classification.dataset.BinDataset;

public class Limited implements ErrorFunction {

    public final ErrorFunction function;

    public BinDataset dataset = null;
    public double best = Double.POSITIVE_INFINITY;
    public final double[] log;
    public int qid = 0;

    public Limited(ErrorFunction function, int limit) {
        this.function = function;
        log = new double[limit];
    }

    @Override
    public double aggregate(double[] vector) {
        return function.aggregate(vector);
    }

    @Override
    public double[] componentwise(BinDataset dataset) throws EndSearch {
        if (qid >= log.length) {
            throw new EndSearch();
        }

        double[] vector = function.componentwise(dataset);

        double value;
        if (function.length() == 1) {
            value = vector[0];
        } else {
            value = function.aggregate(vector);
        }

        if (value < best) {
            best = value;
            this.dataset = dataset;
        }

        log[qid++] = value;

        if (qid >= log.length) {
            throw new EndSearch();
        }

        return vector;
    }

    @Override
    public int length() {
        return function.length();
    }

}
