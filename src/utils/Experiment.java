package utils;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;

public class Experiment {
    public Problem<?> problem;
    public Limited function;
    public Algorithm<?> algorithm;
    public String name;

    public Experiment(Problem<?> problem, Limited function) {
        this.problem = problem;
        this.function = function;
    }

    public Experiment(Problem<?> problem, Limited function, String name) {
        this.problem = problem;
        this.function = function;
        this.name = name;
    }
}
