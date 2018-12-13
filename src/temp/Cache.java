package temp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import com.ifmo.recommendersystem.metafeatures.decisiontree.WrappedC45DecisionTree;
import com.ifmo.recommendersystem.metafeatures.decisiontree.WrappedC45ModelSelection;

import features_inversion.classification.dataset.BinDataset;
import weka.classifiers.trees.j48.ModelSelection;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class Cache implements Serializable {

    final BinDataset dataset;

    private transient Instances instances = null;
    private transient WrappedC45DecisionTree decisionPTree = null;
    private transient WrappedC45DecisionTree decisionUTree = null;

    public Cache(BinDataset dataset) {
        this.dataset = Objects.requireNonNull(dataset);
    }

    public synchronized WrappedC45DecisionTree decisionPTree() throws Exception {
        if (decisionPTree == null) {
            Instances instances = WEKAInstances();
            ModelSelection modelSelection = new WrappedC45ModelSelection(instances);
            decisionPTree = new WrappedC45DecisionTree(modelSelection, true);
            decisionPTree.buildClassifier(instances);
        }
        return decisionPTree;
    }

    public synchronized WrappedC45DecisionTree decisionUTree() throws Exception {
        if (decisionUTree == null) {
            Instances instances = WEKAInstances();
            ModelSelection modelSelection = new WrappedC45ModelSelection(instances);
            decisionUTree = new WrappedC45DecisionTree(modelSelection, true);
            decisionUTree.buildClassifier(instances);
        }
        return decisionUTree;
    }

    public synchronized Instances WEKAInstances() {
        if (instances != null) {
            return instances;
        }

        ArrayList<Attribute> attributes = new ArrayList<Attribute>(dataset.numAttr + 1);
        for (int i = 0; i < dataset.numAttr; i++) {
            attributes.add(new Attribute("attr" + i));
        }
        ArrayList<String> classNames = new ArrayList<String>(2);
        classNames.add("neg");
        classNames.add("pos");
        Attribute classAttr = new Attribute("class", classNames);

        attributes.add(classAttr);

        instances = new Instances("name", attributes, dataset.neg.length + dataset.pos.length);

        instances.setClassIndex(dataset.numAttr);

        for (double[] inst : dataset.neg) {
            Instance instance = new DenseInstance(dataset.numAttr + 1);
            instance.setDataset(instances);
            for (int i = 0; i < dataset.numAttr; i++) {
                instance.setValue(i, inst[i]);
            }
            instance.setClassValue(classNames.get(0));
            instances.add(instance);
        }
        for (double[] inst : dataset.pos) {
            Instance instance = new DenseInstance(dataset.numAttr + 1);
            instance.setDataset(instances);
            for (int i = 0; i < dataset.numAttr; i++) {
                instance.setValue(i, inst[i]);
            }
            instance.setClassValue(classNames.get(1));
            instances.add(instance);
        }

        return instances;
    }

}
