package com.castellanos94.jfuzzylogic.core.membershipfunction.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RTrapezoidal extends LTrapezoidal {
    public RTrapezoidal(Double a, Double b) {
        this(a, b, false);
    }

    public RTrapezoidal(Double a, Double b, boolean editable) {
        super(a, b, editable);
    }

    @Override
    public double eval(Number value) {
        Double v = value.doubleValue();
        if (v < a)
            return 1.0;
        if (a <= v && v <= b) {
            double lw = (b == a) ? Double.NaN : b - a;
            return 1 - (v - a) / lw;
        }
        return 0.0;
    }

    @Override
    public Double[] toArray() {
        return new Double[] { a, b };
    }

    @Override
    public RTrapezoidal copy() {
        return new RTrapezoidal(a, b, editable);
    }

    @Override
    public String toString() {
        return "RTrapezoidal [a=" + a + ", b=" + b + "]";
    }

}
