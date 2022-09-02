package com.castellanos94.jfuzzylogic.algorithm;

/**
 * Simple class for definy probability value
 * 
 * @version 0.1.0
 */
public abstract class AProbabilityOperator {
    protected double probabilty;

    public AProbabilityOperator(double probabilty) {
        this.probabilty = probabilty;
    }

    public double getProbabilty() {
        return probabilty;
    }

    public void setProbabilty(double probabilty) {
        this.probabilty = probabilty;
    }
}
