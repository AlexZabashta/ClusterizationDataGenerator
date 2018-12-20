package exclude;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class ScanArff {

    public static void main(String[] args) throws IOException {
        File dataFolder = new File("data" + File.separator + "carff");

        // System.out.println("a, i, b");

        for (File file : dataFolder.listFiles()) {

            try (FileReader reader = new FileReader(file)) {
                Instances instances = new Instances(reader);
                int classIndex = instances.numAttributes() - 1;
                instances.setClassIndex(classIndex);

                int p = 0, n = 0;

                for (Instance instance : instances) {
                    if (instance.classValue() < 0.5) {
                        ++n;
                    } else {
                        ++p;
                    }
                }

                int b = (1000 * Math.max(n, p)) / (p + n);
                System.out.printf(Locale.ENGLISH, "%4d, %5d, %3d%n", instances.numAttributes(), instances.numInstances(), b);

            }

        }

    }

}
