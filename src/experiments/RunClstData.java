package experiments;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Set;
import java.util.TreeSet;

import clusterization.CMFExtractor;
import clusterization.Dataset;
import clusterization.MetaFeaturesExtractor;
import utils.ArrayUtils;
import utils.MahalanobisDistance;
import utils.MatrixUtils;
import utils.StatUtils;

public class RunClstData {

    public static void main(String[] args) throws IOException {

        String folder = "result\\experiments.RunExp\\1545332320227";

        Set<String> datas = new TreeSet<>();

        for (File file : new File(folder).listFiles()) {
            String[] name = file.getName().split("_");
            if (name.length == 3) {
                double value;
                String opt = name[0];
                String prob = name[1];
                String data = name[2];

                datas.add(data);
            }
        }

        MetaFeaturesExtractor extractor = new CMFExtractor();

        int numMF = extractor.lenght();
        int numData = 0;

        double[][] metaData = new double[512][];

        int[] ids = new int[512];
        int ip = 0;

        for (File file : new File("pdata").listFiles()) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
                int n = objectInputStream.readInt();
                int m = objectInputStream.readInt();
                double[][] data = (double[][]) objectInputStream.readObject();

                Dataset dataset = new Dataset(data, extractor);
                double[] mf = dataset.metaFeatures();

                if (mf != null && mf.length == numMF) {
                    metaData[numData] = mf;

                    if (datas.contains(file.getName())) {
                        ids[ip++] = numData;
                    }

                    ++numData;
                }

                System.out.println(file.getName() + " " + n + " " + m);
                System.out.flush();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        double[][] cov = StatUtils.covarianceMatrix(numData, numMF, metaData);
        ArrayUtils.print(cov);
        double[][] invCov = MatrixUtils.inv(numMF, cov);
        ArrayUtils.print(invCov);

        MahalanobisDistance distance = new MahalanobisDistance(numMF, invCov);

        System.out.println(ip);

        double avg = 0;

        for (int i = 0; i < ip; i++) {
            int id = ids[i];

            double cls = Double.POSITIVE_INFINITY;

        // for (int j = 0; j < ip; j++) {
          //     int d = ids[j];

                 for (int d = 0; d < numData; d++) {
                if (d != id) {
                    cls = Math.min(cls, distance.distance(metaData[id], metaData[d]));
                }
            }

            avg += cls;

        }
        System.out.println(avg / ip);

    }
}
