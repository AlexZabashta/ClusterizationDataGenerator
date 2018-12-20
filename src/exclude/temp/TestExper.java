package exclude.temp;

import java.io.File;
import java.io.FileReader;

import exclude.dsgenerators.ListMetaFeatures;
import exclude.features_inversion.classification.dataset.BinDataset;
import weka.core.Instances;

public class TestExper {

    public static void main(String[] args) {

        System.out.println(ListMetaFeatures.size());

        for (File file : new File("data\\bin_undin\\").listFiles()) {
            try (FileReader reader = new FileReader(file)) {
                Instances instances = new Instances(reader);
                instances.setClassIndex(instances.numAttributes() - 1);
                BinDataset dataset = BinDataset.fromInstances(instances);

                System.out.println(file + " " + dataset.getMetaFeature(79) + " " + dataset.getMetaFeature(80));
                System.out.flush();

            } catch (Exception e) {
                System.err.println(e.getLocalizedMessage());
            }
        }
    }

}
