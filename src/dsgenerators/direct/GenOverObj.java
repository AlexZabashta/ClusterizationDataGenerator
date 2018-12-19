package dsgenerators.direct;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;

import clusterization.direct.GDSProblem;
import dsgenerators.DatasetGenerator;
import dsgenerators.ErrorFunction;
import features_inversion.classification.dataset.BinDataset;

public class GenOverObj implements DatasetGenerator {

    final List<BinDataset> initPopulation;

    public GenOverObj(List<BinDataset> datasets) {
        this.initPopulation = new ArrayList<>(datasets);
    }

    @Override
    public List<BinDataset> generate(int a, int p, int n, ErrorFunction error, int numDatasets, int limit) {

        // ErrorFunction errorFunction = new SimpleDist(target, weight, mfIndices);

        GDSProblem problem = null;// new GDSProblem(error, initPopulation);

        CrossoverOperator<BinDataSetSolution> crossoverOperator = new Crossover();
        MutationOperator<BinDataSetSolution> mutationOperator = new Mutation();

        NSGAIIBuilder<BinDataSetSolution> nsgaiiBuilder = new NSGAIIBuilder<BinDataSetSolution>(problem, crossoverOperator, mutationOperator);
        nsgaiiBuilder.setPopulationSize(numDatasets);
        nsgaiiBuilder.setMaxIterations(limit / numDatasets);

        NSGAII<BinDataSetSolution> ga = nsgaiiBuilder.build();

        List<BinDataSetSolution> initSolutions = new ArrayList<>();

        // for (int i = 0; i < numDatasets; i++) {
        // initSolutions.add(problem.createSolution());
        // }

        for (BinDataset dataset : initPopulation) {
            // initSolutions.add(new BinDataSetSolution(dataset));
        }

        // ga.setPopulation(initSolutions);
        ga.run();
        List<BinDataSetSolution> result = ga.getResult();

        List<BinDataset> finalPopulation = new ArrayList<>();
        for (BinDataSetSolution solution : result) {
            finalPopulation.add(solution.getDataset());
        }

        return finalPopulation;
    }

}
