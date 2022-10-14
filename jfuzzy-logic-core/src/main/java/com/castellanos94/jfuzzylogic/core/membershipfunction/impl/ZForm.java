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
public class ZForm extends MembershipFunction {
    @EqualsAndHashCode.Include
    protected Double a;
    @EqualsAndHashCode.Include
    protected Double b;

    public ZForm(Double a, Double b) {
        this(a, b, false);
    }

    public ZForm(Double a, Double b, boolean editable) {
        this.a = a;
        this.b = b;
        this.editable = editable;
    }

    @Override
    public boolean isValid() {
        return !(a == null || b == null);
    }

    @Override
    public double eval(Number value) {
        Double v = value.doubleValue();
        if (v <= a)
            return 1.0;
        if (a <= v && v <= (a + b) / 2.0)
            return (1 - 2 * Math.pow((v - a) / (b - a), 2));
        if ((a + b) / 2.0 <= v && v <= b)
            return 2 * Math.pow((v - b) / (b - a), 2);
        return 0.0;
    }

    @Override
    public ZForm copy() {
        return new ZForm(a, b, editable);
    }

    @Override
    public Double[] toArray() {
        return new Double[] { a, b };
    }

    @Override
    public String toString() {
        return "ZForm [a=" + a + ", b=" + b + "]";
    }

}
