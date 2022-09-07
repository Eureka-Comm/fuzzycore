package com.castellanos94.jfuzzylogic.algorithm.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
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
import com.castellanos94.jfuzzylogic.core.base.impl.And;
import com.castellanos94.jfuzzylogic.core.base.impl.DiscoveryResult;
import com.castellanos94.jfuzzylogic.core.base.impl.Eqv;
import com.castellanos94.jfuzzylogic.core.base.impl.Generator;
import com.castellanos94.jfuzzylogic.core.base.impl.Imp;
import com.castellanos94.jfuzzylogic.core.base.impl.Not;
import com.castellanos94.jfuzzylogic.core.base.impl.Or;
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
    /**
     * Maximum tolerance for repeated predicates
     */
    protected int maximumToleranceForRepeated;

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
        this.maximumToleranceForRepeated = populationSize / 20;
        if (maximumToleranceForRepeated < 2) {
            this.maximumNumberResult = 2;
        }
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
                population[i] = createRandomIndividual(generators, i < populationSize / 2);
            }
            // Evaluate initial populaiton
            Arrays.parallelSetAll(population, idx -> {
                return optimizer.copy().execute(population[idx]).copy();
            });
            Arrays.sort(population, Collections.reverseOrder());
            log.info("End time for evalution of random population: {} ms", (System.currentTimeMillis() - startTime));
            Set<Integer> indexToReplace = new HashSet<>();
            for (int i = 0; i < populationSize; i++) {
                if (population[i].getFitness() >= minTruthValue) {
                    int idx = expressionExists(population[i].toString(), discoveryPredicates);
                    if (idx == -1) {
                        discoveryPredicates.add(population[i].copy());
                        indexToReplace.add(i);
                    } else {
                        // Replace if better
                        if (population[i].compareTo(discoveryPredicates.get(idx)) > 0) {
                            discoveryPredicates.set(idx, population[i].copy());
                            indexToReplace.add(i);
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
            long currentIteration = 1;
            Iterator<Integer> replaceIterator;
            while (discoveryPredicates.size() < maximumNumberResult && elapsedTime < maximumTime && run) {
                log.error("Current iteration {}, results {},  time {} ms, to replace {}", currentIteration,
                        discoveryPredicates.size(), elapsedTime, indexToReplace.size());
                if (!indexToReplace.isEmpty()) {
                    Operator[] nOperators = new Operator[indexToReplace.size()];
                    replaceIterator = indexToReplace.iterator();
                    for (int idx = 0; idx < nOperators.length; idx++) {
                        nOperators[idx] = createRandomIndividual(generators, random.nextDouble() < 0.5);
                    }
                    Arrays.parallelSetAll(nOperators, idx -> {
                        return optimizer.copy().execute(nOperators[idx]).copy();
                    });
                    replaceIterator = indexToReplace.iterator();
                    for (int idx = 0; idx < nOperators.length; idx++) {
                        population[replaceIterator.next()] = nOperators[idx];
                    }
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
                    c = crossover(a, b, generators);
                    c = mutation(c, generators);
                    offspring[i] = c;
                }
                // evaluate offspring
                Arrays.parallelSetAll(offspring, _idx -> {
                    return optimizer.copy().execute(offspring[_idx]).copy();
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
                HashMap<String, List<Integer>> expressionMap = new HashMap<>();
                for (int i = 0; i < populationSize; i++) {
                    String exp = population[i].toString();
                    List<Integer> indexList = expressionMap.getOrDefault(exp, new ArrayList<>());
                    indexList.add(i);
                    expressionMap.putIfAbsent(exp, indexList);
                    if (population[i].getFitness() >= minTruthValue) {
                        int idx = expressionExists(exp, discoveryPredicates);
                        if (idx == -1) {
                            discoveryPredicates.add(population[i].copy());
                            indexToReplace.add(i);
                        } else {
                            // Replace if better
                            if (population[i].compareTo(discoveryPredicates.get(idx)) > 0) {
                                discoveryPredicates.set(idx, population[i].copy());
                                indexToReplace.add(i);
                            }
                        }
                    }
                }
                // We check if there is no stagnation in the search
                // log.error("Diversity {}, maximum tolerance for repeated predicates {}",
                // expressionMap.size(),maximumToleranceForRepeated);
                expressionMap.forEach((k, v) -> {
                    if (v.size() > maximumToleranceForRepeated) {
                        // log.error("\t {} - {}", v.size(), k);
                        if (v.size() > maximumToleranceForRepeated) {
                            indexToReplace.addAll(v.subList(0, v.size() - 1));
                        }
                    }
                });
                if (expressionMap.size() < ((2 * populationSize) / 5) && indexToReplace.size() < (populationSize / 2)) {
                    int cnt = 0;
                    int intents = 0;
                    while (cnt < (3 * populationSize) / 5 && intents < 2 * populationSize) {
                        if (indexToReplace.add(random.nextInt(populationSize))) {
                            cnt++;
                        }
                        intents++;
                    }
                }

                // update stop condition
                elapsedTime = System.currentTimeMillis() - startTime;
                currentIteration++;
            }
            Arrays.sort(population, Collections.reverseOrder());
            if (this.discoveryPredicates.size() < 2) {
                int size = Math.min(10 - this.discoveryPredicates.size(), populationSize);
                log.error("Few results exist {}, copying the best ones {}", this.discoveryPredicates, size);
                for (int i = 0; i < size; i++) {
                    this.discoveryPredicates.add(population[i]);
                }
            }
        }
        Collections.sort(discoveryPredicates, Collections.reverseOrder());
        this.endTime = System.currentTimeMillis();
        log.error("Discovery Results {}, elapsed time {} ms", this.discoveryPredicates.size(), this.getComputeTime());
    }

    private Operator mutation(Operator c, List<Generator> generators) {
        if (random.nextDouble() <= mutationRate) {
            return createRandomIndividual(generators, random.nextDouble() < 0.5);
        }
        return c;
    }

    private Operator crossover(Operator a, Operator b, List<Generator> generators) {
        if (a.toString().equals(b.toString())) {
            return createRandomIndividual(generators, random.nextDouble() < this.crossoverRate);
        }

        List<AElement> aEdit = OperatorUtil.getEditableNode(a);
        List<AElement> bEdit = OperatorUtil.getEditableNode(b);

        if (bEdit.isEmpty() || aEdit.isEmpty()) {
            log.error("A {} - {}", a, aEdit.size());
            log.error("B {} - {}", b, bEdit.size());
            throw new JFuzzyLogicAlgorithmError("Not editable nodes for crossover " + predicate);
        }
        Operator c = a.copy();// OperatorUtil.getInstance(OperatorUtil.getType(a));

        if (!OperatorUtil.isValid(c)) {
            log.error("Copy Invalid predicate {}", c);
        }
        List<AElement> successors = OperatorUtil.getSuccessors(c);
        int minSize = successors.size();/*
                                         * (int) (successors.size() > 2 ? Utils.randomNumber(random, 2.0, (double)
                                         * successors.size())
                                         * : getMinimumChildRequired(c));
                                         */
        int currentSize = 0;
        AElement aux, nVal;
        while (currentSize < minSize) {
            aux = successors.get(currentSize);
            if (aux.isEditable()) {
                Operator tmp;
                int intents = 0;
                if (random.nextDouble() <= this.crossoverRate) {
                    do {
                        nVal = bEdit.get(random.nextInt(bEdit.size())).copy();
                        tmp = OperatorUtil.replace(c, aux, nVal);
                        intents++;
                    } while (!OperatorUtil.isValid(tmp) && intents < bEdit.size() * 2);
                    if (intents > bEdit.size() * 2) {
                        return createRandomIndividual(generators, random.nextDouble() <= this.crossoverRate);
                    }
                }
            }
            currentSize++;
        }

        if (!OperatorUtil.isValid(c)) {
            log.error("Invalid predicate {}", c);
        }
        return c;
    }

    @SuppressWarnings("unused")
    private int getMinimumChildRequired(Operator c) {
        Objects.requireNonNull(c);
        if (c instanceof And || c instanceof Or || c instanceof Imp || c instanceof Eqv) {
            return 2;
        }
        if (c instanceof Not)
            return 1;
        throw new JFuzzyLogicAlgorithmError("Unknown minimum type for " + c);
    }

    protected void setRun(boolean flag) {
        this.run = flag;
    }

    private int expressionExists(String expression, List<Operator> set) {
        if (set.isEmpty())
            return -1;
        String exp;
        for (int i = 0; i < set.size(); i++) {
            exp = set.get(i).toString();
            if (exp.equals(expression)) {
                return i;
            }
        }
        return -1;
    }

    private Operator createRandomIndividual(List<Generator> generators, boolean isBalanced) {
        boolean flag = predicate instanceof Generator;
        Iterator<Generator> iterator = generators.iterator();
        List<AElement> offspring = new ArrayList<>();

        while (iterator.hasNext()) {
            Generator next = iterator.next();
            AElement e = PredicateGenerator.generate(random, flag ? null : predicate, next, isBalanced);
            if (flag) {
                return (Operator) e;
            } else {
                offspring.add(e);
            }
        }
        Operator p = predicate.copy();
        for (AElement element : offspring) {
            Generator g = generators.stream().filter(gs -> gs.getUuid().equals(element.getFrom())).findAny().get();
            if (p != null) {
                p = OperatorUtil.replace(p, g, element);
            } else {
                log.error("Predicate {}", p);
                log.error("Child {} from {}", element, g);
                throw new JFuzzyLogicAlgorithmError(
                        "Illegal assignment at createRandomIndividual with multiple generators");
            }
        }
        if (!OperatorUtil.isValid(p)) {
            log.error("Invalid {}", p);
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
