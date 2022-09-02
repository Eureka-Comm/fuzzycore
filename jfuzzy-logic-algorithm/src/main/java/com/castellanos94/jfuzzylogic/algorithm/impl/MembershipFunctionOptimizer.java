package com.castellanos94.jfuzzylogic.algorithm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import com.castellanos94.jfuzzylogic.algorithm.Algorithm;
import com.castellanos94.jfuzzylogic.algorithm.IMembershipFunctionGenerator;
import com.castellanos94.jfuzzylogic.algorithm.IMembershipFunctionRepair;
import com.castellanos94.jfuzzylogic.core.OperatorUtil;
import com.castellanos94.jfuzzylogic.core.base.JFuzzyLogicError;
import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.Result;
import com.castellanos94.jfuzzylogic.core.base.impl.State;
import com.castellanos94.jfuzzylogic.core.logic.Logic;
import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.FPG;

import tech.tablesaw.api.Table;

/**
 * Membership function optimizer
 * 
 * @version 0.5.0
 * @see FPGRepair FPG repair operator default
 * @see FPGGenerator FPG generator operator default
 */
public class MembershipFunctionOptimizer extends Algorithm {
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
    protected EvaluationAlgorithm evaluator;
    protected Random random;

    public MembershipFunctionOptimizer(Operator predicate, Table table) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(table);
        this.predicate = predicate;
        this.table = table;
        this.repairOperators = new HashMap<>();
        this.register(FPG.class, new FPGRepair());
        this.generatorOperator = new HashMap<>();
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
        List<Chromosome> population = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            Chromosome sol = generate(states);
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

            List<Chromosome> offspring = new ArrayList<>(offspringSize);
            // Crossover
            for (int i = 0; i < offspringSize; i++) {

            }

        }
        this.endTime = System.currentTimeMillis();
    }

    /**
     * Returns the index of the best solution in the set.
     * 
     * @param set to get the best index solution
     * @return
     */
    private int getMaxValueIndex(List<Chromosome> set) {
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
    private void evaluate(List<State> states, Chromosome solution) {
        Operator operator = (Operator) predicate.copy();
        ArrayList<State> _states = OperatorUtil.getNodesByClass(operator, State.class);
        for (State state : states) {
            State toUpd = _states.get(_states.indexOf(state));
            toUpd.setMembershipFunction(solution.getFunction(state.getUuid()));
        }
        new EvaluationAlgorithm(operator, logic, table).execute();
        solution.setFitness(operator.getFitness());
    }

    private void generateBoundaries(List<State> states) {
        // TODO: IMPLEMENTAR UN PATRON DE DISENO PARA LA GENERACION DE LIMITES
        for (State state : states) {
            Class<?> clazz = state.getMembershipFunction() == null ? FPG.class
                    : state.getMembershipFunction().getClass();
            double min = 1, avg = 1, max = 1;
            this.referenceValue.put(state.getUuid(),
                    this.generatorOperator.get(clazz).generateBoundaries(min, avg, max));
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
    private Chromosome generate(List<State> states) {
        Chromosome chromosome = new Chromosome(states.size());
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

    @Override
    public Result getResult() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Auxiliary class that represents a solution in the algorithm. Each allele
     * belonging to the chromosome represents a membership function to be
     * discovered/optimized.
     */
    public static class Chromosome implements Comparable<Chromosome> {
        protected Double fitness;
        protected String[] id;
        protected MembershipFunction[] functions;

        public Chromosome(int size) {
            this.id = new String[size];
            this.functions = new MembershipFunction[size];
        }

        public MembershipFunction getFunction(String uuid) {
            if (uuid == null)
                return null;
            for (int i = 0; i < functions.length; i++) {
                if (id[i].equals(uuid)) {
                    return functions[i];
                }
            }
            return null;
        }

        public MembershipFunction getFunction(int index) {
            return this.functions[index];
        }

        public void setFunction(int index, MembershipFunction value) {
            this.functions[index] = value;
        }

        public String getId(int index) {
            return this.id[index];
        }

        public void setId(int index, String value) {
            this.id[index] = value;
        }

        public Double getFitness() {
            return fitness;
        }

        public void setFitness(Double fitness) {
            this.fitness = fitness;
        }

        @Override
        public int compareTo(Chromosome o) {
            return Double.compare(this.fitness, o.fitness);
        }
    }

}
