package wtf;

import java.util.function.Function;

public interface MetaFeaturesExtractor<T> extends Function<T, double[]> {
    public int length();
}
