package com.castellanos94.jfuzzylogic.algorithm.operators;

import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;

/**
 * Membership Function interface for mutation
 * 
 * @version 0.1.0
 */
public interface IMembershipFunctionMutation<T extends MembershipFunction> {
    T execute(MembershipFunction function);
}
