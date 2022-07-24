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
public class FPG extends MembershipFunction {
    protected Double beta;
    protected Double gamma;
    protected Double m;

    public FPG() {
    }

    public FPG(Double beta, Double gamma, Double m) {
        this.beta = beta;
        this.gamma = gamma;
        this.m = m;
    }

    public FPG(Double beta, Double gamma, Double m, boolean editable) {
        this(beta, gamma, m);
        this.editable = editable;
    }

    @Override
    public MembershipFunction copy() {
        return new FPG(beta, gamma, m, editable);

    }

    @Override
    public double eval(Number v) {

        double sigm, sigmm, M;
        sigm = Math.pow(new Sigmoid(gamma, beta).eval(v), m);
        sigmm = Math.pow(1.0 - new Sigmoid(gamma, beta).eval(v), 1.0 - m);
        M = Math.pow(m, m) * Math.pow((1 - m), (1 - m));

        return ((sigm * sigmm) / M);
    }

    @Override
    public boolean isValid() {
        return (beta != null && gamma != null && m != null) && (m >= 0 && m <= 1.0) && (gamma > beta);
    }

    @Override
    public Double[] toArray() {
        return new Double[] { beta, gamma, m };
    }

}
