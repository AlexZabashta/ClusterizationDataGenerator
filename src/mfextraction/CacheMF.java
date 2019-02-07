package mfextraction;

import java.util.Arrays;
import java.util.Objects;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import clusterization.Dataset;
import weka.core.EuclideanDistance;
import weka.core.Instances;

public class CacheMF {

    public CacheMF(Dataset dataset) {
        this.dataset = Objects.requireNonNull(dataset);
    }

    private final Dataset dataset;

    public Dataset dataset() {
        return dataset;
    }

    private Instances instances;

    public Instances instances() {
        if (instances == null) {
            instances = dataset.toInstances();
            if (instances == null) {
                throw new IllegalStateException("instances == null");
            }
        }
        return instances;
    }

    private double[][] attributesToDoubleArrays;

    public double[][] attributeToDoubleArray() {
        if (attributesToDoubleArrays == null) {
            Instances instances = instances();
            int numAttributes = instances.numAttributes();

            attributesToDoubleArrays = new double[numAttributes][];
            for (int index = 0; index < numAttributes; index++) {
                attributesToDoubleArrays[index] = instances.attributeToDoubleArray(index);
            }
        }
        return attributesToDoubleArrays;
    }

    private EuclideanDistance euclideanDistance;

    public EuclideanDistance euclideanDistance() {
        if (euclideanDistance == null) {
            euclideanDistance = new EuclideanDistance(instances());
        }
        return euclideanDistance;
    }

    private double[] distances;

    public double[] distances() {
        if (distances == null) {
            Instances instances = instances();
            int n = instances.size();
            EuclideanDistance d = euclideanDistance();
            distances = new double[n * (n - 1) / 2];

            for (int p = 0, i = 0; i < n; i++) {
                for (int j = 0; j < i; j++, p++) {
                    double dist = d.distance(instances.instance(i), instances.instance(j));
                    if (!Double.isFinite(dist)) {
                        throw new IllegalStateException("distance is not finite");
                    }
                    distances[p] = dist;
                }
            }
        }
        return distances;
    }

    private double[] normalizedDistances;

    public double[] normalizedDistances() {
        if (normalizedDistances == null) {
            normalizedDistances = distances().clone();

            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;

            for (int i = 0; i < normalizedDistances.length; i++) {
                min = Math.min(min, normalizedDistances[i]);
                max = Math.max(max, normalizedDistances[i]);
            }

            double scale = 1 / (max - min);

            if (Double.isFinite(scale) && scale < 1e9) {
                for (int i = 0; i < normalizedDistances.length; i++) {
                    normalizedDistances[i] *= scale;
                }
            } else {
                Arrays.fill(normalizedDistances, 0);
            }
        }
        return normalizedDistances;
    }

    private double[] standartializedDistances;

    public double[] standartializedDistances() {
        if (standartializedDistances == null) {
            standartializedDistances = distances().clone();

            StandardDeviation sd = new StandardDeviation();
            double scale = 1 / sd.evaluate(standartializedDistances);

            Mean m = new Mean();
            double mean = m.evaluate(standartializedDistances);

            if (Double.isFinite(scale) && scale < 1e9) {
                for (int i = 0; i < standartializedDistances.length; i++) {
                    standartializedDistances[i] -= mean;
                    standartializedDistances[i] *= scale;
                }
            } else {
                Arrays.fill(standartializedDistances, 0);
            }
        }
        return standartializedDistances;
    }

    private KmeansResult kmeansResult;

    public KmeansResult kmeansResult() {
        if (kmeansResult == null) {
            try {
                kmeansResult = new KmeansResult(this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return kmeansResult;
    }

}
