package features_inversion.classification;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class ArffConverter {

    public static int setClass(Instances instances, int classLimit) {
        int numAttributes = instances.numAttributes();

        int classIndex = instances.classIndex();

        if (classIndex != -1) {
            return classIndex;
        }

        int[] numDistinctValues = new int[numAttributes];

        for (int i = 0; i < numAttributes; i++) {
            numDistinctValues[i] = instances.numDistinctValues(i);
        }

        if (classIndex != -1) {
            if (numDistinctValues[classIndex] <= 1 || classLimit < numDistinctValues[classIndex]) {
                classIndex = -1;
            }
        }

        if (classIndex == -1) {
            for (int i = numAttributes - 1; classIndex == -1 && i >= 0; i--) {
                if (numDistinctValues[i] <= 1 || classLimit < numDistinctValues[i]) {
                    continue;
                }

                Attribute attribute = instances.attribute(i);

                String name = attribute.name().toLowerCase();

                for (String keyWord : keyWords) {
                    if (name.contains(keyWord)) {
                        classIndex = i;
                        break;
                    }
                }
            }
        }

        if (classIndex == -1) {
            for (int i = numAttributes - 1; classIndex == -1 && i >= 0; i--) {
                if (numDistinctValues[i] <= 1 || classLimit < numDistinctValues[i]) {
                    continue;
                }
                classIndex = i;
            }
        }

        return classIndex;
    }

    public static void main(String[] args) throws Exception {

        File arff = new File("data/arff/446.arff");
        double[][][] v = convert(new Instances(new FileReader(arff)), 21);

        for (double[][] cls : v) {
            for (double[] inst : cls) {
                for (double atr : inst) {
                    System.out.printf("%7.0f ", atr);
                }
                System.out.println();
            }
            System.out.println();
        }

        System.out.println(v.length);

        Instances instances = convert(v);
        System.out.println(instances);

    }

    static final String[] keyWords = { "label", "class", "target" };

    public static Instances convert(double[][][] values) {

        int numAttributes = -1;
        int numClasses = values.length;
        int numInstances = 0;

        for (double[][] clazz : values) {
            numInstances += clazz.length;
            for (double[] instance : clazz) {
                if (numAttributes == -1) {
                    numAttributes = instance.length;
                }
                if (numAttributes != instance.length) {
                    return null;
                }
            }
        }

        if (numInstances == 0 || numAttributes == -1) {
            return null;
        }

        ArrayList<Attribute> attributes = new ArrayList<>(numAttributes + 1);

        for (int i = 0; i < numAttributes; i++) {
            attributes.add(new Attribute("value_" + i));
        }

        List<String> classesNames = new ArrayList<String>();
        for (int c = 0; c < numClasses; c++) {
            classesNames.add("class_" + c);
        }
        Attribute classAttribute = new Attribute("class", classesNames);
        attributes.add(classAttribute);

        Instances instances = new Instances("dataset", attributes, numInstances);
        instances.setClassIndex(numAttributes);

        for (int c = 0; c < numClasses; c++) {
            double[][] clazz = values[c];
            String classesName = classesNames.get(c);

            for (double[] v : clazz) {
                Instance instance = new DenseInstance(numAttributes + 1);
                instance.setDataset(instances);

                for (int i = 0; i < numAttributes; i++) {
                    instance.setValue(i, v[i]);
                }
                instance.setClassValue(classesName);
                instances.add(instance);
            }
        }

        return instances;

    }

    public static double[][][] convert(Instances instances, int classLimit) throws Exception {
        Random random = new Random();
        int numClasses = 0;
        int numInstances = instances.numInstances();
        int numAttributes = instances.numAttributes() - 1;

        if (numInstances <= 1) {
            throw new Exception("numInstances=" + numInstances + " <= 1");
        }

        if (numAttributes <= 1) {
            throw new Exception("numAttributes=" + numAttributes + " <= 1");
        }

        int classIndex = setClass(instances, classLimit);

        if (classIndex == -1) {
            throw new Exception("cant set class index");
        }

        Map<Double, Integer> intClass = new HashMap<Double, Integer>();

        for (Instance instance : instances) {
            Double cval = instance.value(classIndex);

            if (cval.isNaN() || cval.isInfinite()) {
                continue;
            }

            if (!intClass.containsKey(cval)) {
                intClass.put(cval, numClasses++);
            }

            if (numClasses > classLimit) {
                return null;
            }
        }

        if (numClasses <= 1) {
            throw new Exception("numClasses=" + numClasses + " <= 1");
        }

        double[][] attrValues = new double[numInstances][numAttributes];
        int[] color = new int[numInstances];
        int[] size = new int[numClasses];

        for (int i = 0; i < numInstances; i++) {
            Instance instance = instances.get(i);
            double cval = instance.value(classIndex);
            Integer cint = intClass.get(cval);

            if (cint == null) {
                color[i] = random.nextInt(numClasses);
            } else {
                color[i] = cint;
            }

            ++size[color[i]];

            for (int a = 0, j = 0; a <= numAttributes; a++) {
                if (a == classIndex) {
                    continue;
                }

                attrValues[i][j++] = instance.value(a);
            }
        }

        int n = 64;
        double[] list = new double[n];
        for (int i = 0; i < n; i++) {
            list[i] = random.nextGaussian() * numClasses * n;
        }

        for (int j = 0; j < numAttributes; j++) {
            int m = 0;

            for (int i = 0; i < numInstances; i++) {
                double val = attrValues[i][j];

                if (Double.isInfinite(val) || Double.isNaN(val)) {
                    continue;
                }

                if (m < n) {
                    list[m++] = val;
                } else {
                    list[random.nextInt(m)] = val;
                }
            }

            if (m == 0) {
                m = n;
            }

            for (int i = 0; i < numInstances; i++) {
                double val = attrValues[i][j];

                if (Double.isInfinite(val) || Double.isNaN(val)) {
                    attrValues[i][j] = list[random.nextInt(m)];
                }
            }
        }

        double[][][] result = new double[numClasses][][];

        for (int c = 0; c < numClasses; c++) {
            result[c] = new double[size[c]][];
            size[c] = 0;
        }

        for (int i = 0; i < numInstances; i++) {
            int c = color[i];
            result[c][size[c]++] = attrValues[i];
        }

        return result;

    }
}
