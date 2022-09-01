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
public class NSigmoid extends MembershipFunction {
    protected Double center;
    protected Double beta;

    public NSigmoid() {
    }

    public NSigmoid(Double center, Double beta) {
        this.center = center;
        this.beta = beta;
    }

    public NSigmoid(Double center, Double beta, boolean editable) {
        this(center, beta);
        this.editable = editable;
    }

    @Override
    public double eval(Number value) {    
        Double v = value.doubleValue();
        double alpha = (Math.log(0.99) - Math.log(0.01)) / (beta - center);
        return 1.0 - 1.0 / (1 + Math.exp(-alpha * (v - beta)));
    }

    @Override
    public NSigmoid copy() {
        return new NSigmoid(center, beta, editable);
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

