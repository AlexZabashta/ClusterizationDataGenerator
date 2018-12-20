package exclude.dsgenerators.direct;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.uma.jmetal.operator.CrossoverOperator;

import exclude.features_inversion.classification.dataset.BinDataset;
import exclude.features_inversion.classification.dataset.RelationsGenerator;
import exclude.features_inversion.util.BooleanArray;

public class Crossover implements CrossoverOperator<BinDataSetSolution> {
    static int randomInt(Random random, int x, int y) {
        return Math.min(x, y) + random.nextInt(Math.abs(x - y) + 1);
    }

    static int error(int px, int nx, int py, int ny) {
        return Math.abs(px - py) + Math.abs(nx - ny);
    }

    static boolean inversion(int px, int nx, int py, int ny) {
        return error(px, nx, ny, py) < error(px, nx, py, ny);
    }

    @Override
    public List<BinDataSetSolution> execute(List<BinDataSetSolution> source) {
        if (source.size() != 2) {
            throw new IllegalArgumentException("Source should have two datasets.");
        }

        Random random = new Random();

        BinDataset objX = source.get(0).getDataset(), objY = source.get(1).getDataset();

        double[][] posX = objX.pos, negX = objX.neg, posY, negY;

        if (random.nextBoolean()) {
            negY = objY.neg;
            posY = objY.pos;
        } else {
            negY = objY.pos;
            posY = objY.neg;
        }

        if (inversion(posX.length, negX.length, posY.length, negY.length)) {
            double[][] tmp = posY;
            posY = negY;
            negY = tmp;
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

        BinDataSetSolution offspringA = new BinDataSetSolution(new BinDataset(posA, negA, attrA));
        BinDataSetSolution offspringB = new BinDataSetSolution(new BinDataset(posB, negB, attrB));

        return Arrays.asList(offspringA, offspringB);
    }

}
