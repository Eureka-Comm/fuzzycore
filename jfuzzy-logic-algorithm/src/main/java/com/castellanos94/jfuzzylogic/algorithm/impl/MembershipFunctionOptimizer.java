package com.castellanos94.jfuzzylogic.algorithm.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.castellanos94.jfuzzylogic.algorithm.Algorithm;
import com.castellanos94.jfuzzylogic.algorithm.IMembershipFunctionGenerator;
import com.castellanos94.jfuzzylogic.algorithm.IRepairFunction;
import com.castellanos94.jfuzzylogic.core.OperatorUtil;
import com.castellanos94.jfuzzylogic.core.base.JFuzzyLogicError;
import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.Result;
import com.castellanos94.jfuzzylogic.core.base.impl.State;
import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.FPG;

import tech.tablesaw.api.Table;

/**
 * Membership function optimizer
 * 
 * @version 0.5.0
 * @see FPGRepair FPG repair operator default
 */
public class MembershipFunctionOptimizer extends Algorithm {
    protected Integer maxIterations;
    protected Integer populationSize;
    protected Double minTruthValue;
    protected Double crossoverRate;
    protected Double mutationRate;
    protected Operator predicate;
    protected Table table;
    protected HashMap<String, MembershipFunction[]> referenceValue;
    protected HashMap<Class<? extends MembershipFunction>, IRepairFunction<? extends MembershipFunction>> repairOperators;
    protected HashMap<Class<? extends MembershipFunction>, IMembershipFunctionGenerator<? extends MembershipFunction>> generatorOperator;

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
        //
        int currentIteration = 1;
        int maxValueIndex = 0;

        while (currentIteration < maxIterations) {

        }
        this.endTime = System.currentTimeMillis();
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
    public IRepairFunction<? extends MembershipFunction> register(Class<? extends MembershipFunction> clazz,
            IRepairFunction<? extends MembershipFunction> repairFunction) {
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

    public static class Chromosome {
        protected String[] id;
        protected MembershipFunction[] functions;

        public Chromosome(int size) {
            this.id = new String[size];
            this.functions = new MembershipFunction[size];
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
    }

}
