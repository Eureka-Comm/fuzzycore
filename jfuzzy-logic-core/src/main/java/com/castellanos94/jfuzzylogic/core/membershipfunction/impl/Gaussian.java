package com.castellanos94.jfuzzylogic.core.membershipfunction.impl;

import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * The class {@code GAUSSIAN} is Generalized Gaussian function fuzzy
 * membership generator.
 * 
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class Gaussian extends MembershipFunction {
    @EqualsAndHashCode.Include
    protected Double center;
    @EqualsAndHashCode.Include
    protected Double deviation;

    public Gaussian() {
    }

    public Gaussian(Double center, Double deviation) {
        this(center, deviation, false);
    }

    public Gaussian(Double center, Double deviation, boolean b) {
        this.center = center;
        this.deviation = deviation;
        this.editable = b;
    }

    @Override
    public double eval(Number value) {
        Double v = value.doubleValue();
        return Math.exp(-Math.pow(v - center, 2) / (2 * Math.pow(deviation, 2)));
    }

    @Override
    public boolean isValid() {
        return (center != null && deviation != null);
    }

    @Override
    public Gaussian copy() {
        return new Gaussian(center, deviation, editable);
    }

    @Override
    public Double[] toArray() {
        return new Double[] { center, deviation };
    }

    @Override
    public String toString() {
        return "Gaussian [center=" + center + ", deviation=" + deviation + "]";
    }
}
