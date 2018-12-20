package exclude.old;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import exclude.dsgenerators.ListMetaFeatures;
import exclude.features_inversion.classification.dataset.BinDataset;
import misc.FolderUtils;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class PrintInfo {
    // static String src = "result\\dsgenerators.RunMultCritExp\\1498841833828";
    // static String src = "result\\dsgenerators.RunMultCritExp\\1499696328585";
    static String src = "result\\dsgenerators.RunMultCritExp\\all";
    static final int[] mfIndices = { 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33 };
    static final int n = mfIndices.length;

    static Set<String> alg = new TreeSet<>();
    static Set<String> prb = new TreeSet<>();
    static Set<String> dat = new TreeSet<>();
    static Map<String, Double> avg = new TreeMap<String, Double>();

    static File folder = new File(src);

    static void read() throws IOException {
        for (File file : folder.listFiles()) {
            String[] name = file.getName().split("_");

            if (name.length != 4) {
                continue;
            }

            if (name[2].contains("false")) {
                // continue;
            }

            if (!(name[2].contains("GMM") || name[2].contains("GDS") || name[2].contains("Simple"))) {
                // continue;
            }

            if (!(name[1].contains("SMSEMOA") || name[1].contains("SPEA2") || name[1].contains("NSGAII") || name[1].contains("CovarianceMatrix") || name[1].contains("MOCell"))) {
                // continue;
            }

            alg.add(name[1]);
            prb.add(name[2]);
            dat.add(name[3]);
        }

        alg.remove("PAES");
    }

    static void normalize() throws IOException {
        for (String d : dat) {
            double error = 0;
            int norm = 0;

            for (String a : alg) {

                for (String p : prb) {

                    File file = new File(folder + "\\1_" + a + "_" + p + "_" + d);

                    if (file.exists()) {
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            String line = reader.readLine();
                            double val = Double.parseDouble(line.substring(2));

                            error += val;
                            ++norm;
                        }

                    }
                }
            }

            avg.put(d, error / norm);
        }
    }

    static void retain() throws IOException {
        Set<String> tmp = new TreeSet<>();

        Set<String> algprb = new TreeSet<>();

        for (String a : alg) {
            for (String p : prb) {
                int cnt = 0;

                for (String d : dat) {
                    File file = new File(folder + "\\1_" + a + "_" + p + "_" + d);

                    if (file.exists()) {
                        ++cnt;
                    }
                }
                if (cnt > 100) {
                    algprb.add(a + "_" + p);
                }
            }
        }

        for (String d : dat) {
            boolean ok = true;
            for (String ap : algprb) {
                File file = new File(folder + "\\1_" + ap + "_" + d);
                ok &= file.exists();
            }
            if (ok) {
                tmp.add(d);
            }
        }

        dat = tmp;
    }

    static void printAP() throws IOException {

        System.out.printf("%20s", "");
        for (String p : prb) {
            System.out.printf("%12s ", p.length() > 12 ? p.substring(0, 12) : p);
        }
        System.out.println();
        for (String a : alg) {

            System.out.printf("%12s ", a.length() > 12 ? a.substring(0, 12) : a);

            for (String p : prb) {
                double error = 0;
                int norm = 0;

                for (String d : dat) {
                    File file = new File(folder + "\\1_" + a + "_" + p + "_" + d);

                    if (file.exists()) {
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            String line = reader.readLine();
                            double val = Double.parseDouble(line.substring(2));

                            Double ae = avg.get(d);

                            if (ae == null) {
                                ae = 1.0;
                            }

                            error += val / ae;
                            ++norm;
                        }
                    } else {
                        // error += 100;
                        // ++norm;
                    }
                }

                // System.out.printf("%3d ", norm);

                if (norm < 100) {
                    System.out.printf("%15s ", "-");
                } else {
                    System.out.printf("%15.2f ", error / norm);
                }
            }

            System.out.println();
        }

    }

    static void printWI() throws IOException {

        Set<String> algprob = new TreeSet<>();

        algprob.add("CovarianceMatrixAdaptationEvolutionStrategy_SimpleProblemtrue");
        algprob.add("CovarianceMatrixAdaptationEvolutionStrategy_GMMGenProblem");

        algprob.add("SPEA2_SimpleProblemtrue");
        algprob.add("MOCell_GMMGenProblem");

        algprob.add("NSGAII_GDSProblemtrue");
        algprob.add("SMSEMOA_GDSProblemtrue");
        algprob.add("SPEA2_GDSProblemtrue");

        ArrayList<String> classNames = new ArrayList<>(algprob);

        ArrayList<Attribute> attributes = new ArrayList<>(n + 1);

        for (int i = 0; i < n; i++) {
            attributes.add(new Attribute("mf" + i));
        }

        attributes.add(new Attribute("class", classNames));

        Instances instances = new Instances("mfd", attributes, 300);
        instances.setClassIndex(n);

        for (String d : dat) {

            Instance instance = new DenseInstance(n + 1);
            instance.setDataset(instances);

            try (BufferedReader reader = new BufferedReader(new FileReader(folder + "\\" + d + ".txt"))) {
                String[] line = reader.readLine().split(" ");

                for (int i = 0; i < n; i++) {
                    instance.setValue(i, Double.parseDouble(line[i]));
                }
            }

            String bestAP = null;
            double beestVal = Double.POSITIVE_INFINITY;

            for (String a : alg) {
                for (String p : prb) {

                    File file = new File(folder + "\\1_" + a + "_" + p + "_" + d);

                    if (file.exists()) {
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            String line = reader.readLine();
                            double val = Double.parseDouble(line.substring(2));
                            String ap = a + "_" + p;

                            if (val < beestVal && algprob.contains(ap)) {
                                beestVal = val;
                                bestAP = ap.intern();
                            }
                        }
                    }
                }
            }

            System.out.println(d + " " + bestAP + " " + beestVal);
            instance.setClassValue(bestAP);
            instances.add(instance);
        }

        try (PrintWriter writer = new PrintWriter("instances.arff")) {
            writer.println(instances);
        }
    }

    static void printCB() throws IOException {

        Map<String, Integer> cnt = new HashMap<>();
        cnt.put("", 0);

        for (String a : alg) {
            for (String p : prb) {
                cnt.put(a + "_" + p, 0);
            }
        }

        for (String d : dat) {
            String bestAP = "";
            double beestVal = Double.POSITIVE_INFINITY;

            for (String a : alg) {
                for (String p : prb) {

                    File file = new File(folder + "\\1_" + a + "_" + p + "_" + d);

                    if (file.exists()) {
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            String line = reader.readLine();
                            double val = Double.parseDouble(line.substring(2));

                            if (val < beestVal) {
                                beestVal = val;
                                bestAP = a + "_" + p;
                            }

                        }
                    }
                }
            }

            System.out.println(d + " " + bestAP);

            cnt.put(bestAP, cnt.get(bestAP) + 1);
        }

        System.out.printf("%20s", "");
        for (String p : prb) {
            System.out.printf("%12s ", p.length() > 12 ? p.substring(0, 12) : p);
        }
        System.out.println();
        for (String a : alg) {

            System.out.printf("%12s ", a.length() > 12 ? a.substring(0, 12) : a);

            for (String p : prb) {
                System.out.printf("%15d ", cnt.get(a + "_" + p));
            }

            System.out.println();
        }

    }

    static void printDE() throws IOException {

        System.out.println(dat.size());

        for (String d : dat) {
            System.out.print(d.replace(".arff", "") + " &");
            double error = 0;
            int norm = 0;
            for (String a : alg) {
                for (String p : prb) {
                    File file = new File(folder + "\\" + a + "_" + p + "_" + d);

                    if (file.exists()) {
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            String line = reader.readLine();
                            double val = Double.parseDouble(line.substring(2));

                            error += val;
                            ++norm;
                        }
                    }
                }
            }

            System.out.printf("%6.2f ", error / norm);
            System.out.print(" & ");
        }

    }

    public static void printMFinfo() throws IOException {

        final double[] sum0 = new double[n];
        final double[] sum1 = new double[n];
        final double[] sum2 = new double[n];

        final List<BinDataset> datasets = new ArrayList<BinDataset>();
        final List<String> fileNames = new ArrayList<String>();

        for (File file : new File("data\\bin_undin\\").listFiles()) {
            try (FileReader reader = new FileReader(file)) {
                Instances instances = new Instances(reader);
                instances.setClassIndex(instances.numAttributes() - 1);
                BinDataset dataset = BinDataset.fromInstances(instances);

                if (instances.numAttributes() > 300 || instances.numInstances() > 1000) {
                    System.err.println(file.getName() + " too big");
                    continue;
                }

                boolean nan = false;
                for (int i = 0; i < n; i++) {
                    double val = dataset.getMetaFeature(mfIndices[i]);
                    if (Double.isNaN(val) || Double.isInfinite(val)) {
                        nan = true;
                    } else {
                        sum0[i] += 1;
                        sum1[i] += val;
                        sum2[i] += val * val;
                    }
                }

                if (nan) {
                    System.err.println(file.getName() + " mf is NaN");
                } else {
                    datasets.add(dataset);
                    fileNames.add(file.getName());
                    System.err.println(file.getName() + " added");
                }

            } catch (Exception e) {
                System.err.println(file.getName() + " " + e.getMessage());
            }
        }

        double[] mean = new double[n];
        double[] weight = new double[n];

        for (int i = 0; i < n; i++) {
            double mX1 = sum1[i] / sum0[i];
            double mX2 = sum2[i] / sum0[i];

            double var = mX2 - mX1 * mX1;
            double std = Math.sqrt(var);

            mean[i] = mX1;
            weight[i] = 1 / std;
        }

        double[] error = new double[n];
        double[] norm = new double[n];

        for (String d : dat) {
            double[] target = new double[n];

            try (BufferedReader reader = new BufferedReader(new FileReader(folder + "\\" + d + ".txt"))) {
                String[] line = reader.readLine().split(" ");
                for (int i = 0; i < n; i++) {
                    target[i] = Double.parseDouble(line[i]);
                }
            }

            for (String a : alg) {
                for (String p : prb) {
                    File file = new File(folder + "\\" + a + "_" + p + "_" + d);

                    if (file.exists()) {
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            reader.readLine();
                            String[] line = reader.readLine().substring(2).split(" ");

                            for (int i = 0; i < n; i++) {
                                error[i] += Math.abs(Double.parseDouble(line[i]) - target[i]);
                                norm[i]++;
                            }
                        }
                    }
                }
            }

        }

        for (int i = 0; i < n; i++) {
            if (i % 3 == 0) {
                System.out.println();
            }
            System.out.printf("%8.3f & %8.3f & %8.3f & ", mean[i], weight[i], error[i] / norm[i] * weight[i]);
        }

    }

    static void printMF() throws IOException {

        List<String> prob = new ArrayList<>();

        prob.add("REAL");

        prob.add("GDSProblemtrue");
        prob.add("GMMGenProblem");
        prob.add("SimpleProblemtrue");

        ArrayList<Attribute> attributes = new ArrayList<>(n + 1);

        for (int i = 0; i < n; i++) {
            attributes.add(new Attribute("mf" + i));
        }

        attributes.add(new Attribute("class", prob));
        Instances instances = new Instances("mfd", attributes, 300);

        instances.setClassIndex(n);

        for (String p : prob) {
            for (String d : dat) {
                if (p.equals("REAL")) {
                    File file = new File(folder + "\\" + d + ".txt");
                    if (file.exists()) {
                        Instance instance = new DenseInstance(n + 1);
                        instance.setDataset(instances);

                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            String[] line = reader.readLine().split(" ");

                            for (int i = 0; i < n; i++) {
                                instance.setValue(i, Double.parseDouble(line[i]));
                            }
                        }
                        instance.setClassValue(p.intern());
                        instances.add(instance);
                    }
                } else {

                    String minA = null;
                    double min = Double.POSITIVE_INFINITY;

                    for (String a : alg) {
                        File file = new File(folder + "\\1_" + a + "_" + p + "_" + d);

                        if (file.exists()) {
                            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                                double val = Double.parseDouble(reader.readLine().substring(2));

                                if (val < min) {
                                    min = val;
                                    minA = reader.readLine().substring(2);
                                }

                            }
                        }
                    }

                    Instance instance = new DenseInstance(n + 1);
                    instance.setDataset(instances);
                    String[] line = minA.split(" ");
                    for (int i = 0; i < n; i++) {
                        instance.setValue(i, Double.parseDouble(line[i]));
                    }
                    instance.setClassValue(p.intern());
                    instances.add(instance);
                }
            }
            System.out.println(instances.size());
        }
        try (PrintWriter writer = new PrintWriter("mf_instances.arff")) {
            writer.println(instances);
        }
    }

    static void findBestWorst() throws IOException {
        Set<String> algprob = new TreeSet<>();
        // algprob.add("CovarianceMatrixAdaptationEvolutionStrategy_GMMGenProblem");
        algprob.add("MOCell_GDSProblemtrue");
        // algprob.add("SPEA2_SimpleProblemtrue");

        double max = Double.NEGATIVE_INFINITY, min = Double.POSITIVE_INFINITY;
        String maxArff = "", minArff = "";

        Set<String> algprb = new TreeSet<>();

        for (String a : alg) {
            for (String p : prb) {
                int cnt = 0;

                for (String d : dat) {
                    File file = new File(folder + "\\1_" + a + "_" + p + "_" + d);

                    if (file.exists()) {
                        ++cnt;
                    }
                }
                if (cnt > 100) {
                    algprb.add(a + "_" + p);
                }
            }
        }

        for (String d : dat) {

            boolean ok = true;

            {
                File file = new File(folder + "\\" + d + ".txt");
                ok &= file.exists();

                if (ok) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String[] line = reader.readLine().split(" ");
                        double inst = Double.parseDouble(line[0]);
                        double feat = Double.parseDouble(line[1]);
                        ok &= inst > 20 && feat > 5;
                    }
                }
            }

            // file = new File();

            double sum = 0;

            for (String ap : algprb) {
                if (ok) {
                    File file = new File(folder + "\\1_" + ap + "_" + d);
                    if (file.exists()) {
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            String line = reader.readLine();
                            double val = Double.parseDouble(line.substring(2));
                            sum += val;
                        }
                    } else {
                        ok = false;
                    }
                }
            }

            if (ok && sum < min) {
                min = sum;
                minArff = d;
            }

            if (ok && sum > max) {
                max = sum;
                maxArff = d;
            }

            System.out.println(d + " " + sum);

        }

        System.out.println(minArff + " " + maxArff);

    }

    public static void main(String[] args) throws IOException {
        read();
        retain();

        // normalize();
        // printAP();
        // printCB();
        // printWI();
        printMF();

        // findBestWorst();

    }

}
