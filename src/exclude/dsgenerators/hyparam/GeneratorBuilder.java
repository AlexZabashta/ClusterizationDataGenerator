package exclude.dsgenerators.hyparam;

import org.uma.jmetal.solution.DoubleSolution;

//ParametrizedGenerator
public interface GeneratorBuilder {
    public Generator generate(int a, int p, int n, DoubleSolution solution);

    public Double getLowerBound(int index);

    public Double getUpperBound(int index);

    public int length();
    
    public int repeat();

}
