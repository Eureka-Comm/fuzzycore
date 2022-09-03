package com.castellanos94.jfuzzylogic.algorithm.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.castellanos94.jfuzzylogic.algorithm.AMembershipFunctionOptimizer;
import com.castellanos94.jfuzzylogic.algorithm.MembershipFunctionChromosome;
import com.castellanos94.jfuzzylogic.core.OperatorUtil;
import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.impl.State;
import com.castellanos94.jfuzzylogic.core.logic.Logic;
import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.FPG;

import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;

/**
 * Membership function optimizer
 * 
 * @version 0.5.0
 * @apiNote {@link FPGGenerator} default generator
 * @apiNote {@link FPGCrossover} default crossover
 * @apiNote {@link FPGRepair} default repair
 */
public class MembershipFunctionOptimizer extends AMembershipFunctionOptimizer {
    @SuppressWarnings("unused")
    private static final Logger log = LogManager.getLogger(MembershipFunctionOptimizer.class);
    /**
     * Maximum process iterations
     */
    protected final Integer maxIterations;
    /**
     * Population size
     */
    protected final Integer populationSize;
    /**
     * Min truth value
     */
    protected final Double minTruthValue;
    /**
     * Crossover probability
     */
    protected final Double crossoverRate;
    /**
     * Mutation probability
     */
    protected final Double mutationRate;
    /**
     * Logic for evaluation
     */
    protected final Logic logic;
    /**
     * Data set
     */
    protected final Table table;
    /**
     * States to work
     */
    protected final List<State> states;
    /**
     * Aux predicate
     */
    protected Operator predicate;

    public MembershipFunctionOptimizer(Logic logic, Table table, Integer maxIterations,
            Integer populationSize, Double minTruthValue, Double crossoverProbability, Double mutationProbability) {
        super(new Random());
        Objects.requireNonNull(table);
        this.table = table;
        this.logic = logic;
        this.maxIterations = maxIterations;
        this.populationSize = populationSize;
        this.minTruthValue = minTruthValue;
        this.crossoverRate = crossoverProbability;
        this.mutationRate = mutationProbability;
        this.register(FPG.class, new FPGGenerator());
        this.register(FPG.class, new FPGRepair());
        this.register(FPG.class, new FPGCrossover(crossoverProbability));
        this.states = new ArrayList<>();
    }

    @Override
    public Operator execute(Operator predicate) {
        this.startTime = System.currentTimeMillis();
        this.predicate = predicate;
        states.clear();
        states.addAll(OperatorUtil.getNodesByClass(predicate, State.class)
                .stream()
                .filter(s -> s.getMembershipFunction() == null || !s.getMembershipFunction().isValid()
                        || s.isEditable())
                .collect(Collectors.toList()));
        this.stateIdByClass = this.identifyClass(states, FPG.class);
        // Validate that repair operators exist for the functions to be optimized.
        validateOperators(states, FPG.class);
        // Generate boundaries
        this.boundaries = generateBoundaries(states);
        // Generate random population
        List<MembershipFunctionChromosome> population = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            MembershipFunctionChromosome sol = generate(states);
            evaluate(states, sol);
            population.add(sol);
        }

        int currentIteration = 1;
        int maxValueIndex = getMaxValueIndex(population);
        int offspringSize = populationSize / 2;
        while (offspringSize % 2 != 0) {
            offspringSize++;
        }
        while (currentIteration < maxIterations && population.get(maxValueIndex).getFitness() < minTruthValue) {
            if (Utils.equals(population.get(maxValueIndex).getFitness(), minTruthValue)) {
                break;
            }
            // log.error("Iteration {}, fitness
            // {}",currentIteration,population.get(maxValueIndex).getFitness());
            List<MembershipFunctionChromosome> offspring = new ArrayList<>(offspringSize);
            // Crossover
            for (int i = 0; i < offspringSize; i++) {
                MembershipFunctionChromosome a = population.get(random.nextInt(populationSize));
                MembershipFunctionChromosome b = population.get(random.nextInt(populationSize));
                MembershipFunctionChromosome c = crossover(a, b);
                mutation(c);
                repair(c, states);
                evaluate(states, c);
                offspring.add(c);
            }
            // Replace population
            Collections.sort(offspring, Collections.reverseOrder());
            Iterator<MembershipFunctionChromosome> iterator = offspring.iterator();
            while (iterator.hasNext()) {
                MembershipFunctionChromosome a = iterator.next();
                for (int j = 0; j < populationSize; j++) {
                    if (a.compareTo(population.get(j)) > 0) {
                        population.set(j, a);
                        iterator.remove();
                        break;
                    }
                }
            }
            // find best
            maxValueIndex = getMaxValueIndex(population);
            currentIteration++;
        }

        MembershipFunctionChromosome solution = population.get(maxValueIndex);
        for (State state : states) {
            state.setMembershipFunction(solution.getFunction(state.getUuid()));
        }
        new EvaluationAlgorithm(predicate, logic, table).execute();
        this.endTime = System.currentTimeMillis();
        return predicate;
    }

    /**
     * Repair operator
     * 
     * @param chromosome individual
     * @param states     to work
     */
    private void repair(MembershipFunctionChromosome chromosome, List<State> states) {
        for (int i = 0; i < states.size(); i++) {
            Class<? extends MembershipFunction> clazz = this.stateIdByClass.get(chromosome.getId(i));
            MembershipFunction[] boundaries = this.boundaries.get(states.get(i).getUuid());
            chromosome.setFunction(i,
                    this.repairOperators.get(clazz).execute(chromosome.getFunction(i), boundaries[0], boundaries[1]));
        }
    }

    @Override
    protected void mutation(MembershipFunctionChromosome chromosome) {
        for (int i = 0; i < chromosome.getSize(); i++) {
            Class<? extends MembershipFunction> clazz = this.stateIdByClass.get(chromosome.getId(i));
            if (this.mutationOperator.isEmpty() || random.nextDouble() <= this.mutationRate) {
                MembershipFunction[] boundary = this.boundaries.get(chromosome.getId(i));
                chromosome.setFunction(i, this.generatorOperator.get(clazz).generate(boundary[0], boundary[1]));
            } else {
                chromosome.setFunction(i, this.mutationOperator.get(clazz).execute(chromosome.getFunction(i)));
            }
        }
    }

    @Override
    protected void evaluate(List<State> states, MembershipFunctionChromosome solution) {
        // Operator operator = (Operator) predicate.copy();
        // ArrayList<State> _states = OperatorUtil.getNodesByClass(operator,
        // State.class);
        for (State state : states) {
            // State toUpd = _states.get(_states.indexOf(state));
            state.setMembershipFunction(solution.getFunction(state.getUuid()));
        }
        new EvaluationAlgorithm(predicate, logic, table).execute();
        solution.setFitness(predicate.getFitness());
    }

    @Override
    protected Map<String, MembershipFunction[]> generateBoundaries(List<State> states) {
        Map<String, MembershipFunction[]> boundaries = new HashMap<>();
        for (State state : states) {
            Class<?> clazz = this.stateIdByClass.get(state.getUuid());
            NumericColumn<?> column = table.numberColumn(state.getColName());
            boundaries.put(state.getUuid(),
                    this.generatorOperator.get(clazz).generateBoundaries(state.getMembershipFunction(), column.min(),
                            column.mean(), column.max()));
        }
        return boundaries;
    }

    @Override
    protected MembershipFunctionChromosome generate(List<State> states) {
        MembershipFunctionChromosome chromosome = new MembershipFunctionChromosome(states.size());
        for (int i = 0; i < states.size(); i++) {
            Class<?> clazz = this.stateIdByClass.get(states.get(i).getUuid());
            MembershipFunction[] ref = this.boundaries.get(states.get(i).getUuid());
            chromosome.setFunction(i,
                    this.generatorOperator.get(clazz).generate(ref[0], ref[1]));
            chromosome.setId(i, states.get(i).getUuid());
        }
        return chromosome;
    }

    @Override
    public MembershipFunctionOptimizer copy() {
        MembershipFunctionOptimizer cpy = new MembershipFunctionOptimizer(logic, table, maxIterations, populationSize,
                minTruthValue, crossoverRate, mutationRate);

        return cpy;
    }

}
