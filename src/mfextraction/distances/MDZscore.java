package mfextraction.distances;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import mfextraction.MetaFeatureExtractor;
import weka.core.EuclideanDistance;
import weka.core.Instances;

import java.util.ArrayList;

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
    public double extract(Instances instances) throws Exception {

        EuclideanDistance d = new EuclideanDistance(instances);

        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;

        ArrayList<Double> distances = new ArrayList<>();
        for (int i = 0; i < instances.numInstances(); i++) {
            for (int j = i + 1; j < instances.numInstances(); j++) {
                double dist = d.distance(instances.instance(i), instances.instance(j));

                max = max < dist ? dist : max;
                min = min > dist ? dist : min;

                distances.add(dist);
            }
        }
        double[] finalDist = new double[distances.size()];
        for (int i = 0; i < finalDist.length; i++) {
            finalDist[i] = distances.get(i) / (max - min);
        }

        Mean m = new Mean();
        double mean = m.evaluate(finalDist);

        StandardDeviation sd = new StandardDeviation();
        double std = sd.evaluate(finalDist);

        int ammount = 0;
        double value = 0.0;
        for (int i = 0; i < finalDist.length; i++) {
            value = (finalDist[i] - mean) / std;
            if ((value >= low) && (value < high)) {
                ammount++;
            }
        }

        return (100.0 * ((double) ammount / (double) finalDist.length));

    }
}
