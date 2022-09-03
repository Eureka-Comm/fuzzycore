package com.castellanos94.jfuzzylogic.algorithm.impl;

import java.util.Random;

import com.castellanos94.jfuzzylogic.algorithm.Algorithm;
import com.castellanos94.jfuzzylogic.core.base.impl.DiscoveryResult;
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
     * Logic for evaluation
     */
    protected Logic logic;
    /**
     * Data set
     */
    protected Table table;
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

    protected Random random;

    protected DiscoveryAlgorithm() {

    }

    @Override
    public void execute() {
        // TODO Auto-generated method stub

    }

    @Override
    public DiscoveryResult getResult() {
        // TODO Auto-generated method stub
        return null;
    }

}
