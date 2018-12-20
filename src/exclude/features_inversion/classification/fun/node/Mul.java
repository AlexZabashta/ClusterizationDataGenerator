package exclude.features_inversion.classification.fun.node;

import exclude.features_inversion.classification.fun.AttributeFunction;

public class Mul implements AttributeFunction {
    public final AttributeFunction left, right;

    public Mul(AttributeFunction left, AttributeFunction right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public double evaluate(double[] attributes, boolean clazz) {
        return left.evaluate(attributes, clazz) * right.evaluate(attributes, clazz);
    }

}
