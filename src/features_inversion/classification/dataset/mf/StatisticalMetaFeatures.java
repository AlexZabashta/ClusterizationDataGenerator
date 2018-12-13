package features_inversion.classification.dataset.mf;

import com.ifmo.recommendersystem.utils.StatisticalUtils;

import features_inversion.classification.dataset.BinDataset;

public class StatisticalMetaFeatures {

    static final int MEAN_KURTOSIS = MetaFeatures.MEAN_KURTOSIS.ordinal();
    static final int MEAN_SKEWNESS = MetaFeatures.MEAN_SKEWNESS.ordinal();
    static final int MEAN_LINEAR_CORRELATION_COEFFICIENT = MetaFeatures.MEAN_LINEAR_CORRELATION_COEFFICIENT.ordinal();

    public static int extract(BinDataset datasets, double[] features, int index, boolean[] mask) {

        if (mask[MEAN_LINEAR_CORRELATION_COEFFICIENT] | mask[MEAN_KURTOSIS] | mask[MEAN_SKEWNESS]) {
            int numAttr = datasets.numAttr;
            double[][] values = new double[datasets.numAttr][datasets.neg.length + datasets.neg.length];

            {
                int id = 0;

                for (double[] inst : datasets.pos) {
                    for (int aid = 0; aid < numAttr; aid++) {
                        values[aid][id] = inst[aid];
                    }
                    ++id;
                }
                for (double[] inst : datasets.neg) {
                    for (int aid = 0; aid < numAttr; aid++) {
                        values[aid][id] = inst[aid];
                    }
                    ++id;
                }
            }

            if (mask[MEAN_SKEWNESS] | mask[MEAN_KURTOSIS]) {

                double meanSkew = 0, meanKurt = 0;
                for (int aid = 0; aid < numAttr; aid++) {
                    double a = 0, b = 0, c = 0, d = 0;
                    int cnt = 0;

                    for (double val : values[aid]) {
                        double pwr = val;
                        cnt += 1;
                        a += pwr;
                        pwr *= val;
                        b += pwr;
                        pwr *= val;
                        c += pwr;
                        pwr *= val;
                        d += pwr;
                    }

                    if (cnt > 0) {
                        a /= cnt;
                        b /= cnt;
                        c /= cnt;
                        d /= cnt;
                        double aa = a * a;
                        double ab = a * b;
                        double ac = a * c;

                        double aaa = aa * a;
                        double aab = aa * b;
                        double aaaa = aa * aa;

                        double cm2 = b - aa;
                        double cm3 = c + 2 * aaa - 3 * ab;
                        double cm4 = d - 3 * aaaa + 6 * aab - 4 * ac;

                        if (cm2 > 1e-8) {
                            meanSkew += cm3 / Math.pow(cm2, 1.5);
                            meanKurt += cm4 / (cm2 * cm2);
                        }
                    }
                }

                if (numAttr > 0) {
                    meanSkew /= numAttr;
                    meanKurt /= numAttr;
                }

                if (mask[MEAN_SKEWNESS]) {
                    features[index++] = meanSkew;
                }

                if (mask[MEAN_KURTOSIS]) {
                    features[index++] = meanKurt;
                }
            }

            if (mask[MEAN_LINEAR_CORRELATION_COEFFICIENT]) {

                double meanCor = 0;
                int count = 0;

                for (int i = 0; i < numAttr; i++) {
                    double[] values1 = values[i];
                    for (int j = i + 1; j < numAttr; j++) {
                        double[] values2 = values[j];
                        double linearCorrelationCoefficient = StatisticalUtils.linearCorrelationCoefficient(values1, values2);
                        if (Double.isFinite(linearCorrelationCoefficient)) {
                            meanCor += linearCorrelationCoefficient;
                            count++;
                        }
                    }
                }

                if (count > 0) {
                    meanCor /= count;
                }
                features[index++] = meanCor;
            }
        }

        return index;
    }

}
