package exclude.features_inversion.classification.fun.leaf;

import exclude.features_inversion.classification.fun.AttributeFunction;

public class AttributeValue implements AttributeFunction {
    public final int index;

    public AttributeValue(int index) {
        this.index = index;
    }

    @Override
    public double evaluate(double[] attributes, boolean clazz) {
        return attributes[index];
    }

}
