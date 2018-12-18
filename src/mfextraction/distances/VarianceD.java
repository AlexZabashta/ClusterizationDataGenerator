package mfextraction.distances;

import java.util.ArrayList;

import org.apache.commons.math3.stat.descriptive.moment.Variance;

import mfextraction.MetaFeatureExtractor;
import weka.core.EuclideanDistance;
import weka.core.Instances;

/**
 * Created by sergey on 04.05.16.
 */
public class VarianceD extends MetaFeatureExtractor {
    @Override
    public String getName() {
        return "MD2";
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

        Variance v = new Variance();
        return v.evaluate(finalDist);
    }
}