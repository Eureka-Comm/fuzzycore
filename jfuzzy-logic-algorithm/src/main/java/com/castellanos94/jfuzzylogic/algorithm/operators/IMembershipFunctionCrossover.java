package com.castellanos94.jfuzzylogic.algorithm.operators;

import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;

/**
 * Membership Function interface for crossover
 * 
 * @version 0.1.0
 */
public interface IMembershipFunctionCrossover<T extends MembershipFunction> {
    /**
     * Crossover strategy
     * 
     * @param a     parent
     * @param b     parent
     * @param lower boundary
     * @param upper boundary
     * @return child
     */
    T execute(MembershipFunction a, MembershipFunction b, MembershipFunction lower, MembershipFunction upper);
}
