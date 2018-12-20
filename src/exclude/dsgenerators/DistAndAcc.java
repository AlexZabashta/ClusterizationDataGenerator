package exclude.dsgenerators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import exclude.features_inversion.classification.dataset.BinDataset;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class DistAndAcc implements ErrorFunction {

    final int[] mfIndices;
    final double[] target;
    final double[] weight;
    final int length;

    private Classifier classifier;
    private Instances format;

    @Override
    public String toString() {
        return classifier.getClass().getSimpleName();
    }

    public double test(BinDataset dataset) {
        Instance instance = new DenseInstance(length + 1);

        synchronized (format) {
            instance.setDataset(format);
        }

        for (int i = 0; i < length; i++) {
            instance.setValue(i, dataset.getMetaFeature(mfIndices[i]));
        }
        // 79, 80

        try {
            double[] d;

            synchronized (classifier) {
                d = classifier.distributionForInstance(instance);
            }

            double x = dataset.getMetaFeature(79);
            double y = dataset.getMetaFeature(80);

            double norm = x + y;

            if (norm < 0.2) {
                return 1 + 1 / (norm + 0.07);
            }
            x /= norm;
            y /= norm;

            double max = Math.max(d[0], d[1]);

            return (max - Math.abs(d[0] - x)) / max;

        } catch (Exception e) {
            return 1000;
        }

    }

    public DistAndAcc(double[] target, double[] weight, int[] mfIndices, Classifier classifier, List<BinDataset> datasets) throws Exception {
        this.mfIndices = mfIndices.clone();
        this.target = target.clone();
        this.weight = weight.clone();
        this.length = mfIndices.length;

        if (target.length != length || weight.length != length) {
            throw new IllegalArgumentException("The target, weight, and mfIndices should have the same length");
        }

        this.classifier = classifier;

        ArrayList<Attribute> attributes = new ArrayList<>(length + 1);
        for (int i = 0; i < length; i++) {
            attributes.add(new Attribute("mf" + mfIndices[i]));
        }
        attributes.add(new Attribute("class", Arrays.asList("s", "n")));

        format = new Instances("meta", attributes, datasets.size());
        format.setClassIndex(length);

        for (BinDataset dataset : datasets) {

            double s = dataset.getMetaFeature(79);
            double n = dataset.getMetaFeature(80);

            if (Math.max(s, n) < 0.2) {
                continue;
            }

            double m = s + n;
            s /= m;
            n /= m;

            if (Math.abs(s - n) < 0.1) {
                continue;
            }

            Instance instance = new DenseInstance(length + 1);
            instance.setDataset(format);

            for (int i = 0; i < length; i++) {
                instance.setValue(i, dataset.getMetaFeature(mfIndices[i]));
            }

            if (s > n) {
                instance.setClassValue("s");
            } else {
                instance.setClassValue("n");
            }

            format.add(instance);
        }

        System.out.println(format.numInstances());

        classifier.buildClassifier(format);
    }

    public double best = Double.POSITIVE_INFINITY;

    public double evaluate(BinDataset dataset) {
        double sumOfSquares = 0;

        for (int i = 0; i < length; i++) {
            double diff = (target[i] - dataset.getMetaFeature(mfIndices[i])) * weight[i];
            sumOfSquares += diff * diff;
        }

        return Math.sqrt(sumOfSquares) + 10 * test(dataset);

    }

    @Override
    public double[] componentwise(BinDataset dataset) throws EndSearch {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int length() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double aggregate(double[] vector) {
        // TODO Auto-generated method stub
        return 0;
    }

}
