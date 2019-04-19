package experiments;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import utils.FolderUtils;
import weka.core.Instance;
import weka.core.Instances;

public class ConvertToCSV {

    public static void main(String[] args) throws IOException {
        String src = "result\\experiments.GenDip\\1555507592761";

        String dst = FolderUtils.buildPath(false, Long.toString(System.currentTimeMillis()));
        for (int f = 2; f <= 10; f += 2) {

            for (int a = 0; a < 300; a++) {
                try (BufferedReader reader = new BufferedReader(new FileReader(src + "\\" + f + "_" + a + ".arff"))) {
                    double dip = Double.parseDouble(reader.readLine().substring(2));
                    reader.readLine();
                    Instances instances = new Instances(reader);

                    int n = instances.numInstances();
                    int m = instances.numAttributes();

                    int name = (int) Math.round(dip * 1000000);
                    if (name >= 1000000) {
                        name = 999999;
                        System.out.println(f + "_" + a + " " + dip);
                    }

                    try (PrintWriter writer = new PrintWriter(String.format("%s%06d.csv", dst, name))) {
                        for (int j = 0; j < m; j++) {
                            if (j != 0) {
                                writer.print(',');
                            }
                            writer.print("f" + j);
                        }

                        for (int i = 0; i < n; i++) {
                            writer.println();
                            Instance instance = instances.get(i);
                            for (int j = 0; j < m; j++) {
                                if (j != 0) {
                                    writer.print(',');
                                }
                                writer.print(instance.value(j));
                            }
                        }

                    }

                } catch (FileNotFoundException e) {
                    System.err.println(e.getMessage());
                }
            }

        }

    }

}
