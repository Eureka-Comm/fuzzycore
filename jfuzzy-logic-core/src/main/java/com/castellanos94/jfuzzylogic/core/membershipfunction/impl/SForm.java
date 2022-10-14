package com.castellanos94.jfuzzylogic.core.membershipfunction.impl;

import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * S-shaped membership function MathWorks-based implementation
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SForm extends MembershipFunction {
    @EqualsAndHashCode.Include
    protected Double a;
    @EqualsAndHashCode.Include
    protected Double b;

    public SForm(Double a, Double b) {
        this(a, b, false);
    }

    public SForm(Double a, Double b, boolean editable) {
        this.a = a;
        this.b = b;
        this.editable = editable;
    }

    @Override
    public boolean isValid() {
        return (a != null && b != null) && a < b;
    }

    @Override
    public double eval(Number value) {
        Double v = value.doubleValue();
        if (v <= a)
            return 0;
        if (a <= v && v <= (a + b) / 2.0)
            return 2 * Math.pow((v - a) / (b - a), 2);
        if ((a + b) / 2.0 <= v && v <= b)
            return (1 - 2 * Math.pow((v - b) / (b - a), 2));

        return 1;
    }

    @Override
    public SForm copy() {
        return new SForm(a, b, editable);
    }

    @Override
    public Double[] toArray() {
        return new Double[] { a, b };
    }

    @Override
    public String toString() {
        return "SForm [a=" + a + ", b=" + b + "]";
    }

}
