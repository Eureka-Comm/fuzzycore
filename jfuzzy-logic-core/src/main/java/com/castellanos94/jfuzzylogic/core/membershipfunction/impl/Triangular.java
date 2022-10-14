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
public class Triangular extends MembershipFunction {

    @EqualsAndHashCode.Include
    protected Double a;
    @EqualsAndHashCode.Include
    protected Double b;
    @EqualsAndHashCode.Include
    protected Double c;

    public Triangular(Double a, Double b, Double c) {
        this(a, b, c, false);
    }

    public Triangular(Double a, Double b, Double c, boolean editable) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.editable = editable;
    }

    @Override
    public double eval(Number value) {
        Double v = value.doubleValue();
        double la = b - a;
        double lb = c - b;
        if (v <= a)
            return 0;
        if (a <= v && v <= b) {
            if (la == 0)
                return Float.NaN;
            return (v - a) / la;
        }
        if (b <= v && v <= c) {
            if (lb == 0)
                return Float.NaN;
            return (c - v) / lb;
        }
        return 0;
    }

    @Override
    public boolean isValid() {
        return (a != null && b != null && c != null) && (a <= b && b <= c);
    }

    @Override
    public Triangular copy() {
        return new Triangular(a, b, c, editable);
    }

    @Override
    public Double[] toArray() {
        return new Double[] { a, b, c };
    }

    @Override
    public String toString() {
        return "Triangular [a=" + a + ", b=" + b + ", c=" + c + "]";
    }

}
