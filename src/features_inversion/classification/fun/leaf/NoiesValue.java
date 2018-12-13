package features_inversion.classification.fun.leaf;

import java.util.Random;

import features_inversion.classification.fun.AttributeFunction;

public class NoiesValue implements AttributeFunction {
    public final Random random;

    public NoiesValue(Random random) {
        this.random = random;
    }

    @Override
    public double evaluate(double[] attributes, boolean clazz) {
        return random.nextGaussian();
    }

}
