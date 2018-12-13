package features_inversion.classification.fun;

import java.util.Random;

import features_inversion.classification.fun.leaf.AttributeValue;
import features_inversion.classification.fun.leaf.ClassValue;
import features_inversion.classification.fun.leaf.ConstValue;
import features_inversion.classification.fun.leaf.NoiesValue;
import features_inversion.classification.fun.node.Abs;
import features_inversion.classification.fun.node.Mul;
import features_inversion.classification.fun.node.Sin;
import features_inversion.classification.fun.node.Sum;

public class RandomFunction {
    public static AttributeFunction generate1(Random random, int attr, int level) {
        double p = random.nextDouble();

        if (level <= 0) {
            p *= 0.3999;
        }

        if (p < 0.1 && attr > 0) {
            return new AttributeValue(random.nextInt(attr));
        }

        if (p < 0.2) {
            return new ClassValue();
        }

        if (p < 0.3) {
            return new ConstValue(random.nextGaussian());
        }

        if (p < 0.4) {
            return new NoiesValue(random);
        }

        if (p < 0.5) {
            return new Sin(generate(random, attr, level - 1));
        }

        if (p < 0.6) {
            return new Abs(generate(random, attr, level - 1));
        }

        if (p < 0.8) {
            return new Sum(generate(random, attr, level - 1), generate(random, attr, level - 1));
        } else {
            return new Mul(generate(random, attr, level - 1), generate(random, attr, level - 1));
        }

    }

    public static AttributeFunction generate(Random random, int attr, int level) {
        double p = random.nextDouble();

        if (level <= 0) {
            if (p < 0.5) {
                if (p < 0.25 && attr > 0) {
                    return new AttributeValue(random.nextInt(attr));
                } else {
                    return new ClassValue();
                }
            } else {
                if (p < 0.75) {
                    return new ConstValue(random.nextGaussian());
                } else {
                    return new NoiesValue(random);
                }
            }
        } else {

            if (p < 0.5) {
                if (p < 0.25) {
                    return new Sin(generate(random, attr, level - 1));
                } else {
                    return new Abs(generate(random, attr, level - 1));
                }
            } else {
                if (p < 0.75) {
                    return new Sum(generate(random, attr, level - 1), generate(random, attr, level - 1));
                } else {
                    return new Mul(generate(random, attr, level - 1), generate(random, attr, level - 1));
                }
            }
        }
    }
}
