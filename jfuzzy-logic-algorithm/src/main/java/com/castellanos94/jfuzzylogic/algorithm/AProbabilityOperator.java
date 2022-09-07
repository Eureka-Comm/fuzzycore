package com.castellanos94.jfuzzylogic.algorithm;

/**
 * Simple class for define probability value
 * 
 * @version 0.1.0
 */
public abstract class AProbabilityOperator {
    protected double probability;

    public AProbabilityOperator(double probability) {
        this.probability = probability;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }
}
