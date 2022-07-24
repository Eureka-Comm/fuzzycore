package com.castellanos94.jfuzzylogic.core.membershipfunction.impl;

import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class Sigmoid extends MembershipFunction {
    protected Double center;
    protected Double beta;

    public Sigmoid() {
    }

    public Sigmoid(Double center, Double beta) {
        this.center = center;
        this.beta = beta;
    }

    public Sigmoid(Double center, Double beta, boolean editable) {
        this(center, beta);
        this.editable = editable;
    }

    @Override
    public double eval(Number value) {
        Double v = value.doubleValue();
        return (1 / (1 + (Math.exp(-((Math.log(0.99) - Math.log(0.01)) / (center - beta)) * (v - center)))));
    }

    @Override
    public MembershipFunction copy() {
        return new Sigmoid(center, beta, editable);
    }

    @Override
    public Double[] toArray() {
        return new Double[] { center, beta };
    }

    @Override
    public boolean isValid() {
        return (center != null && beta != null) && center > beta;
    }
}
