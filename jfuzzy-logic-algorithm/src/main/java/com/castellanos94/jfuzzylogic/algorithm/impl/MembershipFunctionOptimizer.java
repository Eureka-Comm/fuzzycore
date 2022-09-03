package com.castellanos94.jfuzzylogic.algorithm.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.castellanos94.jfuzzylogic.algorithm.AMembershipFunctionOptimizer;
import com.castellanos94.jfuzzylogic.algorithm.MembershipFunctionChromosome;
import com.castellanos94.jfuzzylogic.core.OperatorUtil;
import com.castellanos94.jfuzzylogic.core.base.JFuzzyLogicError;
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
 * @see FPGRepair FPG repair operator default
 * @see FPGGenerator FPG generator operator default
 */
public class MembershipFunctionOptimizer extends AMembershipFunctionOptimizer {
    private static final Logger log = LogManager.getLogger(MembershipFunctionOptimizer.class);
    protected Integer maxIterations;
    protected Integer populationSize;
    protected Double minTruthValue;
    protected Double crossoverRate;
    protected Double mutationRate;
    protected Logic logic;
    protected Table table;
    protected final List<State> states;
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
        for (State state : states) {
            MembershipFunction f = state.getMembershipFunction();
            if (f != null && !(f instanceof FPG)) {
                Class<?> clazz = f.getClass();
                if (!this.generatorOperator.containsKey(clazz)) {
                    throw new JFuzzyLogicError("No generator operator is registered for " + clazz);
                }
                if (!this.crossoverOperator.containsKey(clazz)) {
                    throw new JFuzzyLogicError("No crossover operator is registered for " + clazz);
                }
                if (!this.repairOperators.containsKey(clazz)) {
                    throw new JFuzzyLogicError("No repair operator is registered for " + clazz);
                }
                if (!mutationOperator.isEmpty() && !this.mutationOperator.containsKey(clazz)) {
                    throw new JFuzzyLogicError("No mutation operator is registered for " + clazz);
                }

            }
        }
        // Generate boundaries
        generateBoundaries(states);
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
            //log.error("Iteration {}, fitness {}",currentIteration,population.get(maxValueIndex).getFitness());
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

        Operator operator = (Operator) predicate.copy();
        MembershipFunctionChromosome solution = population.get(maxValueIndex);
        ArrayList<State> _states = OperatorUtil.getNodesByClass(operator, State.class);
        for (State state : states) {
            State toUpd = _states.get(_states.indexOf(state));
            toUpd.setMembershipFunction(solution.getFunction(state.getUuid()));
        }
        new EvaluationAlgorithm(operator, logic, table).execute();
        this.endTime = System.currentTimeMillis();
        return operator;
    }

    /**
     * Repair operator
     * 
     * @param chromosome individual
     * @param states     to work
     */
    private void repair(MembershipFunctionChromosome chromosome, List<State> states) {
        for (int i = 0; i < states.size(); i++) {
            Class<?> clazz = states.get(i).getMembershipFunction() == null ? FPG.class
                    : states.get(i).getMembershipFunction().getClass();
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
                chromosome.setFunction(i, this.generatorOperator.get(clazz).generate(boundary[0], boundary[1],
                        states.get(i).getMembershipFunction()));
            } else {
                chromosome.setFunction(i, this.mutationOperator.get(clazz).execute(chromosome.getFunction(i)));
            }
        }
    }

    @Override
    protected void evaluate(List<State> states, MembershipFunctionChromosome solution) {
        Operator operator = (Operator) predicate.copy();
        ArrayList<State> _states = OperatorUtil.getNodesByClass(operator, State.class);
        for (State state : states) {
            State toUpd = _states.get(_states.indexOf(state));
            toUpd.setMembershipFunction(solution.getFunction(state.getUuid()));
        }
        new EvaluationAlgorithm(operator, logic, table).execute();
        solution.setFitness(operator.getFitness());
    }

    @Override
    protected void generateBoundaries(List<State> states) {
        this.boundaries = new HashMap<>();
        for (State state : states) {
            Class<?> clazz = this.stateIdByClass.get(state.getUuid());
            NumericColumn<?> column = table.numberColumn(state.getColName());
            this.boundaries.put(state.getUuid(),
                    this.generatorOperator.get(clazz).generateBoundaries(column.min(), column.mean(), column.max()));
        }
    }

    @Override
    protected MembershipFunctionChromosome generate(List<State> states) {
        MembershipFunctionChromosome chromosome = new MembershipFunctionChromosome(states.size());
        for (int i = 0; i < states.size(); i++) {
            Class<?> clazz = this.stateIdByClass.get(states.get(i).getUuid());
            MembershipFunction[] ref = this.boundaries.get(states.get(i).getUuid());
            chromosome.setFunction(i,
                    this.generatorOperator.get(clazz).generate(ref[0], ref[1], states.get(i).getMembershipFunction()));
            chromosome.setId(i, states.get(i).getUuid());
        }
        return chromosome;
    }

}
