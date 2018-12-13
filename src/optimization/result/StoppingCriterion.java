package optimization.result;

import optimization.Measurable;

public interface StoppingCriterion<T extends Measurable> {
	public boolean test(Result<T> result);
}
