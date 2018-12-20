package exclude.features_inversion.classification.fun.node;

import exclude.features_inversion.classification.fun.AttributeFunction;

public class Sin implements AttributeFunction {
    public final AttributeFunction node;

    public Sin(AttributeFunction node) {
        this.node = node;
    }

    @Override
    public double evaluate(double[] attributes, boolean clazz) {
        return Math.sin(node.evaluate(attributes, clazz));
    }

}
