package experiments;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import clusterization.CMFExtractor;
import clusterization.Dataset;
import clusterization.MetaFeaturesExtractor;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.RemoveByName;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class PrepareData {

    public static final int A_LIMIT = 128;
    public static final int I_LIMIT = 256;

    public static void main(String[] args) throws IOException {

        Map<String, String> target = new HashMap<>();

        try (CSVParser parser = new CSVParser(new FileReader("data.csv"), CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : parser) {
                target.put(record.get("id") + ".arff", record.get("target"));
            }
        }

        MetaFeaturesExtractor extractor = new CMFExtractor();

        for (File file : new File("data").listFiles()) {
            try (FileReader reader = new FileReader(file)) {
                Instances instances = new Instances(reader);

                String className = target.get(file.getName());
                if (className == null) {
                    continue;
                }

                RemoveByName rbn = new RemoveByName();
                rbn.setExpression(className);
                rbn.setInputFormat(instances);
                instances = Filter.useFilter(instances, rbn);

                Filter rmv = new ReplaceMissingValues();
                rmv.setInputFormat(instances);
                instances = Filter.useFilter(instances, rmv);

                if (instances.numAttributes() > A_LIMIT || instances.numInstances() > I_LIMIT) {
                    continue;
                }

                Filter ntb = new NominalToBinary();
                ntb.setInputFormat(instances);
                instances = Filter.useFilter(instances, ntb);

                if (instances.numAttributes() > A_LIMIT || instances.numInstances() > I_LIMIT) {
                    continue;
                }

                int n = instances.numInstances();
                int m = instances.numAttributes();
                double[][] data = new double[n][m];

                for (int i = 0; i < n; i++) {
                    Instance instance = instances.get(i);
                    for (int j = 0; j < m; j++) {
                        data[i][j] = instance.value(j);
                    }
                }
                Dataset dataset = new Dataset(data, extractor);

                double[] mf = dataset.metaFeatures();
                if (mf == null) {
                    continue;
                }

                System.out.println(file.getName());
                System.out.println(Arrays.toString(mf));
                System.out.flush();

                try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("pdata" + File.separator + file.getName() + ".obj"))) {
                    objectOutputStream.writeInt(n);
                    objectOutputStream.writeInt(m);
                    objectOutputStream.writeObject(data);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
