package exclude.temp.aggr;

public class Kurt implements DoubleAggregation {

    @Override
    public double aggregate(double[] array) {

        int length = array.length;
        if (length == 0) {
            throw new IllegalArgumentException("Can't aggregate empty array");
        }

        double a = 0, b = 0, c = 0, d = 0;

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
            d += pwr;

        }

        a /= length;
        b /= length;
        c /= length;
        d /= length;
        double aa = a * a;
        double ac = a * c;

        double aab = aa * b;
        double aaaa = aa * aa;

        double cm2 = b - aa;
        double cm4 = d - 3 * aaaa + 6 * aab - 4 * ac;

        if (cm2 > 1e-8) {
            return cm4 / (cm2 * cm2);
        } else {
            return 0.0;
        }
    }

}
