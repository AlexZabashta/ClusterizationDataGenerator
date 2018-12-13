package temp.aggr;

public class Skewn implements DoubleAggregation {

    @Override
    public double aggregate(double[] array) {

        int length = array.length;
        if (length == 0) {
            throw new IllegalArgumentException("Can't aggregate empty array");
        }

        double a = 0, b = 0, c = 0;

        for (int i = 0; i < length; i++) {
            double value = array[i];
            if (Double.isNaN(value)) {
                throw new IllegalArgumentException(i + " element is NaN");
            }
            if (Double.isInfinite(value)) {
                throw new IllegalArgumentException(i + " element is Infinite");
            }

            double pwr = value;
            a += pwr;
            pwr *= value;
            b += pwr;
            pwr *= value;
            c += pwr;
            pwr *= value;

        }

        a /= length;
        b /= length;
        c /= length;

        double aa = a * a;
        double ab = a * b;

        double aaa = aa * a;
        double cm2 = b - aa;
        double cm3 = c + 2 * aaa - 3 * ab;

        if (cm2 > 1e-8) {
            return cm3 / Math.pow(cm2, 1.5);
        } else {
            return 0.0;
        }
    }

}
