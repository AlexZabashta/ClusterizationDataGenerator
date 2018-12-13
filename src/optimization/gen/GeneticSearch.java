package optimization.gen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import distribution.UniformNorm;
import optimization.Crossover;
import optimization.Measurable;
import optimization.Mutation;
import optimization.result.Result;
import optimization.result.StoppingCriterion;

public class GeneticSearch<T extends Measurable> {

    final int threads = 16;
    final RouletteWheel<T> rouletteWheel;
    final Random random = new Random();
    final Crossover<T> crossover;
    final Mutation<T> mutation;

    public GeneticSearch(Crossover<T> crossover, Mutation<T> mutation) {
        this.rouletteWheel = new RouletteWheel<T>(new UniformNorm());
        this.crossover = crossover;
        this.mutation = mutation;
    }

    public Result<T> search(List<T> generation, double crossoverRate, double mutationRate, StoppingCriterion<T> stoppingCriterion, List<Result<T>> results) {
        final int generationSize = generation.size();
        int numFunctionEval = 0;
        int maxIter = 10000;
        int it = 0;

        int numOfChild = Math.max(generationSize, (int) (generationSize * crossoverRate)) / 2;
        int numOfMut = Math.max(generationSize, (int) (generationSize * mutationRate));

        while (true) {

            Result<T> result = new Result<T>(numFunctionEval, it, generation);
            if (results != null) {
                results.add(result);
            }

            if (stoppingCriterion.test(result) || (it++ >= maxIter)) {
                return result;
            }

            numFunctionEval += numOfChild;
            numFunctionEval += numOfMut;

            List<T> newGeneration = new ArrayList<T>();
            newGeneration.addAll(generation);

            List<T> parents = rouletteWheel.select(generation, numOfChild * 2, random);
            List<Runnable> crossovers = new ArrayList<>();

            for (int t = 0; t < threads; t++) {
                final int offset = t;
                crossovers.add(new Runnable() {
                    @Override
                    public void run() {
                        Random random = new Random();
                        for (int i = offset; i < numOfChild; i += threads) {
                            int u = 2 * i, v = u + 1;
                            List<T> children = crossover.cross(parents.get(u), parents.get(v), random);
                            synchronized (newGeneration) {
                                newGeneration.addAll(children);
                            }
                        }
                    }
                });
            }
            ParallelRunnner.run(crossovers);

            List<Runnable> mutations = new ArrayList<>();

            List<T> mutants = rouletteWheel.select(newGeneration, numOfMut, random);

            for (int t = 0; t < threads; t++) {
                final int offset = t;
                mutations.add(new Runnable() {
                    @Override
                    public void run() {
                        Random random = new Random();
                        for (int i = offset; i < numOfMut; i += threads) {
                            List<T> mutant = mutation.mutate(mutants.get(i), random);
                            synchronized (newGeneration) {
                                newGeneration.addAll(mutant);
                            }
                        }
                    }
                });
            }
            ParallelRunnner.run(mutations);

            Collections.sort(newGeneration, new Comparator<T>() {
                @Override
                public int compare(T x, T y) {
                    return Double.compare(y.fitnessFunction(), x.fitnessFunction());
                }
            });

            generation = new ArrayList<>();
            for (int i = 0; i < generationSize; i++) {
                generation.add(newGeneration.get(i));
            }
            System.gc();
        }
    }
}
