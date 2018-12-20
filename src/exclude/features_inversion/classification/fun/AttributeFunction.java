package exclude.features_inversion.classification.fun;

public interface AttributeFunction {
    public abstract double evaluate(double[] attributes, boolean clazz);
}
