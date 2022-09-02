package com.castellanos94.jfuzzylogic.algorithm.impl;

import com.castellanos94.jfuzzylogic.algorithm.AProbabilityOperator;
import com.castellanos94.jfuzzylogic.algorithm.IMembershipFunctionCrossover;
import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.FPG;

public class FPGCrossover extends AProbabilityOperator implements IMembershipFunctionCrossover<FPG> {

    public FPGCrossover(double probabilty) {
        super(probabilty);
    }

    @Override
    public FPG execute(MembershipFunction a, MembershipFunction b, MembershipFunction lower, MembershipFunction upper) {
        // TODO Auto-generated method stub
        return null;
    }

}
