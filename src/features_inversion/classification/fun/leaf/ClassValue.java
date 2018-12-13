package features_inversion.classification.fun.leaf;

import features_inversion.classification.fun.AttributeFunction;

public class ClassValue implements AttributeFunction {

    @Override
    public double evaluate(double[] attributes, boolean clazz) {
        return clazz ? (-1) : (+1);
    }
}
