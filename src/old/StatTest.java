package old;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.ifmo.recommendersystem.utils.StatisticalUtils;

import dsgenerators.ListMetaFeatures;
import dsgenerators.hyparam.BayesNetGen;
import dsgenerators.hyparam.GeneratorBuilder;
import features_inversion.classification.dataset.BinDataMutation;
import features_inversion.classification.dataset.BinDataset;
import features_inversion.classification.fun.AttributeFunction;
import features_inversion.classification.fun.RandomFunction;
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
