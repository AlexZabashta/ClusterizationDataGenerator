package optimization.result;

import optimization.Measurable;

public class IterationLimit<T extends Measurable> implements StoppingCriterion<T> {
    public final int limit;

    public IterationLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean test(Result<T> result) {
        return limit <= result.numItereation;
    }
}
