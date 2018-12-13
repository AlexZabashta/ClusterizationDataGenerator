package distribution;

import java.util.Arrays;
import java.util.Comparator;

public class ReRankViaSort extends Noramalization {
	public static final double EPS = 1e-9;

	public void normalize(final double[] distribution, double minScale) {

		int len = distribution.length;
		if (len == 0) {
			return;
		}

		for (int i = 0; i < len; i++) {
			if (Double.isNaN(distribution[i])) {
				distribution[i] = Double.NEGATIVE_INFINITY;
			}
		}

		final Integer[] order = new Integer[len];
		for (int i = 0; i < len; i++) {
			order[i] = i;
		}

		Arrays.sort(order, new Comparator<Integer>() {
			@Override
			public int compare(Integer i, Integer j) {
				return Double.compare(distribution[i], distribution[j]);
			}
		});

		double min = 1.0, max = min;
		double u = 0, v = 0;

		for (int i = 0; i < len; i++) {
			int index = order[i];
			v = distribution[index];

			if (i != 0) {
				if (u != v) {
					if (Double.isInfinite(u) || Double.isInfinite(v) || v - u > EPS) {
						max += 1.0;
					}
				}
			}
			u = v;

			distribution[index] = max;
		}

		if (min == max) {
			Arrays.fill(distribution, 1.0 / len);
			return;
		}

		// min + offset
		// ------------ = minScale
		// max + offset

		double offset = (max * minScale - min) / (1 - minScale);

		double sum = 0;
		for (int i = 0; i < len; i++) {
			distribution[i] += offset;
			sum += distribution[i];
		}

		for (int i = 0; i < len; i++) {
			distribution[i] /= sum;
		}

	}
}
