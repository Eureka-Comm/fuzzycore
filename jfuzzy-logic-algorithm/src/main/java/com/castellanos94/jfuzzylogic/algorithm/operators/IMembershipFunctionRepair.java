package com.castellanos94.jfuzzylogic.algorithm.operators;

import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;

/**
 * Repair Membership function interface
 * 
 * @version 0.1.0
 */
public interface IMembershipFunctionRepair<T extends MembershipFunction> {
    /**
     * Performance reparation
     * 
     * @param f function to repair
     * @param l lower boundary
     * @param u upper boundary
     * @return
     */
    T execute(MembershipFunction f, MembershipFunction l, MembershipFunction u);
}
