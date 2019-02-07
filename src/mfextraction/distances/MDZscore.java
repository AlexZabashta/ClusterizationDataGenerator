package mfextraction.distances;

import mfextraction.CacheMF;
import mfextraction.MetaFeatureExtractor;

/**
 * Created by sergey on 04.05.16.
 */
public class MDZscore extends MetaFeatureExtractor {

    private double low = 0.0;
    private double high = 1.0;

    @Override
    public String getName() {
        return "MD-Z-score" + 15 + (low + 1);
    }

    public void setBounds(double low, double high) {
        this.low = low;
        this.high = high;
    }

    @Override
    public double extract(CacheMF cache) {
        double[] distances = cache.standartializedDistances();
        if (distances.length == 0) {
            return 100;
        }
        int ammount = 0;
        double value = 0.0;
        for (int i = 0; i < distances.length; i++) {
            value = distances[i];
            if ((value >= low) && (value < high)) {
                ammount++;
            }
        }
        return (100.0 * ((double) ammount / (double) distances.length));
    }
}
