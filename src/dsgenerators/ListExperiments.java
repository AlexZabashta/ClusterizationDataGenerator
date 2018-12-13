package dsgenerators;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.gde3.GDE3Builder;
import org.uma.jmetal.algorithm.multiobjective.ibea.IBEABuilder;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder.Variant;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.omopso.OMOPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.paes.PAESBuilder;
import org.uma.jmetal.algorithm.multiobjective.pesa2.PESA2Builder;
import org.uma.jmetal.algorithm.multiobjective.randomsearch.RandomSearchBuilder;
import org.uma.jmetal.algorithm.multiobjective.smsemoa.SMSEMOABuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.CovarianceMatrixAdaptationEvolutionStrategy;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import dsgenerators.direct.BinDataSetSolution;
import dsgenerators.direct.Crossover;
import dsgenerators.direct.GDSProblem;
import dsgenerators.direct.Mutation;
import dsgenerators.hyparam.BayesNetGen;
import dsgenerators.hyparam.GMMGen;
import dsgenerators.hyparam.PGProblem;
import dsgenerators.hyparam.RBFGen;
import dsgenerators.hyparam.RDG1Gen;
import dsgenerators.vect.SimpleProblem;
import features_inversion.classification.dataset.BinDataset;
import misc.Experiment;

public class ListExperiments {

    static List<Experiment> experiments(int a, int p, int n, ErrorFunction ef, int limit, String name, List<BinDataset> datasets) {
        List<Experiment> experiments = new ArrayList<Experiment>();
        for (Experiment experiment : problems(a, p, n, ef, limit, name, datasets)) {
            Problem<?> prob = experiment.problem;

            try {
                DoubleProblem problem = (DoubleProblem) prob;
                Algorithm<?> algorithm = new GDE3Builder(problem).setMaxIterations(10000000).setPopulationSize(32).build();
                experiment.algorithm = algorithm;

                experiments.add(experiment);
            } catch (ClassCastException ifNotDouble) {
            }
        }

        for (Experiment experiment : problems(a, p, n, ef, limit, name, datasets)) {
            Problem<?> prob = experiment.problem;

            try {
                DoubleProblem problem = (DoubleProblem) prob;
                Algorithm<?> algorithm = new IBEABuilder(problem).setMaxEvaluations(10000000).setPopulationSize(32).build();
                experiment.algorithm = algorithm;

                experiments.add(experiment);
            } catch (ClassCastException ifNotDouble) {
            }
        }

        for (Experiment experiment : problems(a, p, n, ef, limit, name, datasets)) {
            Problem<?> prob = experiment.problem;

            try {
                GDSProblem problem = (GDSProblem) prob;

                Algorithm<?> algorithm = new MOCellBuilder<BinDataSetSolution>(problem, new Crossover(), new Mutation()).setMaxEvaluations(10000000).build();
                experiment.algorithm = algorithm;
                experiments.add(experiment);

            } catch (ClassCastException ifNotGDS) {
            }

            try {
                DoubleProblem problem = (DoubleProblem) prob;
                Algorithm<?> algorithm = new MOCellBuilder<DoubleSolution>(problem, new SBXCrossover(0.9, 20.0), new PolynomialMutation()).setMaxEvaluations(10000000).build();
                experiment.algorithm = algorithm;
                experiments.add(experiment);
            } catch (ClassCastException ifNotDouble) {
            }
        }

        for (Experiment experiment : problems(a, p, n, ef, limit, name, datasets)) {
            Problem<?> prob = experiment.problem;

            try {
                DoubleProblem problem = (DoubleProblem) prob;
                Algorithm<?> algorithm = new MOEADBuilder(problem, Variant.MOEAD).setMaxEvaluations(10000000).setPopulationSize(32).build();
                experiment.algorithm = algorithm;
                experiments.add(experiment);
            } catch (ClassCastException ifNotDouble) {
            }
        }

        for (Experiment experiment : problems(a, p, n, ef, limit, name, datasets)) {
            Problem<?> prob = experiment.problem;

            try {
                GDSProblem problem = (GDSProblem) prob;

                Algorithm<?> algorithm = new NSGAIIBuilder<BinDataSetSolution>(problem, new Crossover(), new Mutation()).setMaxIterations(10000000).setPopulationSize(32).build();
                experiment.algorithm = algorithm;
                experiments.add(experiment);

            } catch (ClassCastException ifNotGDS) {
            }

            try {
                DoubleProblem problem = (DoubleProblem) prob;
                Algorithm<?> algorithm = new NSGAIIBuilder<DoubleSolution>(problem, new SBXCrossover(0.9, 20.0), new PolynomialMutation()).setMaxIterations(10000000).setPopulationSize(32).build();
                experiment.algorithm = algorithm;
                experiments.add(experiment);
            } catch (ClassCastException ifNotDouble) {
            }
        }

        for (Experiment experiment : problems(a, p, n, ef, limit, name, datasets)) {
            Problem<?> prob = experiment.problem;

            try {
                DoubleProblem problem = (DoubleProblem) prob;
                Algorithm<?> algorithm = new OMOPSOBuilder(problem, new SequentialSolutionListEvaluator<>()).setMaxIterations(10000000).build();
                experiment.algorithm = algorithm;
                experiments.add(experiment);
            } catch (ClassCastException ifNotDouble) {
            }
        }

        for (Experiment experiment : problems(a, p, n, ef, limit, name, datasets)) {
            Problem<?> prob = experiment.problem;

            try {
                GDSProblem problem = (GDSProblem) prob;

                Algorithm<?> algorithm = new PAESBuilder<BinDataSetSolution>(problem).setMutationOperator(new Mutation()).setMaxEvaluations(10000000).build();
                experiment.algorithm = algorithm;
                experiments.add(experiment);

            } catch (ClassCastException ifNotGDS) {
            }

            try {
                DoubleProblem problem = (DoubleProblem) prob;
                Algorithm<?> algorithm = new PAESBuilder<DoubleSolution>(problem).setMutationOperator(new PolynomialMutation()).setMaxEvaluations(10000000).build();
                experiment.algorithm = algorithm;
                experiments.add(experiment);
            } catch (ClassCastException ifNotDouble) {
            }
        }

        for (Experiment experiment : problems(a, p, n, ef, limit, name, datasets)) {
            Problem<?> prob = experiment.problem;

            if (prob.getNumberOfObjectives() > 1) {
                continue;
            }

            try {
                GDSProblem problem = (GDSProblem) prob;

                Algorithm<?> algorithm = new PESA2Builder<BinDataSetSolution>(problem, new Crossover(), new Mutation()).setMaxEvaluations(10000000).setPopulationSize(32).build();
                experiment.algorithm = algorithm;
                experiments.add(experiment);

            } catch (ClassCastException ifNotGDS) {
            }

            try {
                DoubleProblem problem = (DoubleProblem) prob;
                Algorithm<?> algorithm = new PESA2Builder<DoubleSolution>(problem, new SBXCrossover(0.9, 20.0), new PolynomialMutation()).setMaxEvaluations(10000000).setPopulationSize(32).build();
                experiment.algorithm = algorithm;
                experiments.add(experiment);
            } catch (ClassCastException ifNotDouble) {
            }
        }

        for (Experiment experiment : problems(a, p, n, ef, limit, name, datasets)) {
            Problem<?> prob = experiment.problem;

            try {
                GDSProblem problem = (GDSProblem) prob;

                Algorithm<?> algorithm = new RandomSearchBuilder<BinDataSetSolution>(problem).setMaxEvaluations(10000000).build();
                experiment.algorithm = algorithm;
                experiments.add(experiment);

            } catch (ClassCastException ifNotGDS) {
            }

            try {
                DoubleProblem problem = (DoubleProblem) prob;
                Algorithm<?> algorithm = new RandomSearchBuilder<DoubleSolution>(problem).setMaxEvaluations(10000000).build();
                experiment.algorithm = algorithm;
                experiments.add(experiment);
            } catch (ClassCastException ifNotDouble) {
            }
        }
        for (Experiment experiment : problems(a, p, n, ef, limit, name, datasets)) {
            Problem<?> prob = experiment.problem;

            try {
                GDSProblem problem = (GDSProblem) prob;

                Algorithm<?> algorithm = new SMSEMOABuilder<BinDataSetSolution>(problem, new Crossover(), new Mutation()).setMaxEvaluations(10000000).setPopulationSize(32).build();
                experiment.algorithm = algorithm;
                experiments.add(experiment);

            } catch (ClassCastException ifNotGDS) {
            }

            try {
                DoubleProblem problem = (DoubleProblem) prob;
                Algorithm<?> algorithm = new SMSEMOABuilder<DoubleSolution>(problem, new SBXCrossover(0.9, 20.0), new PolynomialMutation()).setMaxEvaluations(10000000).setPopulationSize(32).build();
                experiment.algorithm = algorithm;
                experiments.add(experiment);
            } catch (ClassCastException ifNotDouble) {
            }
        }

        for (Experiment experiment : problems(a, p, n, ef, limit, name, datasets)) {
            Problem<?> prob = experiment.problem;

            try {
                GDSProblem problem = (GDSProblem) prob;

                Algorithm<?> algorithm = new SPEA2Builder<BinDataSetSolution>(problem, new Crossover(), new Mutation()).setMaxIterations(10000000).setPopulationSize(32).build();
                experiment.algorithm = algorithm;
                experiments.add(experiment);

            } catch (ClassCastException ifNotGDS) {
            }

            try {
                DoubleProblem problem = (DoubleProblem) prob;
                Algorithm<?> algorithm = new SPEA2Builder<DoubleSolution>(problem, new SBXCrossover(0.9, 20.0), new PolynomialMutation()).setMaxIterations(10000000).setPopulationSize(32).build();
                experiment.algorithm = algorithm;
                experiments.add(experiment);
            } catch (ClassCastException ifNotDouble) {
            }
        }

        for (Experiment experiment : problems(a, p, n, ef, limit, name, datasets)) {
            Problem<?> prob = experiment.problem;

            if (prob.getNumberOfVariables() > 1000) {
                continue;
            }

            try {
                DoubleProblem problem = (DoubleProblem) prob;
                Algorithm<?> algorithm = new CovarianceMatrixAdaptationEvolutionStrategy.Builder(problem).setMaxEvaluations(10000000).build();
                experiment.algorithm = algorithm;
                experiments.add(experiment);
            } catch (ClassCastException ifNotDouble) {
            }
        }

        return experiments;
    }

    static List<Experiment> problems(int a, int p, int n, ErrorFunction ef, int limit, String name, List<BinDataset> datasets) {
        final List<Experiment> problems = new ArrayList<Experiment>();

        {
            Limited lef = new Limited(ef, limit);
            Problem<?> problem = (new GDSProblem(a, p, n, lef, datasets));
            problems.add(new Experiment(problem, lef, name));
        }
        {
            Limited lef = new Limited(ef, limit);
            Problem<?> problem = (new GDSProblem(a, p, n, lef, null));
            problems.add(new Experiment(problem, lef, name));
        }
        {
            Limited lef = new Limited(ef, limit);
            Problem<?> problem = (new SimpleProblem(a, p, n, lef, datasets));
            problems.add(new Experiment(problem, lef, name));
        }
        {
            Limited lef = new Limited(ef, limit);
            Problem<?> problem = (new SimpleProblem(a, p, n, lef, null));
            problems.add(new Experiment(problem, lef, name));
        }
        {
            Limited lef = new Limited(ef, limit);
            Problem<?> problem = new PGProblem(a, p, n, lef, new BayesNetGen(a, p, n));
            problems.add(new Experiment(problem, lef, name));
        }
        {
            Limited lef = new Limited(ef, limit);
            Problem<?> problem = new PGProblem(a, p, n, lef, new GMMGen(a, p, n));
            problems.add(new Experiment(problem, lef, name));
        }
        {
            Limited lef = new Limited(ef, limit);
            Problem<?> problem = new PGProblem(a, p, n, lef, new RBFGen(a, p, n));
            problems.add(new Experiment(problem, lef, name));
        }
        {
            Limited lef = new Limited(ef, limit);
            Problem<?> problem = new PGProblem(a, p, n, lef, new RDG1Gen(a, p, n));
            problems.add(new Experiment(problem, lef, name));
        }

        return problems;
    }
}
