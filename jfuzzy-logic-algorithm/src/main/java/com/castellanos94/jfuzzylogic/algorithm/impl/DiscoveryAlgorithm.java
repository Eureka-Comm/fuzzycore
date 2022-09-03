package com.castellanos94.jfuzzylogic.algorithm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.castellanos94.jfuzzylogic.algorithm.AMembershipFunctionOptimizer;
import com.castellanos94.jfuzzylogic.algorithm.Algorithm;
import com.castellanos94.jfuzzylogic.core.OperatorUtil;
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
        this.optimizer = new MembershipFunctionOptimizer(logic, table, adjMaxIteration, adjPopulationSize,
                adjMinTruthValue, adjCrossoverRate, adjMutationRate);
    }

    @Override
    public void execute() {
        this.startTime = System.currentTimeMillis();
        this.discoveryPredicates = new ArrayList<>(this.maximumNumberResult);
        ArrayList<Generator> generators = OperatorUtil.getNodesByClass(predicate, Generator.class);
        if (generators.isEmpty()) {
            this.discoveryPredicates.add(optimizer.execute(predicate).copy());
        }
        this.endTime = System.currentTimeMillis();
    }

    @Override
    public DiscoveryResult getResult() {
        // TODO Auto-generated method stub
        return null;
    }

    public DiscoveryAlgorithm setRandom(Random random) {
        this.random = random;
        return this;
    }

    public Random getRandom() {
        return random;
    }

}
