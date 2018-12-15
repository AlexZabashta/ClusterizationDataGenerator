package utils;

import java.util.Objects;

public class Evaluated<T> implements Comparable<Evaluated<T>> {
    public final T object;
    public final double cost;

    public Evaluated(T object, Evaluator<T> evaluator) {
        this(object, evaluator.evaluate(object));
    }

    public Evaluated(T object, double cost) {
        if (Double.isInfinite(cost)) {
            throw new IllegalArgumentException("Cost can't be infinite.");
        }
        if (Double.isNaN(cost)) {
            throw new IllegalArgumentException("Cost can't be NaN.");
        }

        this.object = Objects.requireNonNull(object, "Object can't be null.");
        this.cost = cost;
    }

    @Override
    public int compareTo(Evaluated<T> other) {
        return Double.compare(other.cost, this.cost);
    }

}
