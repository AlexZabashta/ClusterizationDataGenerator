package exclude.temp.aggr;

public class Max implements DoubleAggregation {

    @Override
    public double aggregate(double[] array) {
        double max = Double.NEGATIVE_INFINITY;
        int length = array.length;
        if (length == 0) {
            throw new IllegalArgumentException("Can't aggregate empty array");
        }
        for (int i = 0; i < length; i++) {
            double value = array[i];
            if (Double.isNaN(value)) {
                throw new IllegalArgumentException(i + " element is NaN");
            }
            if (Double.isInfinite(value)) {
                throw new IllegalArgumentException(i + " element is Infinite");
            }
            max = Math.max(max, value);
        }
        return max;
    }

}
