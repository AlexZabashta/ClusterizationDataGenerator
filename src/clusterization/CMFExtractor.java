package clusterization;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import mfextraction.CacheMF;
import mfextraction.MetaFeatureExtractor;
import mfextraction.distances.KurtosisD;
import mfextraction.distances.MDDistances615;
import mfextraction.distances.MDZscore;
import mfextraction.distances.MeanD;
import mfextraction.distances.SkewnessD;
import mfextraction.distances.StddevD;
import mfextraction.distances.VarianceD;
import mfextraction.landmark.KMCH;
import mfextraction.landmark.KMCOP;
import mfextraction.landmark.KMCS;
import mfextraction.landmark.KMDunn;
import mfextraction.landmark.KMOS;
import mfextraction.landmark.KMSF;
import mfextraction.landmark.KMSV;
import mfextraction.landmark.KMSil;
import mfextraction.statistical.MeanKurtosis;
import mfextraction.statistical.MeanLinearCorrelationCoefficient;
import mfextraction.statistical.MeanSkewness;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class CMFExtractor implements MetaFeaturesExtractor {

    final List<MetaFeatureExtractor> extractors = new ArrayList<>();

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

                Filter rmv = new ReplaceMissingValues();
                rmv.setInputFormat(instances);
                instances = Filter.useFilter(instances, rmv);

                int n = instances.numInstances();

                int ci = -1;

                String className = target.get(file.getName());

                if (className != null && !className.isEmpty()) {
                    for (int j = 0; j < instances.numAttributes(); j++) {
                        Attribute attribute = instances.attribute(j);
                        if (attribute.name().equals(className)) {
                            ci = j;
                        }
                    }
                }

                int m = instances.numAttributes();
                if (ci != -1) {
                    --m;
                }
                double[][] data = new double[n][m];

                for (int i = 0; i < n; i++) {
                    int a = 0;
                    Instance instance = instances.get(i);
                    for (int j = 0; j < m; j++) {
                        while (a == ci) {
                            ++a;
                        }
                        data[i][j] = instance.value(a++);
                    }
                }

                Dataset dataset = new Dataset(data, extractor);

                System.out.println(file.getName());
                System.out.println(Arrays.toString(dataset.metaFeatures()));
                System.out.flush();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public CMFExtractor() {

        extractors.add(new MeanSkewness());
        extractors.add(new MeanKurtosis());
        extractors.add(new MeanLinearCorrelationCoefficient());

        extractors.add(new MeanD());
        extractors.add(new VarianceD());
        extractors.add(new StddevD());
        extractors.add(new SkewnessD());
        extractors.add(new KurtosisD());

        extractors.add(new MDDistances615());
        extractors.add(new MDZscore());

        extractors.add(new KMCH());
        extractors.add(new KMCOP());
        extractors.add(new KMCS());
        extractors.add(new KMDunn());
        extractors.add(new KMOS());
        extractors.add(new KMSF());
        extractors.add(new KMSil());
        extractors.add(new KMSV());
    }

    @Override
    public int lenght() {
        return extractors.size();
    }

    @Override
    public double[] extract(Dataset dataset) {
        try {
            int n = lenght();
            double[] values = new double[n];
            CacheMF cache = new CacheMF(dataset);

            for (int i = 0; i < n; i++) {
                MetaFeatureExtractor extractor = extractors.get(i);
                values[i] = extractor.extract(cache);
            }

            return values;

        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public String name(int index) {
        return MetaFeaturesExtractor.super.name(index);
    }

}
