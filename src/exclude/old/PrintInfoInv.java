package exclude.old;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import exclude.dsgenerators.ListMetaFeatures;
import exclude.features_inversion.classification.dataset.BinDataset;
import misc.FolderUtils;
import weka.core.Instances;

public class PrintInfoInv {
    static String src = "result\\dsgenerators.RunInvExp\\1496776525035";
    static final int[] mfIndices = { 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33 };
    static final int n = mfIndices.length;

    static void printAP() throws IOException {

        File folder = new File(src);

        Set<String> alg = new TreeSet<>();
        Set<String> prb = new TreeSet<>();
        Set<String> dat = new TreeSet<>();

        for (File file : folder.listFiles()) {
            String[] name = file.getName().split("_");
            if (name.length == 3) {

                alg.add(name[0]);
                prb.add(name[1]);
                dat.add(name[2]);
            }
        }

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

                if (norm == 0) {
                    System.out.printf("%15s ", "-");
                } else {
                    System.out.printf("%15.2f ", error / norm);
                }
            }

            System.out.println();
        }

    }

    static void printDE() throws IOException {

        File folder = new File(src);

        Set<String> alg = new TreeSet<>();
        Set<String> prb = new TreeSet<>();
        Set<String> dat = new TreeSet<>();

        for (File file : folder.listFiles()) {
            String[] name = file.getName().split("_");
            if (name.length == 3) {

                alg.add(name[0]);
                prb.add(name[1]);
                dat.add(name[2]);
            }
        }

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

        File folder = new File(src);

        Set<String> alg = new TreeSet<>();
        Set<String> prb = new TreeSet<>();
        Set<String> dat = new TreeSet<>();

        for (File file : folder.listFiles()) {
            String[] name = file.getName().split("_");
            if (name.length == 3) {

                alg.add(name[0]);
                prb.add(name[1]);
                dat.add(name[2]);
            }
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

    public static void main(String[] args) throws IOException {
       // printAP();
        printDE();

    }

}
