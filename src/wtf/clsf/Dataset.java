package wtf.clsf;

import java.util.ArrayList;
import java.util.Arrays;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.Standardize;

public class Dataset {
    protected final int[][] catValues;
    protected final double[][] ratValues;
    protected final int[] categorySizes;
    protected final int numCatAttr, numRatAttr, numClasses, numObjects;

    protected Dataset(int numObjects, int numRatAttr, int numCatAttr, int numClasses) {
        if (numObjects < 1) {
            throw new IllegalArgumentException(String.format("numObjects=%d must be positive", numObjects));
        }
        this.numObjects = numObjects;

        if (numRatAttr < 0) {
            throw new IllegalArgumentException(String.format("numRatAttr=%d must be non negative", numRatAttr));
        }
        this.numRatAttr = numRatAttr;

        if (numCatAttr < 0) {
            throw new IllegalArgumentException(String.format("numCatAttr=%d must be non negative", numCatAttr));
        }
        this.numCatAttr = numCatAttr;

        int numAttr = numRatAttr + numCatAttr;
        if (numAttr == 0) {
            throw new IllegalArgumentException(String.format("numAttr=%d must be positive", numAttr));
        }

        this.numClasses = numClasses;
        if (numClasses < 2) {
            throw new IllegalArgumentException(String.format("numClasses=%d must be greater than one", numClasses));
        }

        this.catValues = new int[numObjects][numCatAttr + 1];
        this.ratValues = new double[numObjects][numRatAttr];

        this.categorySizes = new int[numCatAttr];
    }

    public int numAttr() {
        return numCatAttr() + numRatAttr();
    }

    protected void checkObjectIndex(int objectIndex) {
        if (objectIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("objectIndex=%d must be non negative", objectIndex));
        }
        if (objectIndex >= numObjects) {
            throw new IndexOutOfBoundsException(String.format("objectIndex=%d must be less than numObjects=%d", objectIndex, numObjects));
        }
    }

    protected void checkRatAttrIndex(int ratAttrIndex) {
        if (ratAttrIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("ratAttrIndex=%d must be non negative", ratAttrIndex));
        }
        if (ratAttrIndex >= numCatAttr) {
            throw new IndexOutOfBoundsException(String.format("ratAttrIndex=%d must be less than numRatAttr=%d", ratAttrIndex, numRatAttr));
        }
    }

    protected void checkCatAttrIndex(int catAttrIndex) {
        if (catAttrIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("catAttrIndex=%d must be non negative", catAttrIndex));
        }
        if (catAttrIndex >= numCatAttr) {
            throw new IndexOutOfBoundsException(String.format("catAttrIndex=%d must be less than numCatAttr=%d", catAttrIndex, numCatAttr));
        }
    }

    public int numCatAttr() {
        return numCatAttr;
    }

    public int numRatAttr() {
        return numRatAttr;
    }

    public int numClasses() {
        return numClasses;
    }

    public int numObjects() {
        return numObjects;
    }

    public int categorySize(int catAttrIndex) {
        checkCatAttrIndex(catAttrIndex);
        return categorySize(catAttrIndex);
    }

    public int catValue(int objectIndex, int catAttrIndex) {
        checkObjectIndex(objectIndex);
        checkCatAttrIndex(catAttrIndex);
        return catValues[objectIndex][catAttrIndex];
    }

    public double ratValue(int objectIndex, int ratAttrIndex) {
        checkObjectIndex(objectIndex);
        checkRatAttrIndex(ratAttrIndex);
        return ratValues[objectIndex][ratAttrIndex];
    }

    public int classValue(int objectIndex) {
        checkObjectIndex(objectIndex);
        return catValues[objectIndex][numCatAttr];
    }

}
