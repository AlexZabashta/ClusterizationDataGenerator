package features_inversion.util;

import optimization.Measurable;

public class FeaturePoint<T> extends Point implements Measurable, Comparable<FeaturePoint<T>> {

    public final Point target, scale;

    private final double fitnessFunction;
    public final T object;

    public FeaturePoint(FeaturePoint<T> copy, T object, MetaFeaturesExtractor<T> extractor) throws Exception {
        this(copy.target, copy.scale, object, extractor);
    }

    public FeaturePoint(Point target, Point scale, T object, MetaFeaturesExtractor<T> extractor) throws Exception {
        super(extractor.extract(object));
        this.target = target;
        this.scale = scale;
        this.object = object;

        double sumOfSquares = 0;

        int d = dimension();

        if (scale.dimension() != d || target.dimension() != d) {
            throw new IllegalArgumentException("Point has different dimension");
        }

        for (int i = 0; i < d; i++) {
            double diff = dist(i);
            sumOfSquares += diff * diff;
        }
        this.fitnessFunction = -Math.sqrt(sumOfSquares);
    }

    public double dist(int index) {
        return Math.abs((super.coordinate(index) - target.coordinate(index)) / scale.coordinate(index));
    }

    @Override
    public double fitnessFunction() {
        return fitnessFunction;
    }

    @Override
    public int compareTo(FeaturePoint<T> fp) {
        return Double.compare(fitnessFunction(), fp.fitnessFunction());
    }

}
