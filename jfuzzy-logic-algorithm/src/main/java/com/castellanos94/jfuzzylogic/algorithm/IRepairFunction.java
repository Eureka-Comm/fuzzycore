package com.castellanos94.jfuzzylogic.algorithm;

import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;

/**
 * Repair Membership function interface
 */
public interface IRepairFunction<T extends MembershipFunction> {
    void execute(T function, T lower, T upper);
}
