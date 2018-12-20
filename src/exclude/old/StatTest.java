package exclude.old;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.ifmo.recommendersystem.utils.StatisticalUtils;

import exclude.dsgenerators.ListMetaFeatures;
import exclude.dsgenerators.hyparam.BayesNetGen;
import exclude.dsgenerators.hyparam.GeneratorBuilder;
import exclude.features_inversion.classification.dataset.BinDataMutation;
import exclude.features_inversion.classification.dataset.BinDataset;
import exclude.features_inversion.classification.fun.AttributeFunction;
import exclude.features_inversion.classification.fun.RandomFunction;
import misc.FolderUtils;
import weka.core.Instances;

public class StatTest {

    static double kurt(double... values) {
        double mean = StatisticalUtils.mean(values);
        double variance = StatisticalUtils.variance(values, mean);
        return StatisticalUtils.centralMoment(values, 4, mean) / Math.pow(variance, 2);
    }

    static double kappa(double x, double y) {
        return (x - y) / y;
    }

    public static void main(String[] args) {

        System.out.println(kappa(0.000001, 0.000001));

    }
}
