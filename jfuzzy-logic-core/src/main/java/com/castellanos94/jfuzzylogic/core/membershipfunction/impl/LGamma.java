package com.castellanos94.jfuzzylogic.core.membershipfunction.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class LGamma extends Gamma {
    public LGamma() {
    }

    public LGamma(Double a, Double b) {
        this(a, b, false);
    }

    public LGamma(Double a, Double b, boolean editable) {
        super(a, b, editable);
    }

    @Override
    public boolean isValid() {
        return !(a == null || b == null);
    }

    @Override
    public double eval(Number value) {
        Double v = value.doubleValue();
        if (v <= a)
            return 0.0;
        return (b * Math.pow(v - a, 2) / (1 + b * Math.pow(v - a, 2)));
    }

    @Override
    public String toString() {
        return "LGamma [a=" + a + ", b=" + b + "]";
    }

    @Override
    public LGamma copy() {
        return new LGamma(a, b, editable);
    }
}
