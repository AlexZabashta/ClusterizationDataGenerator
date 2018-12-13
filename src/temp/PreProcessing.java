package temp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Reorder;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class PreProcessing {

    static String[] classNames = { "class", "target", "label" };

    public static Instances process(Instances instances) throws Exception {

        int n = instances.numInstances(), m = instances.numAttributes();

        if (m > 500 || n > 2000) {
            throw new Exception("Dataset too big: m > 500 || n > 2000");
        }

        int classIndex = -1;

        for (String className : classNames) {
            for (int j = 0; classIndex == -1 && j < m; j++) {
                int index = (j - 1 + m) % m;
                if (instances.numDistinctValues(index) == 2) {
                    Attribute attribute = instances.attribute(index);
                    if (attribute.isNominal() && attribute.name().toLowerCase().contains(className)) {
                        classIndex = index;
                    }
                }
            }
        }

        // for (int j = 0; classIndex == -1 && j < m; j++) {
        // int index = (j - 1 + m) % m;
        // if (instances.numDistinctValues(index) == 2) {
        // Attribute attribute = instances.attribute(index);
        // if (attribute.isNominal()) {
        // classIndex = index;
        // }
        // }
        // }

        if (classIndex == -1) {
            throw new Exception("Can't find class");
        }

        ReplaceMissingValues replaceMissingValues = new ReplaceMissingValues();
        replaceMissingValues.setInputFormat(instances);
        instances = Filter.useFilter(instances, replaceMissingValues);

        int[] lsit = new int[m + 1];
        int p = 0;

        for (int j = 0; j < m; j++) {
            boolean notMissing = true;
            for (int i = 0; notMissing && i < n; i++) {
                Instance instance = instances.get(i);
                notMissing = instance.isMissing(j) == false;
            }

            if (j == classIndex && !notMissing) {
                throw new Exception("Class value is missing");
            }

            if (j != classIndex && notMissing) {
                lsit[p++] = j;
            }
        }

        lsit[p++] = classIndex;

        Reorder reorder = new Reorder();
        reorder.setAttributeIndicesArray(Arrays.copyOf(lsit, p));
        reorder.setInputFormat(instances);
        instances = Filter.useFilter(instances, reorder);

        return instances;
    }

    public static void main(String[] args) {
        for (File file : new File("data\\all\\").listFiles()) {
            try (FileReader reader = new FileReader(file)) {
                Instances instances = new Instances(reader);
                instances = PreProcessing.process(instances);

                try (PrintWriter writer = new PrintWriter(new File("data\\bin_all\\" + file.getName()))) {
                    writer.println(instances);
                }

            } catch (Exception e) {
                System.err.println(file.getName() + " " + e.getMessage());
            }
        }
    }
}
