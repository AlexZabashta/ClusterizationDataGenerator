package features_inversion.classification.dataset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import features_inversion.util.BooleanArray;
import features_inversion.util.FeaturePoint;
import features_inversion.util.MetaFeaturesExtractor;
import optimization.Crossover;

public class BinDataCross implements Crossover<FeaturePoint<BinDataset>> {
    public final MetaFeaturesExtractor<BinDataset> extractor;

    public BinDataCross(MetaFeaturesExtractor<BinDataset> extractor) {
        this.extractor = extractor;
    }

    @Override
    public List<FeaturePoint<BinDataset>> cross(FeaturePoint<BinDataset> sourceX, FeaturePoint<BinDataset> sourceY, Random random) {

        BinDataset objX = sourceX.object, objY = sourceY.object;

        double[][] posX = objX.pos, negX = objX.neg, posY, negY;

        if (random.nextBoolean()) {
            negY = objY.neg;
            posY = objY.pos;
        } else {
            negY = objY.pos;
            posY = objY.neg;
        }

        int attr = objX.numAttr + objY.numAttr;

        int posN = randomInt(random, posX.length, posY.length);
        int negN = randomInt(random, negX.length, negY.length);

        posX = RelationsGenerator.fit(posX, posN, objX.numAttr, random);
        negX = RelationsGenerator.fit(negX, negN, objX.numAttr, random);
        posY = RelationsGenerator.fit(posY, posN, objY.numAttr, random);
        negY = RelationsGenerator.fit(negY, negN, objY.numAttr, random);

        int attrA = randomInt(random, 1, attr - 1);
        int attrB = attr - attrA;

        boolean[] f = BooleanArray.random(attr, attrA, random);

        double[][] posA = new double[posN][attrA];
        double[][] negA = new double[negN][attrA];

        double[][] posB = new double[posN][attrB];
        double[][] negB = new double[negN][attrB];

        for (int i = 0; i < posN; i++) {
            for (int a = 0, b = 0, j = 0; j < attr; j++) {
                double val;
                if (j < objX.numAttr) {
                    val = posX[i][j];
                } else {
                    val = posY[i][j - objX.numAttr];
                }

                if (f[j]) {
                    posA[i][a++] = val;
                } else {
                    posB[i][b++] = val;
                }
            }
        }

        for (int i = 0; i < negN; i++) {
            for (int a = 0, b = 0, j = 0; j < attr; j++) {
                double val;
                if (j < objX.numAttr) {
                    val = negX[i][j];
                } else {
                    val = negY[i][j - objX.numAttr];
                }

                if (f[j]) {
                    negA[i][a++] = val;
                } else {
                    negB[i][b++] = val;
                }
            }
        }
        List<FeaturePoint<BinDataset>> res = new ArrayList<FeaturePoint<BinDataset>>(2);

        try {
            res.add(new FeaturePoint<BinDataset>(sourceX, new BinDataset(posA, negA, attrA), extractor));
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
        }
        try {
            res.add(new FeaturePoint<BinDataset>(sourceX, new BinDataset(posB, negB, attrB), extractor));
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
        }

        return res;
    }

    static int randomInt(Random random, int x, int y) {
        return Math.min(x, y) + random.nextInt(Math.abs(x - y) + 1);
    }

}
