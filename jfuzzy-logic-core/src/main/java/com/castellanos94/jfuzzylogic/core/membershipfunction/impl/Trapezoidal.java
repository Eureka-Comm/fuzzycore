package com.castellanos94.jfuzzylogic.core.membershipfunction.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Trapezoidal extends LTrapezoidal {
    @EqualsAndHashCode.Include
    protected Double c;
    @EqualsAndHashCode.Include
    protected Double d;

    public Trapezoidal(Double a, Double b, Double c, Double d) {
        this(a, b, c, d, false);
    }

    public Trapezoidal(Double a, Double b, Double c, Double d, boolean editable) {
        super(a, b, editable);
        this.c = c;
        this.d = d;
    }

    @Override
    public boolean isValid() {
        return !(a == null || b == null || c == null || d == null) && (a <= b && b <= c && c <= d);
    }

    @Override
    public Trapezoidal copy() {
        return new Trapezoidal(a, b, c, d, editable);
    }

    @Override
    public String toString() {
        return "Trapezoidal [a=" + a + ", b=" + b + ", c=" + c + ", d=" + d + "]";
    }

}
