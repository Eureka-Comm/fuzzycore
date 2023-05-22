package com.castellanos94.jfuzzylogic.core.membershipfunction.impl;

import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class FPG extends MembershipFunction {
    @EqualsAndHashCode.Include
    protected Double beta;
    @EqualsAndHashCode.Include
    protected Double gamma;
    @EqualsAndHashCode.Include
    protected Double m;

    public FPG() {
    }

    public FPG(Double beta, Double gamma, Double m) {
        this.beta = beta;
        this.gamma = gamma;
        this.m = m;
    }

    protected FPG(Double beta, Double gamma, Double m, boolean editable) {
        this(beta, gamma, m);
        this.editable = editable;
    }

    @Override
    public FPG copy() {
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
         if (beta == null || gamma == null || m == null) {
            return false;
        }
        if (m == 0.0) {
            return gamma < beta;
        } else if (m == 1.0) {
            return gamma > beta;
        }
        return m >= 0 && m <= 1.0;
    }

    @Override
    public Double[] toArray() {
        return new Double[] { beta, gamma, m };
    }

    @Override
    public String toString() {
        return "FPG [beta=" + beta + ", gamma=" + gamma + ", m=" + m + ", editable=" + editable + "]";
    }

}
