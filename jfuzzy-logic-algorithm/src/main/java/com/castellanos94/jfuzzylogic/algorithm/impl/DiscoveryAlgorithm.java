package com.castellanos94.jfuzzylogic.algorithm.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.castellanos94.jfuzzylogic.algorithm.AMembershipFunctionOptimizer;
import com.castellanos94.jfuzzylogic.algorithm.Algorithm;
import com.castellanos94.jfuzzylogic.algorithm.JFuzzyLogicAlgorithmError;
import com.castellanos94.jfuzzylogic.core.OperatorUtil;
import com.castellanos94.jfuzzylogic.core.base.AElement;
import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.impl.DiscoveryResult;
import com.castellanos94.jfuzzylogic.core.base.impl.Generator;
import com.castellanos94.jfuzzylogic.core.logic.Logic;

import tech.tablesaw.api.Table;

/**
 * Algorithm for performance discovery using a predicate
 * 
 * @version 0.1.0
 * @apiNote {@link MembershipFunctionOptimizer} default optimizer
 */
public class DiscoveryAlgorithm extends Algorithm {
    private static final Logger log = LogManager.getLogger(DiscoveryAlgorithm.class);
    /**
     * Predicate guide
     */
    protected final Operator predicate;
    /**
     * Logic for evaluation
     */
    protected Logic logic;
    /**
     * Data set
     */
    protected Table table;
    /**
     * Maximum execution time in ms
     */
    protected final Long maximumTime;
    /**
     * Min truth value predicate
     */
    protected Double minTruthValue;
    /**
     * Crossover probability
     */
    protected Double crossoverRate;
    /**
     * Mutation probability
     */
    protected Double mutationRate;
    /**
     * Maximum number of results
     */
    protected Integer maximumNumberResult;
    /**
     * Population size
     */
    protected Integer populationSize;
    /**
     * Min truth value for membership function optimizer
     */
    protected Double adjMinTruthValue;
    /**
     * Crossover probability for membership function optimizer
     */
    protected Double adjCrossoverRate;
    /**
     * Mutation probability for membership function optimizer
     */
    protected Double adjMutationRate;
    /**
     * Population for membership function optimizer
     */
    protected Integer adjPopulationSize;
    /**
     * Maximum iterations for membership function optimizer
     */
    protected Integer adjMaxIteration;

    protected List<Operator> discoveryPredicates;

    protected Random random;

    protected AMembershipFunctionOptimizer optimizer;

    protected boolean run;

    /**
     * Default constructor
     * 
     * @param predicate           guide
     * @param maximumTime         maximum execution time
     * @param logic               for evaluation
     * @param table               data set
     * @param minTruthValue       predicate min value criteria
     * @param crossoverRate       crossover probability
     * @param mutationRate        mutation probability
     * @param maximumNumberResult maximum number results
     * @param populationSize      population size
     * @param adjMinTruthValue    predicate min value criteria for membership
     *                            optimizer
     * @param adjCrossoverRate    crossover probability criteria for membership
     *                            optimizer
     * @param adjMutationRate     mutation probability criteria for membership
     *                            optimizer
     * @param adjPopulationSize   population size criteria for membership
     *                            optimizer
     * @param adjMaxIteration     max iterations criteria for membership
     *                            optimizer
     * @apiNote {@link MembershipFunctionOptimizer} for membership function
     *          optimizer default
     */
    public DiscoveryAlgorithm(Operator predicate, Long maximumTime, Logic logic, Table table, Double minTruthValue,
            Double crossoverRate,
            Double mutationRate,
            Integer maximumNumberResult, Integer populationSize, Double adjMinTruthValue, Double adjCrossoverRate,
            Double adjMutationRate, Integer adjPopulationSize, Integer adjMaxIteration) {
        this.predicate = predicate;
        this.maximumTime = maximumTime;
        this.logic = logic;
        this.table = table;
        this.minTruthValue = minTruthValue;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.maximumNumberResult = maximumNumberResult;
        this.populationSize = populationSize;
        this.adjMinTruthValue = adjMinTruthValue;
        this.adjCrossoverRate = adjCrossoverRate;
        this.adjMutationRate = adjMutationRate;
        this.adjPopulationSize = adjPopulationSize;
        this.adjMaxIteration = adjMaxIteration;
        this.run = true;
        this.optimizer = new MembershipFunctionOptimizer(logic, table, adjMaxIteration, adjPopulationSize,
                adjMinTruthValue, adjCrossoverRate, adjMutationRate);
        this.random = new Random();
    }

    @Override
    public void execute() {
        this.startTime = System.currentTimeMillis();
        this.discoveryPredicates = new ArrayList<>();
        ArrayList<Generator> generators = OperatorUtil.getNodesByClass(predicate, Generator.class);
        if (generators.isEmpty()) {
            this.discoveryPredicates.add(optimizer.execute(predicate).copy());
        } else {
            Operator[] population = new Operator[populationSize];
            // Generate random population
            for (int i = 0; i < populationSize; i++) {
                population[i] = createRandomIndividual(generators, i);
            }
            // Evaluate initial populaiton
            Arrays.parallelSetAll(population, idx -> {
                return optimizer.copy().execute(population[idx]).copy();
            });
            Arrays.sort(population);
            log.info("End time for evalution of random population: {} ms", (System.currentTimeMillis() - startTime));
            Set<Integer> indexToReplace = new HashSet<>();
            for (int i = 0; i < populationSize; i++) {
                if (population[i].getFitness() >= minTruthValue) {
                    if (!expressionExists(population[i].toString(), discoveryPredicates)) {
                        discoveryPredicates.add(population[i].copy());
                        indexToReplace.add(i);
                    } else {
                        // Replace if better
                        for (int j = 0; j < discoveryPredicates.size(); j++) {
                            if (population[i].compareTo(discoveryPredicates.get(j)) > 0) {
                                discoveryPredicates.set(j, population[i].copy());
                                indexToReplace.add(i);
                                break;
                            }
                        }
                    }
                }
            }
            long elapsedTime = System.currentTimeMillis() - startTime;
            int offspringSize = populationSize / 2;
            while (offspringSize % 2 != 0) {
                offspringSize++;
            }
            // check stop condition
            int aIndex, bIndex;
            Operator a, b, c;
            Operator[] offspring = new Operator[offspringSize];
            int currentIteration = 1;
            while (discoveryPredicates.size() < maximumNumberResult && elapsedTime < maximumTime && run) {
                log.error("Current iteration {}, time {} ms", currentIteration, elapsedTime);
                if (!indexToReplace.isEmpty()) {
                    Operator[] nOperators = new Operator[indexToReplace.size()];
                    for (Integer idx : indexToReplace) {
                        nOperators[idx] = createRandomIndividual(generators, idx);
                    }
                    Arrays.parallelSetAll(nOperators, idx -> {
                        return optimizer.copy().execute(nOperators[idx]).copy();
                    });
                    indexToReplace.clear();
                }
                // offspring generation
                for (int i = 0; i < offspringSize; i++) {
                    // Random parent selection
                    aIndex = random.nextInt(populationSize);
                    do {
                        bIndex = random.nextInt(populationSize);
                    } while (aIndex == bIndex);
                    // crossover
                    a = population[aIndex];
                    b = population[bIndex];
                    c = crossover(a, b);
                    c = mutation(c, generators);
                    offspring[i] = c;
                }
                // evaluate offspring
                Arrays.parallelSetAll(offspring, idx -> {
                    return optimizer.copy().execute(offspring[idx]).copy();
                });
                // Replace population
                for (int i = 0; i < offspringSize; i++) {
                    a = offspring[i];
                    for (int j = 0; j < populationSize; j++) {
                        b = population[j];
                        if (a.compareTo(b) > 0) {
                            population[j] = offspring[i].copy();
                            break;
                        }
                    }
                }
                // Pass to discovery result
                HashMap<String, Integer> expressionMap = new HashMap<>();
                for (int i = 0; i < populationSize; i++) {
                    String exp = population[i].toString();
                    expressionMap.put(exp, expressionMap.getOrDefault(exp, 0) + 1);
                    if (population[i].getFitness() >= minTruthValue) {
                        if (!expressionExists(exp, discoveryPredicates)) {
                            discoveryPredicates.add(population[i].copy());
                            indexToReplace.add(i);
                        } else {
                            // Replace if better
                            for (int j = 0; j < discoveryPredicates.size(); j++) {
                                if (population[i].compareTo(discoveryPredicates.get(j)) > 0) {
                                    discoveryPredicates.set(j, population[i].copy());
                                    indexToReplace.add(i);
                                    break;
                                }
                            }
                        }
                    }
                }
                // We check if there is no stagnation in the search
                expressionMap.forEach((k, v) -> {
                    if (v > 1) {
                        log.error("Expression {} - {}", k, v);
                        if (v > populationSize / 5 && indexToReplace.size() > populationSize / 2) {
                            int cnt = 0;
                            while (cnt < populationSize / 5) {
                                if (indexToReplace.add(random.nextInt(populationSize))) {
                                    cnt++;
                                }
                            }
                        }
                    }
                });

                // update stop condition
                elapsedTime = System.currentTimeMillis() - startTime;
            }

            if (this.discoveryPredicates.size() < 2) {
                int size = Math.min(10 - this.discoveryPredicates.size(), populationSize);
                log.error("Few results exist {}, copying the best ones {}", this.discoveryPredicates, size);
                Arrays.sort(population);
                for (int i = 0; i < size; i++) {
                    this.discoveryPredicates.add(population[i]);
                }
            }
        }
        this.endTime = System.currentTimeMillis();
        log.error("Discovery Results {}, elapsed time {} ms", this.discoveryPredicates.size(), this.getComputeTime());
    }

    private Operator mutation(Operator c, List<Generator> generators) {
        if (random.nextDouble() <= mutationRate) {
            return createRandomIndividual(generators, random.nextInt());
        }
        return c;
    }

    private Operator crossover(Operator a, Operator b) {
        Operator c = a.copy();
        List<AElement> aEdit = OperatorUtil.getEditableNode(a);
        List<AElement> bEdit = OperatorUtil.getEditableNode(b);
        if (aEdit.isEmpty() || bEdit.isEmpty()) {
            log.error("A {} - {}", a, aEdit.size());
            log.error("B {} - {}", b, bEdit.size());
            throw new JFuzzyLogicAlgorithmError("Not editable nodes for crossover " + predicate);
        }
        AElement aCand = aEdit.get(random.nextInt(aEdit.size()));
        int aNivel = OperatorUtil.dfs(a, aCand);
        AElement bCand;
        int bNivel;
        do {
            bCand = bEdit.get(random.nextInt(bEdit.size()));
            bNivel = OperatorUtil.dfs(b, bCand);
        } while (bNivel > aNivel);
        OperatorUtil.replace(c, aCand, bCand);
        return c;
    }

    protected void setRun(boolean flag) {
        this.run = flag;
    }

    private boolean expressionExists(String expression, List<Operator> set) {
        if (set.isEmpty())
            return false;
        Iterator<Operator> iterator = set.iterator();
        String exp;
        while (iterator.hasNext()) {
            exp = iterator.next().toString();
            if (exp.equals(expression)) {
                return true;
            }
        }
        return false;
    }

    private Operator createRandomIndividual(List<Generator> generators, int index) {
        boolean flag = predicate instanceof Generator;
        Iterator<Generator> iterator = generators.iterator();
        List<Operator> offspring = new ArrayList<>();

        while (iterator.hasNext()) {
            Generator next = iterator.next();
            Operator operator = PredicateGenerator.generate(random, next, index < populationSize / 2);
            if (flag) {
                return operator;
            } else {
                offspring.add(operator);
            }
        }
        Operator p = predicate.copy();
        for (Operator operator : offspring) {
            Generator g = generators.stream().filter(gs -> gs.getUuid().equals(operator.getFrom())).findAny().get();
            Operator root = OperatorUtil.getRoot(p, operator);
            if (root != null && root != p) {
                p = OperatorUtil.replace(root, g, operator);
            } else {
                log.error("Predicate {}", p);
                log.error("Child {} from {}", operator, g);
                throw new JFuzzyLogicAlgorithmError(
                        "Illegal assignment at createRandomIndividual with multiple generators");
            }
        }
        return p;
    }

    @Override
    public DiscoveryResult getResult() {
        DiscoveryResult result = new DiscoveryResult();
        result.setData(new LinkedList<>(discoveryPredicates));
        result.setStartTime(startTime);
        result.setEndTime(endTime);
        return result;
    }

    public DiscoveryAlgorithm setRandom(Random random) {
        this.random = random;
        return this;
    }

    public Random getRandom() {
        return random;
    }

}
