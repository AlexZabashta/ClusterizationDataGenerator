package exclude.dsgenerators.vect;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.CovarianceMatrixAdaptationEvolutionStrategy;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import clusterization.direct.GDSProblem;
import exclude.dsgenerators.DatasetGenerator;
import exclude.dsgenerators.ErrorFunction;
import exclude.dsgenerators.hyparam.RDG1Gen;
import exclude.features_inversion.classification.dataset.BinDataset;

public class GenOverDVect implements DatasetGenerator {
    final List<BinDataset> initPopulation;

    public GenOverDVect(List<BinDataset> initPopulation) {
        this.initPopulation = initPopulation;
    }

    @Override
    public List<BinDataset> generate(int a, int p, int n, ErrorFunction error, int numDatasets, int limit) {

        // ErrorFunction errorFunction = new SimpleDist(target, weight, mfIndices);

        // SimpleProblem problem = new SimpleProblem(error, a, p, n);

        // GMMProblem problem = new GMMProblem(error, a, p, n);

        // BayesNetProblem problem = new BayesNetProblem(error, a, p, n);

        // RBFProblem problem = new RBFProblem(error, a, p, n);

        RDG1Gen problem = null;// new RDG1Problem(error, a, p, n);

        CovarianceMatrixAdaptationEvolutionStrategy.Builder bld;
        // bld = new CovarianceMatrixAdaptationEvolutionStrategy.Builder(problem);

        // bld.setMaxEvaluations(500);

        // CovarianceMatrixAdaptationEvolutionStrategy alg = bld.build();

        List<DoubleSolution> initSolutions = new ArrayList<>();

        for (BinDataset dataset : initPopulation) {
            if (dataset.numAttr >= a && dataset.pos.length >= p && dataset.neg.length >= n) {
                // initSolutions.add(problem.build(dataset));
            }
        }

        // alg.setPopulation(initSolutions);

        // alg.run();
        // DoubleSolution result = alg.getResult();

        List<BinDataset> finalPopulation = new ArrayList<>();

        // finalPopulation.add(problem.build(result));

        return finalPopulation;
    }

}
