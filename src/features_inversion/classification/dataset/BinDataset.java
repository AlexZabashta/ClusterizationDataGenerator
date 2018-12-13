package features_inversion.classification.dataset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import com.ifmo.recommendersystem.metafeatures.decisiontree.WrappedC45DecisionTree;
import com.ifmo.recommendersystem.metafeatures.decisiontree.WrappedC45ModelSelection;

import dsgenerators.ListMetaFeatures;
import features_inversion.classification.dataset.mf.MetaFeatures;
import weka.classifiers.trees.j48.ModelSelection;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.Standardize;

public class BinDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    public static BinDataset fromInstances(Instances instances) {
        int numAttr = instances.numAttributes() - 1;

        int p = 0, n = 0;

        int len = instances.numInstances();

        if (instances.numClasses() != 2) {
            throw new IllegalArgumentException("Instances must have 2 classes");
        }

        for (int i = 0; i < len; i++) {
            if (instances.get(i).classValue() < 0.5) {
                ++n;
            } else {
                ++p;
            }
        }

        double[][] pos = new double[p][], neg = new double[n][];

        n = p = 0;

        for (int i = 0; i < len; i++) {

            Instance instance = instances.get(i);
            double[] vec = new double[numAttr];

            for (int j = 0; j < numAttr; j++) {
                vec[j] = instance.value(j);
            }

            if (instances.get(i).classValue() < 0.5) {
                neg[n++] = vec;
            } else {
                pos[p++] = vec;
            }
        }

        return new BinDataset(pos, neg, numAttr);
    }

    static Instances normalize(Instances instances) {
        try {
            Standardize standardize = new Standardize();
            standardize.setInputFormat(instances);
            return Filter.useFilter(instances, standardize);
        } catch (Exception e0) {
            System.err.println(e0.getLocalizedMessage());
            try {
                Normalize normalize = new Normalize();
                normalize.setScale(2);
                normalize.setTranslation(-1);
                normalize.setInputFormat(instances);
                return Filter.useFilter(instances, normalize);
            } catch (Exception e1) {
                System.err.println(e1.getLocalizedMessage());
                return instances;
            }
        }
    }

    private transient WrappedC45DecisionTree decisionPTree = null;

    private transient WrappedC45DecisionTree decisionUTree = null;
    private transient Instances instances = null;
    private final Object lock = new Object();

    final double[] metaFeatures = new double[128];

    public final int numAttr;

    public final double[][] pos, neg;

    public BinDataset(double[][] pos, double[][] neg, int numAttr) {

        if (pos.length >= neg.length) {
            this.pos = pos;
            this.neg = neg;
        } else {
            this.pos = neg;
            this.neg = pos;
        }

        if (numAttr <= 0) {
            throw new IllegalArgumentException("numAttr must be > 0");
        }

        if (pos.length == 0 || neg.length == 0) {
            throw new IllegalArgumentException("'pos' and 'neg' length must be > 0");
        }

        for (double[] inst : neg) {
            if (inst.length < numAttr) {
                throw new IllegalStateException("Some instnce contains < numAttr");
            }
        }

        for (double[] inst : pos) {
            if (inst.length < numAttr) {
                throw new IllegalStateException("Some instnce contains < numAttr");
            }
        }

        this.numAttr = numAttr;

        Arrays.fill(metaFeatures, Double.NaN);
    }

    public WrappedC45DecisionTree decisionPTree() throws Exception {
        if (decisionPTree == null) {
            synchronized (lock) {
                if (decisionPTree == null) {
                    Instances instances = WEKAInstances();
                    ModelSelection modelSelection = new WrappedC45ModelSelection(instances);
                    decisionPTree = new WrappedC45DecisionTree(modelSelection, true);
                    decisionPTree.buildClassifier(instances);
                }
            }
        }
        return decisionPTree;
    }

    public WrappedC45DecisionTree decisionUTree() throws Exception {
        if (decisionUTree == null) {
            synchronized (lock) {
                if (decisionUTree == null) {
                    Instances instances = WEKAInstances();
                    ModelSelection modelSelection = new WrappedC45ModelSelection(instances);
                    decisionUTree = new WrappedC45DecisionTree(modelSelection, false);
                    decisionUTree.buildClassifier(instances);
                }
            }
        }
        return decisionUTree;
    }

    public double getMetaFeature(int index) {
        if (Double.isNaN(metaFeatures[index])) {
            synchronized (metaFeatures) {
                if (Double.isNaN(metaFeatures[index])) {
                    metaFeatures[index] = ListMetaFeatures.extractValue(index, this);
                }
            }
        }
        return metaFeatures[index];
    }

    public Instances WEKAInstances() {
        if (instances == null) {
            synchronized (lock) {
                if (instances == null) {

                    ArrayList<Attribute> attributes = new ArrayList<Attribute>(numAttr + 1);
                    for (int i = 0; i < numAttr; i++) {
                        attributes.add(new Attribute("attr" + i));
                    }
                    ArrayList<String> classNames = new ArrayList<String>(2);
                    classNames.add("neg");
                    classNames.add("pos");
                    Attribute classAttr = new Attribute("class", classNames);

                    attributes.add(classAttr);

                    instances = new Instances("name", attributes, neg.length + pos.length);

                    instances.setClassIndex(numAttr);

                    for (double[] inst : neg) {
                        Instance instance = new DenseInstance(numAttr + 1);
                        instance.setDataset(instances);
                        for (int i = 0; i < numAttr; i++) {
                            instance.setValue(i, inst[i]);
                        }
                        instance.setClassValue(classNames.get(0));
                        instances.add(instance);
                    }
                    for (double[] inst : pos) {
                        Instance instance = new DenseInstance(numAttr + 1);
                        instance.setDataset(instances);
                        for (int i = 0; i < numAttr; i++) {
                            instance.setValue(i, inst[i]);
                        }
                        instance.setClassValue(classNames.get(1));
                        instances.add(instance);
                    }

                    instances = normalize(instances);
                }
            }
        }

        return instances;
    }
}
