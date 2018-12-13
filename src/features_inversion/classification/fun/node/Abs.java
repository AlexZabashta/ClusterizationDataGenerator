package features_inversion.classification.fun.node;

import features_inversion.classification.fun.AttributeFunction;

public class Abs implements AttributeFunction {
    public final AttributeFunction node;

    public Abs(AttributeFunction node) {
        this.node = node;
    }

    @Override
    public double evaluate(double[] attributes, boolean clazz) {
        return Math.abs(node.evaluate(attributes, clazz));
    }

}
