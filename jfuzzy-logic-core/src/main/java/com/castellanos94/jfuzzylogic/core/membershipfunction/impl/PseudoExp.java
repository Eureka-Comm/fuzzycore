package com.castellanos94.jfuzzylogic.core.membershipfunction.impl;

import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class PseudoExp extends MembershipFunction {
    @EqualsAndHashCode.Include
    protected Double center;
    @EqualsAndHashCode.Include
    protected Double deviation;

    public PseudoExp(Double center, Double deviation) {
        this(center, deviation, false);
    }

    public PseudoExp(Double center, Double deviation, boolean editable) {
        this.center = center;
        this.deviation = deviation;
        this.editable = editable;
    }

    @Override
    public boolean isValid() {
        return !(center == null || deviation == null);
    }

    @Override
    public PseudoExp copy() {
        return new PseudoExp(center, deviation, editable);
    }

    @Override
    public Double[] toArray() {
        return new Double[] { center, deviation };
    }

    @Override
    public String toString() {
        return "PseudoExp [center=" + center + ", deviation=" + deviation + "]";
    }

}
