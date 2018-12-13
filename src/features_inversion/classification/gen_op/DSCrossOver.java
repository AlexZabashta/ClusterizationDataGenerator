package features_inversion.classification.gen_op;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import optimization.Crossover;
import features_inversion.classification.dataset.RelationsGenerator;
import features_inversion.util.BooleanArray;
import features_inversion.util.FeaturePoint;
import features_inversion.util.MetaFeaturesExtractor;

public class DSCrossOver implements Crossover<FeaturePoint<double[][][]>> {

    public final MetaFeaturesExtractor<double[][][]> extractor;

    public DSCrossOver(MetaFeaturesExtractor<double[][][]> extractor) {
        this.extractor = extractor;
    }

    public static int numAttr(double[][][] dataset) {
        int numAttr = -1;

        for (double[][] clazz : dataset) {
        }

        return numAttr;
    }

    @Override
    public List<FeaturePoint<double[][][]>> cross(FeaturePoint<double[][][]> sourceX, FeaturePoint<double[][][]> sourceY, Random random) {
        List<FeaturePoint<double[][][]>> result = new ArrayList<FeaturePoint<double[][][]>>();

        double[][][] ds1 = sourceX.object, ds2 = sourceY.object;
        int a1 = ds1[0][0].length;
        int a2 = ds2[0][0].length;

        double[][] ds1f = ds1[0];
        double[][] ds1t = ds1[1];

        int tIndex = random.nextInt(2);
        double[][] ds2f = ds2[tIndex - 0];
        double[][] ds2t = ds2[1 - tIndex];

        int h1 = ds1.length;
        int h2 = ds2.length;

        int f1 = ds1f.length;
        int t1 = ds1t.length;

        int f2 = ds2f.length;
        int t2 = ds2t.length;

        int f3 = f2;
        int t3 = t2;

        int f4 = f1;
        int t4 = t1;

        double[][] ds3f = RelationsGenerator.genaddRelations(ds1f, f3, a1, random);
        double[][] ds3t = RelationsGenerator.genaddRelations(ds1t, t3, a1, random);

        double[][] ds4f = RelationsGenerator.genaddRelations(ds2f, f4, a2, random);
        double[][] ds4t = RelationsGenerator.genaddRelations(ds2t, t4, a2, random);

        boolean[] ra1 = BooleanArray.random(a1, random);
        boolean[] ra2 = BooleanArray.random(a2, random);

        boolean[] rf1 = BooleanArray.random(f1, random);
        boolean[] rt1 = BooleanArray.random(t1, random);

        boolean[] rf2 = BooleanArray.random(f2, random);
        boolean[] rt2 = BooleanArray.random(t2, random);

        return result;
    }

}
