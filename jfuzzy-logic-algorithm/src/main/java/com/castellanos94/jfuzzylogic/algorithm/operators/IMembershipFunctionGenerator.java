package com.castellanos94.jfuzzylogic.algorithm.operators;

import java.util.HashMap;

import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;

/**
 * Membership Function Generator Interface: this interface is in charge of
 * generating limits and new functions randomly.
 * 
 * @version 0.1.0
 */
public interface IMembershipFunctionGenerator<T extends MembershipFunction> {
    /**
     * Generate boundaries
     * 
     * @param function        In case of non-null parameters, reference function
     * @param referenceValues ref values {e.g. min, avg, max}
     * @return [lower, upper] boundaries function
     */
    T[] generateBoundaries(MembershipFunction function, Double... referenceValues);

    /**
     * Generate boundaries
     * 
     * @param function In case of non-null parameters, reference function
     * @param map      ref values map
     * @return [lower, upper] boundaries function
     */
    default T[] generateBoundaries(MembershipFunction function, HashMap<String, Object> map) {
        throw new UnsupportedOperationException("Not implemented yet for " + this.getClass().getSimpleName());
    }

    /**
     * Generates a new membership function using the reference boundaries
     * 
     * @param lower function
     * @param upper function
     * @return new function
     */
    T generate(MembershipFunction lower, MembershipFunction upper);
}
