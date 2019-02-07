package mfextraction.distances;

import org.apache.commons.math3.stat.descriptive.moment.Variance;

import mfextraction.CacheMF;
import mfextraction.MetaFeatureExtractor;

/**
 * Created by sergey on 04.05.16.
 */
public class VarianceD extends MetaFeatureExtractor {
    @Override
    public String getName() {
        return "MD2";
    }

    @Override
    public double extract(CacheMF cache) {
        double[] finalDist = cache.normalizedDistances();
        Variance v = new Variance();
        return v.evaluate(finalDist);
    }
}
