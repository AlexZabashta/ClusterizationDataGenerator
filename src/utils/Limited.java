package utils;

import java.util.function.ToDoubleFunction;

import clusterization.Dataset;

public class Limited implements ToDoubleFunction<Dataset> {

    public final ToDoubleFunction<Dataset> function;

    public Dataset dataset = null;
    public double best = Double.POSITIVE_INFINITY;
    public final double[] log;
    public int qid = 0;

    public Limited(ToDoubleFunction<Dataset> function, int limit) {
        this.function = function;
        log = new double[limit];
    }

    @Override
    public double applyAsDouble(Dataset dataset) {
        if (qid >= log.length) {
            throw new EndSearch();
        }
        double value = function.applyAsDouble(dataset);
        if (value < best) {
            best = value;
            this.dataset = dataset;
        }
        log[qid++] = value;
        return value;
    }
}
