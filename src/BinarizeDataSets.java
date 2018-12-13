import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class BinarizeDataSets {
    public static void main(String[] args) throws IOException {
        for (File file : new File("data\\carff").listFiles()) {
            Instances src;
            try (FileReader reader = new FileReader(file)) {
                src = new Instances(reader);
            }

            int a = src.numAttributes() - 1;

            src.setClassIndex(a);

            int n = src.numClasses();
            int[] cnt = new int[n];
            for (Instance instance : src) {
                int c = (int) Math.round(instance.classValue());
                ++cnt[c];
            }

            int m = 1 << (n - 1);

            int diff = Integer.MAX_VALUE;
            int dist = 0;

            String best = "";

            for (int mask = 1; mask < m; mask++) {
                int x = 0, y = 0;

                for (int i = 0; i < n; i++) {
                    if ((mask & (1 << i)) > 0) {
                        x += cnt[i];
                    } else {
                        y += cnt[i];
                    }
                }

                int d = Math.abs(x - y);
                if (d < diff) {
                    dist = mask;
                    diff = d;
                    best = "[" + x + ", " + y + "]";
                }
            }

            ArrayList<Attribute> attributes = new ArrayList<>();
            for (int i = 0; i < a; i++) {
                attributes.add(src.attribute(i));
            }
            attributes.add(new Attribute("class", Arrays.asList("p", "n")));

            Instances dst = new Instances(file.getName(), attributes, src.size());
            dst.setClassIndex(a);
            
            for (Instance f : src) {
                int c = (int) Math.round(f.classValue());

                Instance t = new DenseInstance(a + 1);
                t.setDataset(dst);

                for (int i = 0; i < a; i++) {
                    t.setValue(i, f.value(i));
                }

                if ((dist & (1 << c)) > 0) {
                    t.setClassValue("p");
                } else {
                    t.setClassValue("n");
                }

                dst.add(t);
            }

            try (PrintWriter writer = new PrintWriter("data\\bin_arff\\" + file.getName())) {
                writer.print(dst.toString());
            }

            System.out.println(file.getName() + " " + Arrays.toString(cnt) + " -> " + best);
        }
    }
}
