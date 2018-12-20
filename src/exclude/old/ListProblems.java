package exclude.old;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

public class ListProblems {

    public static void main(String[] args) {
        DoubleProblem problem = new DoubleProblem() {

            @Override
            public int getNumberOfVariables() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public int getNumberOfObjectives() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public int getNumberOfConstraints() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public String getName() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void evaluate(DoubleSolution solution) {
                // TODO Auto-generated method stub

            }

            @Override
            public DoubleSolution createSolution() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Double getUpperBound(int index) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Double getLowerBound(int index) {
                // TODO Auto-generated method stub
                return null;
            }
        };

    }

}
