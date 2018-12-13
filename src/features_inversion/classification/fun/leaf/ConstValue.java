package features_inversion.classification.fun.leaf;

import features_inversion.classification.fun.AttributeFunction;

public class ConstValue implements AttributeFunction {

    public final double value;

    public ConstValue(double value) {
        this.value = value;
    }

    @Override
    public double evaluate(double[] attributes, boolean clazz) {
        return value;
    }
}
