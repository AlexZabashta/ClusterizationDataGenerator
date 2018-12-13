package wtf.clsf;

import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.Standardize;

public class WekaConverter {
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

    public static Dataset convert(Instances instances) {
        int classIndex = instances.classIndex();

        if (classIndex < 0) {
            throw new IllegalArgumentException("classIndex not set");
        }

        // instances = normalize(instances);

        int numAttr = instances.numAttributes() - 1;

        if (numAttr <= 0) {
            throw new IllegalArgumentException("numAttr <= 0");
        }

        int numCatAttr = 0;

        boolean[] isCat = new boolean[numAttr + 1];

        for (int j = 0; j <= numAttr; j++) {
            Attribute attribute = instances.attribute(j);
            isCat[j] = attribute.isNominal();

            if (j == classIndex) {
                if (!isCat[j]) {
                    throw new IllegalArgumentException("class not nominal");
                }
            } else {
                if (isCat[j]) {
                    ++numCatAttr;
                }
            }

        }

        int numObjects = instances.numInstances();
        int numRatAttr = numAttr - numCatAttr;
        int numClasses = instances.numClasses();
        MutableDataset dataset = new MutableDataset(numObjects, numRatAttr, numCatAttr, numClasses);

        for (int i = 0; i < numObjects; i++) {
            Instance instance = instances.instance(i);

            int cid = 0, rid = 0;

            for (int j = 0; j <= numAttr; j++) {
                if (j == classIndex) {
                    continue;
                }

                if (isCat[j]) {
                    dataset.setCatValue(i, cid++, (int) instance.value(j));
                } else {
                    dataset.setRatValue(i, rid++, instance.value(j));
                }
            }

            dataset.setClassValue(i, (int) instance.classValue());
        }

        return dataset;
    }

    public static Instances convert(Dataset dataset) {
        ArrayList<Attribute> attributes = new ArrayList<Attribute>(dataset.numAttr() + 1);

        for (int j = 0; j < dataset.numRatAttr(); j++) {
            attributes.add(new Attribute("r" + j));
        }

        for (int j = 0; j < dataset.numCatAttr(); j++) {
            int size = dataset.categorySize(j);
            ArrayList<String> values = new ArrayList<>(size);
            for (int k = 0; k < size; k++) {
                values.add("v" + k);
            }
            attributes.add(new Attribute("c" + j, values));
        }

        ArrayList<String> classNames = new ArrayList<String>(dataset.numClasses());

        for (int k = 0; k < dataset.numClasses(); k++) {
            classNames.add("t" + k);
        }

        attributes.add(new Attribute("class", classNames));

        Instances instances = new Instances("name", attributes, dataset.numObjects());

        instances.setClassIndex(dataset.numAttr());

        for (int i = 0; i < dataset.numObjects(); i++) {
            Instance instance = new DenseInstance(dataset.numAttr() + 1);
            instance.setDataset(instances);

            int aid = 0;
            for (int j = 0; j < dataset.numRatAttr(); j++) {
                instance.setValue(aid++, dataset.ratValue(i, j));
            }

            for (int j = 0; j < dataset.numCatAttr(); j++) {
                instance.setValue(aid++, dataset.catValue(i, j));
            }

            instance.setClassValue(dataset.classValue(i));
            instances.add(instance);
        }

        return (instances);
    }
}
