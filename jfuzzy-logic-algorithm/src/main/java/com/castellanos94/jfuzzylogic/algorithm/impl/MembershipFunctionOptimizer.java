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

import com.castellanos94.jfuzzylogic.algorithm.Algorithm;
import com.castellanos94.jfuzzylogic.algorithm.IMembershipFunctionCrossover;
import com.castellanos94.jfuzzylogic.algorithm.IMembershipFunctionGenerator;
import com.castellanos94.jfuzzylogic.algorithm.IMembershipFunctionMutation;
import com.castellanos94.jfuzzylogic.algorithm.IMembershipFunctionRepair;
import com.castellanos94.jfuzzylogic.algorithm.MembershipFunctionChromosome;
import com.castellanos94.jfuzzylogic.core.OperatorUtil;
import com.castellanos94.jfuzzylogic.core.base.JFuzzyLogicError;
import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.Result;
import com.castellanos94.jfuzzylogic.core.base.impl.EvaluationResult;
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
public class MembershipFunctionOptimizer extends Algorithm {
    private static final Logger log = LogManager.getLogger(MembershipFunctionOptimizer.class);
    protected Integer maxIterations;
    protected Integer populationSize;
    protected Double minTruthValue;
    protected Double crossoverRate;
    protected Double mutationRate;
    protected Operator predicate;
    protected Logic logic;
    protected Table table;
    protected HashMap<String, MembershipFunction[]> referenceValue;
    protected HashMap<Class<? extends MembershipFunction>, IMembershipFunctionRepair<? extends MembershipFunction>> repairOperators;
    protected HashMap<Class<? extends MembershipFunction>, IMembershipFunctionGenerator<? extends MembershipFunction>> generatorOperator;
    protected HashMap<Class<? extends MembershipFunction>, IMembershipFunctionCrossover<? extends MembershipFunction>> crossoverOperator;
    protected HashMap<Class<? extends MembershipFunction>, IMembershipFunctionMutation<? extends MembershipFunction>> mutationOperator;
    protected EvaluationAlgorithm evaluator;
    protected Random random;
    protected EvaluationResult result;

    public MembershipFunctionOptimizer(Operator predicate, Logic logic, Table table, Integer maxIterations,
            Integer populationSize, Double minTruthValue, Double crossoverProbability, Double mutationProbability) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(table);
        this.predicate = predicate;
        this.table = table;
        this.logic = logic;
        this.maxIterations = maxIterations;
        this.populationSize = populationSize;
        this.minTruthValue = minTruthValue;
        this.crossoverRate = crossoverProbability;
        this.mutationRate = mutationProbability;
        this.repairOperators = new HashMap<>();
        this.generatorOperator = new HashMap<>();
        this.crossoverOperator = new HashMap<>();
        this.mutationOperator = new HashMap<>();
        this.random = new Random();
        this.register(FPG.class, new FPGGenerator());
        this.register(FPG.class, new FPGRepair());
        this.register(FPG.class, new FPGCrossover(crossoverProbability));
    }

    @Override
    public void execute() {
        this.startTime = System.currentTimeMillis();
        final List<State> states = OperatorUtil.getNodesByClass(predicate, State.class)
                .stream()
                .filter(s -> s.getMembershipFunction() == null || !s.getMembershipFunction().isValid()
                        || s.isEditable())
                .collect(Collectors.toList());
        // Validate that repair operators exist for the functions to be optimized.
        for (State state : states) {
            MembershipFunction f = state.getMembershipFunction();
            if (f != null && !(f instanceof FPG)) {
                Class<?> clazz = f.getClass();
                if (!this.repairOperators.containsKey(clazz)) {
                    throw new JFuzzyLogicError("No repair operator is registered for " + clazz);
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
            List<MembershipFunctionChromosome> offspring = new ArrayList<>(offspringSize);
            // Crossover
            for (int i = 0; i < offspringSize; i++) {
                MembershipFunctionChromosome a = population.get(random.nextInt(populationSize));
                MembershipFunctionChromosome b = population.get(random.nextInt(populationSize));
                MembershipFunctionChromosome c = crossover(a, b, states);
                mutation(c, states);
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
        this.result = new EvaluationResult();
        this.result.setStartTime(startTime);
        this.result.setEndTime(endTime);
        this.result.setPredicate(operator);
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
            MembershipFunction[] boundaries = this.referenceValue.get(states.get(i).getUuid());
            chromosome.setFunction(i,
                    this.repairOperators.get(clazz).execute(chromosome.getFunction(i), boundaries[0], boundaries[1]));
        }
    }

    /**
     * Mutation operator
     * 
     * @param chromosome individual
     * @param states     to work
     */
    private void mutation(MembershipFunctionChromosome chromosome, List<State> states) {
        for (int i = 0; i < states.size(); i++) {
            Class<?> clazz = states.get(i).getMembershipFunction() == null ? FPG.class
                    : states.get(i).getMembershipFunction().getClass();
            if (this.mutationOperator.isEmpty() || random.nextDouble() <= this.mutationRate) {
                MembershipFunction[] boundaries = this.referenceValue.get(states.get(i).getUuid());
                chromosome.setFunction(i, this.generatorOperator.get(clazz).generate(boundaries[0], boundaries[1]));
            } else {
                chromosome.setFunction(i, this.mutationOperator.get(clazz).execute(chromosome.getFunction(i)));
            }
        }
    }

    /**
     * Creates a new individual using a uniform cross given two parents. This method
     * invoke crossoverOperator by class
     * 
     * @param a parent
     * @param b parent
     * @return child
     */
    private MembershipFunctionChromosome crossover(MembershipFunctionChromosome a, MembershipFunctionChromosome b,
            List<State> states) {
        MembershipFunctionChromosome c = new MembershipFunctionChromosome(states.size());
        for (int i = 0; i < states.size(); i++) {
            c.setId(i, states.get(i).getUuid());
            Class<?> clazz = states.get(i).getMembershipFunction() == null ? FPG.class
                    : states.get(i).getMembershipFunction().getClass();
            MembershipFunction[] boundaries = this.referenceValue.get(states.get(i).getUuid());
            c.setFunction(i, this.crossoverOperator.get(clazz).execute(a.getFunction(i), b.getFunction(i),
                    boundaries[0], boundaries[1]));
        }
        return c;
    }

    /**
     * Returns the index of the best solution in the set.
     * 
     * @param set to get the best index solution
     * @return
     */
    private int getMaxValueIndex(List<MembershipFunctionChromosome> set) {
        int index = 0;
        for (int i = 1; i < set.size(); i++) {
            if (set.get(index).getFitness().compareTo(set.get(i).getFitness()) > 0) {
                index = i;
            }
        }
        return index;
    }

    /**
     * Evaluate the solution: by creating a copy of the predicate being optimized
     * and substituting the required functions.
     * 
     * @param states   to work
     * @param solution to evaluete
     */
    private void evaluate(List<State> states, MembershipFunctionChromosome solution) {
        Operator operator = (Operator) predicate.copy();
        ArrayList<State> _states = OperatorUtil.getNodesByClass(operator, State.class);
        for (State state : states) {
            State toUpd = _states.get(_states.indexOf(state));
            toUpd.setMembershipFunction(solution.getFunction(state.getUuid()));
        }
        new EvaluationAlgorithm(operator, logic, table).execute();
        solution.setFitness(operator.getFitness());
    }

    /**
     * Generate boundaries by reference data
     * 
     * @param states to work
     * @see IMembershipFunctionGenerator#generateBoundaries(Double...)
     */
    private void generateBoundaries(List<State> states) {
        this.referenceValue = new HashMap<>();
        for (State state : states) {
            Class<?> clazz = state.getMembershipFunction() == null ? FPG.class
                    : state.getMembershipFunction().getClass();
            NumericColumn<?> column = table.numberColumn(state.getColName());
            this.referenceValue.put(state.getUuid(),
                    this.generatorOperator.get(clazz).generateBoundaries(column.min(), column.mean(), column.max()));
        }
    }

    /**
     * Generate a new solution: this method delegates the creation of alleles to the
     * classes registered in generatorOperator.
     * 
     * @param states to which a new function (allele) is to be generated
     * @return new solution containing all functions to be optimized from the
     *         predicate
     */
    private MembershipFunctionChromosome generate(List<State> states) {
        MembershipFunctionChromosome chromosome = new MembershipFunctionChromosome(states.size());
        for (int i = 0; i < states.size(); i++) {
            Class<?> clazz = states.get(i).getMembershipFunction() == null ? FPG.class
                    : states.get(i).getMembershipFunction().getClass();
            MembershipFunction[] ref = this.referenceValue.get(states.get(i).getUuid());
            chromosome.setFunction(i, this.generatorOperator.get(clazz).generate(ref[0], ref[1]));
            chromosome.setId(i, states.get(i).getUuid());
        }
        return chromosome;
    }

    /**
     * Register new repair operator
     * 
     * @param clazz          to apply operator
     * @param repairFunction operator
     * @return
     */
    public IMembershipFunctionRepair<? extends MembershipFunction> register(Class<? extends MembershipFunction> clazz,
            IMembershipFunctionRepair<? extends MembershipFunction> repairFunction) {
        return this.repairOperators.put(clazz, repairFunction);
    }

    /**
     * Register new generator operator
     * 
     * @param clazz             to apply operator
     * @param generatorFunction operator
     * @return
     */
    public IMembershipFunctionGenerator<? extends MembershipFunction> register(
            Class<? extends MembershipFunction> clazz,
            IMembershipFunctionGenerator<? extends MembershipFunction> generatorFunction) {
        return this.generatorOperator.put(clazz, generatorFunction);
    }

    /**
     * Register new generator operator
     * 
     * @param clazz             to apply operator
     * @param generatorFunction operator
     * @return
     */
    public IMembershipFunctionCrossover<? extends MembershipFunction> register(
            Class<? extends MembershipFunction> clazz,
            IMembershipFunctionCrossover<? extends MembershipFunction> crossoverFunction) {
        return this.crossoverOperator.put(clazz, crossoverFunction);
    }

    /**
     * Register new generator operator
     * 
     * @param clazz             to apply operator
     * @param generatorFunction operator
     * @return
     */
    public IMembershipFunctionMutation<? extends MembershipFunction> register(
            Class<? extends MembershipFunction> clazz,
            IMembershipFunctionMutation<? extends MembershipFunction> mutationFunction) {
        return this.mutationOperator.put(clazz, mutationFunction);
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    @Override
    public EvaluationResult getResult() {
        return result;
    }

}
