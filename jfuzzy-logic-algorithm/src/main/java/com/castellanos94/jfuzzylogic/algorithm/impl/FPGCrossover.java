package com.castellanos94.jfuzzylogic.algorithm.impl;

import java.util.Random;

import com.castellanos94.jfuzzylogic.algorithm.AProbabilityOperator;
import com.castellanos94.jfuzzylogic.algorithm.IMembershipFunctionCrossover;
import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.FPG;

/**
 * Default crossover for FPG functions
 * 
 * @see FPG
 * @see IMembershipFunctionCrossover
 * @see AProbabilityOperator
 */
public class FPGCrossover extends AProbabilityOperator implements IMembershipFunctionCrossover<FPG> {
    private Random random;

    public FPGCrossover() {
        this(0.95);
    }

    public FPGCrossover(double probabilty) {
        this(probabilty, new Random());
    }

    public FPGCrossover(double probabilty, Random random) {
        super(probabilty);
        this.random = random;
    }

    @Override
    public FPG execute(MembershipFunction a, MembershipFunction b, MembershipFunction lower, MembershipFunction upper) {
        FPG fpg = new FPG();
        FPG c = (FPG) a;
        FPG d = (FPG) b;

        fpg.setBeta(random.nextDouble() <= probabilty ? c.getBeta() : d.getBeta());
        fpg.setGamma(random.nextDouble() <= probabilty ? c.getGamma() : d.getGamma());
        fpg.setM(random.nextDouble() <= probabilty ? c.getM() : d.getM());
        fpg.setEditable(true);
        double tmp;
        if (fpg.getBeta().compareTo(fpg.getGamma()) > 0) {
            tmp = fpg.getBeta();
            fpg.setBeta(fpg.getGamma());
            fpg.setGamma(tmp);
        }
        return fpg;
    }

}
