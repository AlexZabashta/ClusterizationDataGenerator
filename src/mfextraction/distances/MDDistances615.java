package mfextraction.distances;

import mfextraction.CacheMF;
import mfextraction.MetaFeatureExtractor;

/**
 * Created by sergey on 04.05.16.
 */
public class MDDistances615 extends MetaFeatureExtractor {

    private double low = 0.0;
    private double high = 0.1;

    @Override
    public String getName() {
        return "MD" + 5 + 10 * high;
    }

    public void SetBounds(double vlow, double vhigh) {
        this.low = vlow;
        this.high = vhigh;
    }

    @Override
    public double extract(CacheMF cache) {
        double[] distances = cache.normalizedDistances();
        if (distances.length == 0) {
            return 100;
        }
        int ammount = 0;
        for (int i = 0; i < distances.length; i++) {
            if ((distances[i] >= low) && (distances[i] <= high)) {
                ammount++;
            }
        }
        return (100.0 * ((double) ammount / (double) distances.length));
    }
}
