package com.castellanos94.jfuzzylogic.algorithm.impl;

import java.util.Random;

import com.castellanos94.jfuzzylogic.algorithm.IMembershipFunctionRepair;
import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.FPG;

/**
 * Default repair for FPG functions
 * 
 * @see FPG
 * @see IMembershipFunctionRepair
 */
public class FPGRepair implements IMembershipFunctionRepair<FPG> {
    protected Random random;

    public FPGRepair() {
        this(new Random());
    }

    public FPGRepair(Random random) {
        this.random = random;
    }

    @Override
    public FPG execute(MembershipFunction f, MembershipFunction l, MembershipFunction u) {
        FPG function = (FPG) f;
        FPG lower = (FPG) l;
        FPG upper = (FPG) u;
        if (Utils.equals(function.getBeta(), function.getGamma()) || function.getBeta() > function.getGamma()) {
            function.setBeta(Utils.randomNumber(random, lower.getBeta(), function.getGamma()));
        }
        if (Double.compare(lower.getBeta(), function.getBeta()) > 0) {
            function.setBeta(Utils.randomNumber(random, lower.getBeta(), function.getGamma()));
        }
        if (Double.compare(lower.getGamma(), function.getGamma()) > 0) {
            function.setGamma(Utils.randomNumber(random, function.getBeta(), upper.getGamma()));
        }
        if (Double.compare(function.getBeta(), upper.getBeta()) > 0) {
            function.setBeta(Utils.randomNumber(random, lower.getBeta(), function.getGamma()));
        }
        if (Double.compare(function.getGamma(), upper.getGamma()) > 0) {
            function.setGamma(Utils.randomNumber(random, function.getBeta(), upper.getGamma()));
        }
        if (Utils.equals(lower.getBeta(), upper.getBeta())) {
            function.setBeta(lower.getBeta());
        }
        if (Utils.equals(lower.getGamma(), upper.getGamma())) {
            function.setGamma(lower.getGamma());
        }
        if (Utils.equals(lower.getM(), upper.getM())) {
            function.setM(lower.getM());
        } else if (Double.compare(function.getM(), 1.0) > 0 || Double.compare(function.getM(), 0.0) < 0) {
            function.setM(Utils.randomNumber(random));
        }
        return function;
    }

}
