package com.castellanos94.jfuzzylogic.algorithm;

import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.impl.State;
import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Definition of Membership Function Optimizer
 * 
 * @version 0.1.0
 */
public abstract class AMembershipFunctionOptimizer {
    /**
     * Start time in ms
     */
    protected long startTime;
    /**
     * End time in ms
     */
    protected long endTime;
    /**
     * PseudoRandom generator
     */
    protected Random random;
    /**
     * Map with membership function class for optimizer
     */
    protected Map<String, Class<? extends MembershipFunction>> stateIdByClass;
    /**
     * Map with boundaries, expected [lower, upper] functions
     */
    protected Map<String, MembershipFunction[]> boundaries;
    /**
     * Repair operators by class
     */
    protected Map<Class<? extends MembershipFunction>, IMembershipFunctionRepair<? extends MembershipFunction>> repairOperators;
    /**
     * Generator operators by class
     */
    protected Map<Class<? extends MembershipFunction>, IMembershipFunctionGenerator<? extends MembershipFunction>> generatorOperator;
    /**
     * Crossover operators by class
     */
    protected Map<Class<? extends MembershipFunction>, IMembershipFunctionCrossover<? extends MembershipFunction>> crossoverOperator;
    /**
     * Mutation operators by class
     */
    protected Map<Class<? extends MembershipFunction>, IMembershipFunctionMutation<? extends MembershipFunction>> mutationOperator;

    /**
     * Default constructor
     * <b>It is required to initialize stateIdByClass</b>
     * 
     * @param random number
     */
    public AMembershipFunctionOptimizer(Random random) {
        this.random = random;
        this.repairOperators = new HashMap<>();
        this.generatorOperator = new HashMap<>();
        this.crossoverOperator = new HashMap<>();
        this.mutationOperator = new HashMap<>();
    }

    /**
     * Performance membership function optimizer
     * 
     * @param predicate to work
     * @return predicate discovery
     */
    public abstract Operator execute(Operator predicate);

    /**
     * Mutation operator, delete to IMembershipFunctionMutation register
     * 
     * @param chromosome individual
     */
    protected void mutation(MembershipFunctionChromosome chromosome) {
        for (int i = 0; i < chromosome.getSize(); i++) {
            chromosome.setFunction(i, this.mutationOperator.get(this.stateIdByClass.get(chromosome.getId(i)))
                    .execute(chromosome.getFunction(i)));
        }
    }

    /**
     * Validate that the necessary genetic operators exist to perform the
     * optimization of membership functions.
     * <p>
     * If the membership function is null, the default value is set.
     * </p>
     * 
     * @param states       to work
     * @param defaultClass in null case, set this value
     * @throws JFuzzyLogicAlgorithmError when the operator does not exist for
     *                                   current class
     */
    protected void validateOperators(List<State> states, Class<? extends MembershipFunction> defaultClass) {
        for (State state : states) {
            MembershipFunction f = state.getMembershipFunction();
            Class<?> clazz = f == null ? defaultClass : f.getClass();
            if (!this.generatorOperator.containsKey(clazz)) {
                throw new JFuzzyLogicAlgorithmError("No generator operator is registered for " + clazz);
            }
            if (!this.crossoverOperator.containsKey(clazz)) {
                throw new JFuzzyLogicAlgorithmError("No crossover operator is registered for " + clazz);
            }
            if (!this.repairOperators.containsKey(clazz)) {
                throw new JFuzzyLogicAlgorithmError("No repair operator is registered for " + clazz);
            }
            if (!mutationOperator.isEmpty() && !this.mutationOperator.containsKey(clazz)) {
                throw new JFuzzyLogicAlgorithmError("No mutation operator is registered for " + clazz);
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
    protected MembershipFunctionChromosome crossover(MembershipFunctionChromosome a,
            MembershipFunctionChromosome b) {
        MembershipFunctionChromosome chromosome = new MembershipFunctionChromosome(a.getSize());
        for (int i = 0; i < chromosome.getSize(); i++) {
            Class<? extends MembershipFunction> clazz = this.stateIdByClass.get(a.getId(i));
            MembershipFunction[] boundaries = this.boundaries.get(a.getId(i));
            chromosome.setFunction(i, this.crossoverOperator.get(clazz).execute(a.getFunction(i), b.getFunction(i),
                    boundaries[0], boundaries[1]));
            chromosome.setId(i, a.getId(i));
        }
        return chromosome;
    }

    /**
     * Returns the index of the best solution in the set.
     * 
     * @param set to get the best index solution
     * @return index
     */
    protected int getMaxValueIndex(List<MembershipFunctionChromosome> set) {
        int index = 0;
        for (int i = 1; i < set.size(); i++) {
            if (set.get(index).getFitness().compareTo(set.get(i).getFitness()) > 0) {
                index = i;
            }
        }
        return index;
    }

    /**
     * Identifies the types of states to be optimized.
     * <p>
     * If the membership function is null, the default value is set.
     * </p>
     * 
     * @param states       to work
     * @param defaultClass in null case, set this value
     * @return Map {@code key:state.uuid, value : class}
     */
    protected Map<String, Class<? extends MembershipFunction>> identifyClass(List<State> states,
            Class<? extends MembershipFunction> defaultClass) {
        Map<String, Class<? extends MembershipFunction>> map = new HashMap<>();
        for (State state : states) {
            Class<? extends MembershipFunction> clazz = state.getMembershipFunction() == null ? defaultClass
                    : state.getMembershipFunction().getClass();
            map.put(state.getUuid(), clazz);
        }
        return map;
    }

    /**
     * Evaluate the solution: by creating a copy of the predicate being optimized
     * and substituting the required functions.
     * 
     * @param states   to work
     * @param solution to evaluate
     */
    protected abstract void evaluate(List<State> states, MembershipFunctionChromosome solution);

    /**
     * Generate boundaries by reference data
     * 
     * @param states to work
     * @apiNote {@link IMembershipFunctionGenerator#generateBoundaries(MembershipFunction, Double...)}
     */
    public abstract Map<String, MembershipFunction[]>  generateBoundaries(List<State> states);

    /**
     * Generate a new solution: this method delegates the creation of alleles to the
     * classes registered in generatorOperator.
     * 
     * @param states to which a new function (allele) is to be generated
     * @return new solution containing all functions to be optimized from the
     *         predicate
     */
    protected abstract MembershipFunctionChromosome generate(List<State> states);

    /**
     * Register new repair operator
     * 
     * @param clazz          to apply operator
     * @param repairFunction operator
     * @return registered
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
     * @return registered
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
     * @param crossoverFunction operator
     * @return register
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
     * @param mutationFunction operator
     * @return registered
     */
    public IMembershipFunctionMutation<? extends MembershipFunction> register(
            Class<? extends MembershipFunction> clazz,
            IMembershipFunctionMutation<? extends MembershipFunction> mutationFunction) {
        return this.mutationOperator.put(clazz, mutationFunction);
    }

    /**
     * Get computation time
     * 
     * @return time ms
     */
    public long getComputeTime() {
        return this.endTime - this.startTime;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    /**
     * Create a copy of this object
     * 
     * @return copy
     */
    public abstract AMembershipFunctionOptimizer copy();

    public Map<Class<? extends MembershipFunction>, IMembershipFunctionRepair<? extends MembershipFunction>> getRepairOperators() {
        return repairOperators;
    }

    public Map<Class<? extends MembershipFunction>, IMembershipFunctionGenerator<? extends MembershipFunction>> getGeneratorOperator() {
        return generatorOperator;
    }

    public Map<Class<? extends MembershipFunction>, IMembershipFunctionCrossover<? extends MembershipFunction>> getCrossoverOperator() {
        return crossoverOperator;
    }

    public Map<Class<? extends MembershipFunction>, IMembershipFunctionMutation<? extends MembershipFunction>> getMutationOperator() {
        return mutationOperator;
    }

    
}
