package com.castellanos94.jfuzzylogic.core.membershipfunction.impl;

import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class Gamma extends MembershipFunction {
    @EqualsAndHashCode.Include
    protected Double a;
    @EqualsAndHashCode.Include
    protected Double b;

    public Gamma() {
    }

    public Gamma(Double a, Double b, boolean editable) {
        this.a = a;
        this.b = b;
        this.editable = editable;
    }

    public Gamma(Double a, Double b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public double eval(Number value) {
        Double v = value.doubleValue();
        if (v <= a)
            return 0.0;
        return (1.0 - Math.exp(-b * Math.pow(v - a, 2)));
    }

    @Override
    public boolean isValid() {
        return (a != null && b != null);
    }

    @Override
    public Gamma copy() {
        return new Gamma(a, b, editable);
    }

    @Override
    public Double[] toArray() {
        return new Double[] { a, b };
    }

    @Override
    public String toString() {
        return "Gamma [a=" + a + ", b=" + b + "]";
    }

}
