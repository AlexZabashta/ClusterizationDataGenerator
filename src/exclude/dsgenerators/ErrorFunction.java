package exclude.dsgenerators;

import java.util.function.ToDoubleFunction;

public interface ErrorFunction extends ToDoubleFunction<double[]> {

    public double error(double[] vector) throws EndSearch;

    public int length();

}
