package com.castellanos94.jfuzzylogic.algorithm.operators.impl;

import java.util.Random;

import com.castellanos94.jfuzzylogic.algorithm.impl.Utils;
import com.castellanos94.jfuzzylogic.algorithm.operators.IMembershipFunctionGenerator;
import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.FPG;

/**
 * Default generator for FPG functions
 * 
 * @see FPG
 * @see IMembershipFunctionGenerator
 */
public class FPGGenerator implements IMembershipFunctionGenerator<FPG> {
    protected Random random;

    public FPGGenerator() {
        this(new Random());
    }

    public FPGGenerator(Random random) {
        this.random = random;
    }

    @Override
    public FPG[] generateBoundaries(MembershipFunction function, Double... referenceValues) {
        FPG lower = new FPG(), upper = new FPG();
        lower.setM(0.0);
        upper.setM(1.0);
        if (function != null) {
            FPG r = (FPG) function;
            if (r.getBeta() != null) {
                lower.setBeta(r.getBeta());
                upper.setBeta(r.getBeta());
            }
            if (r.getGamma() != null) {
                lower.setGamma(r.getGamma());
                upper.setGamma(r.getGamma());
            }
            if (r.getM() != null) {
                lower.setM(r.getM());
                upper.setM(r.getM());
            }
        }
        if (lower.getBeta() == null) {
            lower.setBeta(referenceValues[0]);
            upper.setBeta(referenceValues[2]);
        }
        if (lower.getGamma() == null) {
            lower.setGamma(referenceValues[0]);
            upper.setGamma(referenceValues[2]);
        }
        if (lower.getM() == null) {
            lower.setM(0.0);
            upper.setM(1.0);
        }
        return new FPG[] { lower, upper };
    }

    @Override
    public FPG generate(MembershipFunction lower, MembershipFunction upper) {
        FPG fpg = new FPG();
        FPG l = (FPG) lower;
        FPG u = (FPG) upper;
        double beta = Utils.randomNumber(random, l.getBeta(), u.getGamma());
        double gamma = Utils.randomNumber(random, beta, u.getGamma());
        while (Utils.equals(beta, gamma)) {
            beta = Utils.randomNumber(random, l.getBeta(), u.getGamma());
            gamma = Utils.randomNumber(random, beta, u.getGamma());
        }
        fpg.setBeta(beta);
        fpg.setGamma(gamma);
        fpg.setEditable(true);
        fpg.setM(Utils.randomNumber(random));
        return fpg;
    }

}
