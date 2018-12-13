package wtf.clsf;

public class MutableDataset extends Dataset {

    public MutableDataset(int numObjects, int numRatAttr, int numCatAttr, int numClasses) {
        super(numObjects, numRatAttr, numCatAttr, numClasses);
    }

    public void setCategorySize(int catAttrIndex, int categorySize) {
        checkCatAttrIndex(catAttrIndex);
        categorySizes[catAttrIndex] = categorySize;
    }

    public void setCatValue(int objectIndex, int catAttrIndex, int catValue) {
        checkObjectIndex(objectIndex);
        checkCatAttrIndex(catAttrIndex);
        catValues[objectIndex][catAttrIndex] = catValue;
    }

    public void setRatValue(int objectIndex, int ratAttrIndex, double ratValue) {
        checkObjectIndex(objectIndex);
        checkRatAttrIndex(ratAttrIndex);
        ratValues[objectIndex][ratAttrIndex] = ratValue;
    }

    public void setClassValue(int objectIndex, int classValue) {
        checkObjectIndex(objectIndex);
        catValues[objectIndex][numCatAttr] = classValue;
    }

}
