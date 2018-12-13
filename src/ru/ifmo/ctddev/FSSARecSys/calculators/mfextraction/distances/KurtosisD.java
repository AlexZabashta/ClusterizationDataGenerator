package ru.ifmo.ctddev.FSSARecSys.calculators.mfextraction.distances;

import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import ru.ifmo.ctddev.FSSARecSys.calculators.mfextraction.MetaFeatureExtractor;
import ru.ifmo.ctddev.FSSARecSys.db.DataSet;
import weka.core.EuclideanDistance;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Created by sergey on 04.05.16.
 */
public class KurtosisD extends MetaFeatureExtractor {
    @Override
    public String getName() {
        return "MD5";
    }

    @Override
    public double extract(DataSet dataSet) throws Exception {
        Instances instances = dataSet.getInstances();

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
        double [] finalDist = new double[distances.size()];
        for (int i = 0; i < finalDist.length; i++){
            finalDist[i] = distances.get(i) / (max - min);
        }

        Kurtosis kurt = new Kurtosis();
        return kurt.evaluate(finalDist);
    }
}