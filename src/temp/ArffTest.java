package temp;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.linear.FieldMatrix;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ProtectedProperties;
import weka.core.SparseInstance;
import weka.core.converters.CSVLoader;
import weka.experiment.CSVResultListener;

public class ArffTest {

    public static void print() {

        ArrayList<Attribute> attributes = new ArrayList<>();

        for (int i = 0; i < 256; i++) {

            attributes.add(new Attribute("a" + i, Arrays.asList("v" + (char) i + "v")));
        }

        Instances instances = new Instances("name", attributes, 322);

        System.out.println(instances.toString());

    }

    public static Instances read() throws IOException {

        try (Reader reader = new FileReader("test.arff")) {
            return new Instances(reader);
        }

    }

    public static void main(String[] args) throws IOException {
        // print();
        // Instances instances = read();
        // System.out.println(instances.toString());

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        
        System.out.println(read().toString());

        List<String> list = null;
        Attribute attribute = new Attribute("a", read());

        System.out.println(Attribute.typeToString(attribute));

        Instances instances = new Instances("name", new ArrayList<>(Arrays.asList(attribute)), 322);

        System.out.println(instances);

        Instance instance = new DenseInstance(1);

        instance.setDataset(instances);

      
        //  instance.setValue(0, 0);

        instances.add(instance);

        System.out.println(instances);

        // CSVLoader loader = new CSVLoader();
        // loader.setFile(new File("csv\\test.csv"));
        // Instances instances = loader.getDataSet();
        // System.out.println(instances.toString());
        // for (File file : new File("csv").listFiles()) { }

    }

}
