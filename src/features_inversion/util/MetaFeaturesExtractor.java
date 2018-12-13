package features_inversion.util;

public interface MetaFeaturesExtractor<T> {

    int numberOfFeatures();

    double[] extract(T object) throws Exception;

}
