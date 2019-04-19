package experiments;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import net.sourceforge.jdistlib.disttest.DistributionTest;

public class TestDip {

    public static void main(String[] args) throws IOException {
        String src = "result\\experiments.ConvertToCSV\\1555599559284";

        for (File arff : new File(src).listFiles()) {
            try (CSVParser parser = new CSVParser(new FileReader(arff), CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
                List<CSVRecord> records = parser.getRecords();

                int m = parser.getHeaderMap().size();
                int n = records.size();

                double[][] data = new double[n][m];

                for (int i = 0; i < n; i++) {
                    CSVRecord record = records.get(i);
                    for (int j = 0; j < m; j++) {
                        data[i][j] = Double.parseDouble(record.get(j));
                    }
                }

                double[] dists = new double[n * (n - 1) / 2];

                for (int p = 0, i = 0; i < n; i++) {
                    for (int j = 0; j < i; j++, p++) {
                        double dist = 0;

                        for (int f = 0; f < m; f++) {
                            double diff = data[i][f] - data[j][f];
                            dist += diff * diff;
                        }

                        dists[p] = Math.sqrt(dist);
                    }
                }

                Arrays.sort(dists);
                double[] dip = DistributionTest.diptest_presorted(dists);

                System.out.println(arff.getName() + " " + dip[1]);

            }
        }

    }

}
