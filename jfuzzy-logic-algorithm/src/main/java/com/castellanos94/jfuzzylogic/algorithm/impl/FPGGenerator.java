package com.castellanos94.jfuzzylogic.algorithm.impl;

import java.util.Random;

import com.castellanos94.jfuzzylogic.algorithm.IMembershipFunctionGenerator;
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
    public FPG[] generateBoundaries(Double... referenceValues) {
        FPG lower = new FPG(referenceValues[0], referenceValues[0], 0.0);
        FPG uppper = new FPG(referenceValues[2], referenceValues[2], 1.0);
        return new FPG[] { lower, uppper };
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
