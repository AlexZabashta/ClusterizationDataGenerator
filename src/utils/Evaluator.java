package utils;

public abstract class Evaluator<T> {
    public abstract double evaluate(T object);

    public Evaluated<T> evaluated(T object) {
        return new Evaluated<T>(object, evaluate(object));
    }

}
