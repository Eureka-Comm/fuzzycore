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
public class LTrapezoidal extends MembershipFunction {
    @EqualsAndHashCode.Include
    protected Double a;
    @EqualsAndHashCode.Include
    protected Double b;

    public LTrapezoidal(Double a, Double b) {
        this(a, b, false);
    }

    public LTrapezoidal(Double a, Double b, boolean editable) {
        this.a = a;
        this.b = b;
        this.editable = editable;
    }

    @Override
    public boolean isValid() {
        return (a != null && b != null) && a <= b;
    }

    @Override
    public LTrapezoidal copy() {
        return new LTrapezoidal(a, b, editable);
    }

    @Override
    public String toString() {
        return "LTrapezoidal [a=" + a + ", b=" + b + "]";
    }
}
