package mfextraction.distances;

import weka.core.EuclideanDistance;
import weka.core.Instances;

import java.util.ArrayList;

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

        int ammount = 0;
        for (int i = 0; i < finalDist.length; i++) {
            finalDist[i] = distances.get(i) / (max - min);
            if ((finalDist[i] >= low) && (finalDist[i] <= high)) {
                ammount++;
            }
        }

        return (100.0 * ((double) ammount / (double) finalDist.length));
    }
}
