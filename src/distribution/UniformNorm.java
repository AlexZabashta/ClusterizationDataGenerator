package distribution;

import java.util.Arrays;

public class UniformNorm extends Noramalization {

	public static final double EPS = 1e-9;

	public void normalize(double[] distribution, double minScale) {

		int len = distribution.length;
		if (len == 0) {
			return;
		}

		double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;

		for (double val : distribution) {
			if (Double.isNaN(val)) {
				continue;
			}
			max = Math.max(max, val);
			min = Math.min(min, val);
		}

		// [-inf,nan,+inf] => [-inf, +inf]
		for (int i = 0; i < len; i++) {
			if (Double.isNaN(distribution[i])) {
				min = distribution[i] = Double.NEGATIVE_INFINITY;
			}
		}
		// [-inf,-inf] [+inf, +inf] [x, x] => [1/len]
		if (min == max || (Math.abs(max - min) < EPS)) {
			Arrays.fill(distribution, 1.0 / len);
			return;
		}

		if (Double.isInfinite(max)) {
			// [-inf, +inf] => [0, 1]
			for (int i = 0; i < len; i++) {
				if (distribution[i] == max) {
					distribution[i] = 1;
				} else {
					distribution[i] = 0;
				}
			}
			max = 1;
			min = 0;
		}

		// [-inf, max] => [min, max]
		if (Double.isInfinite(min)) {
			min = Double.POSITIVE_INFINITY;
			for (double val : distribution) {
				if (Double.isInfinite(val)) {
					continue;
				}
				min = Math.min(min, val);
			}
		}

		if (min < 0) {
			// [-x, y] => [0, x+y]
			for (int i = 0; i < len; i++) {
				distribution[i] -= min;
			}
			max -= min;
			min = 0;
		}

		min = Math.max(min, max * minScale);

		double sum = 0;
		for (int i = 0; i < len; i++) {
			if (Double.isInfinite(distribution[i])) {
				distribution[i] = min / 2;
			} else {
				distribution[i] = Math.max(distribution[i], min);
			}

			sum += distribution[i];
		}

		for (int i = 0; i < len; i++) {
			distribution[i] /= sum;
		}

	}
}
