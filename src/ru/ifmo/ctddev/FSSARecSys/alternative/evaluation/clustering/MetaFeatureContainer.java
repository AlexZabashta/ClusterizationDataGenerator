package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering;

import ru.ifmo.ctddev.FSSARecSys.db.internal.Dataset;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import mfextraction.MetaFeatureExtractor;
import mfextraction.distances.*;

/**
 * Created by sergey on 04.05.16.
 */
public class MetaFeatureContainer {

    private HashMap<String, Integer> nameIndexDataset = new HashMap<>();
    private HashMap<String, Integer> nameIndexMF = new HashMap<>();
    private ArrayList<ArrayList<Double>> table = new ArrayList<>();

    public ArrayList<ArrayList<Double>> getTable() {
        return table;
    }

    public void buildMetaFeatures() throws Exception {

        List<Dataset> datasetArray = new ArrayList<>();
        List<File> fileSet = new ArrayList<>();
        HashMap<String, ArrayList<Integer>> clusterNums = new HashMap<>();
        Scanner input = new Scanner(new File("clusterDistr"));
        int count = 0;
        while (input.hasNext()){
            String nextLine = input.nextLine();
            String[] tokenize = nextLine.split(" ");

            String name = tokenize[1];
            nameIndexDataset.put(name, count);
            count++;
        }
        input.close();

        Files.walk(Paths.get("/home/sergey/anew_original_datasets/")).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                File file = new File(filePath.toString());
                fileSet.add(file);
            }
        });

        for (File fs: fileSet)
            datasetArray.add(new Dataset(fs.getName(), fs, "clusterisation"));

        System.out.println(new Date(System.currentTimeMillis()));

        int k = 0;
        for (Dataset d : datasetArray) {
            System.out.println(++k + " " + d.getName());
            ArrayList<MetaFeatureExtractor> mf = new ArrayList<>();
            mf.add(new MeanD());
            mf.add(new VarianceD());
            mf.add(new StddevD());
            mf.add(new SkewnessD());
            mf.add(new KurtosisD());

            for (int i = 0; i < 10; i++) {
                MDDistances615 md = new MDDistances615();
                md.SetBounds(i * 0.1, (i + 1) * 0.1);
                mf.add(md);
            }
            for (int i = 0; i < 4; i++) {
                MDZscore mdz = new MDZscore();
                double val = i;
                if (i != 3)
                    mdz.setBounds(val, val + 1.0);
                else
                    mdz.setBounds(val, Double.POSITIVE_INFINITY);
                mf.add(mdz);
            }

            ArrayList<Double> mfValues = new ArrayList<>();
            for (int i = 0; i < mf.size(); i++) {
                mfValues.add(mf.get(i).extract(d));
            }
            table.add(mfValues);
        }
    }
}
