package com.castellanos94.jfuzzylogic.algorithm;

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
    public T[] generateBoundaries(MembershipFunction function, Double... referenceValues);

    /**
     * Generates a new membership function using the reference boundaries
     * 
     * @param lower function
     * @param upper function
     * @return new function
     */
    public T generate(MembershipFunction lower, MembershipFunction upper);
}
