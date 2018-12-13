package features_inversion.classification.fun.node;

import features_inversion.classification.fun.AttributeFunction;

public class Sum implements AttributeFunction {
    public final AttributeFunction left, right;

    public Sum(AttributeFunction left, AttributeFunction right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public double evaluate(double[] attributes, boolean clazz) {
        return left.evaluate(attributes, clazz) + right.evaluate(attributes, clazz);
    }

}
