package optimization.result;

import optimization.Measurable;

public class ReachSolution<T extends Measurable> implements StoppingCriterion<T> {

	public final double solution, precision;

	public ReachSolution(double solution, double precision) {
		this.solution = solution;
		this.precision = precision;
	}

	@Override
	public boolean test(Result<T> result) {
		return Math.abs(solution - result.bestValue) < precision;
	}

}
