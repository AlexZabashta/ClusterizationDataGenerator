package optimization.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import optimization.Measurable;
import distribution.Noramalization;

public class RouletteWheel<T extends Measurable> {

    public final Noramalization norm;

    public RouletteWheel(Noramalization norm) {
        this.norm = norm;
    }

    public List<T> select(List<T> list, int size, Random random) {
        int n = list.size();

        if (size < 0) {
            throw new RuntimeException("Can't select " + size + " negative number of objects");
        }

        if (size > n) {
            throw new RuntimeException("Can't select " + size + " objects more than list size = " + n);
        }

        List<T> result = new ArrayList<T>(size);

        if (size == n) {
            result.addAll(list);
            return result;
        }

        double[] distribution = new double[n];

        for (int i = 0; i < n; i++) {
            distribution[i] = list.get(i).fitnessFunction();
        }

        norm.normalize(distribution, 0.001);

        double sum = 1;

        for (int i = 0; i < size; i++) {
            double val = random.nextDouble() * sum;

            for (int j = 0; j < n; j++) {
                if (Double.isNaN(distribution[j])) {
                    continue;
                }
                val -= distribution[j];
                if (val < 1e-9) {
                    result.add(list.get(j));
                    sum -= distribution[j];
                    distribution[j] = Double.NaN;
                    break;
                }
            }

        }

        return result;
    }
}
