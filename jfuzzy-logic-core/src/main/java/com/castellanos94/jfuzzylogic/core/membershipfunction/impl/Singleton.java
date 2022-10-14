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
public class Singleton extends MembershipFunction {
    @EqualsAndHashCode.Include
    public Double a;

    public Singleton(Double a) {
        this(a, false);
    }

    public Singleton(Double a, boolean editable) {
        this.a = a;
        this.editable = editable;
    }

    @Override
    public boolean isValid() {
        if (a == null)
            return false;
        return !(a > 1.0 || a < 0.0);
    }

    @Override
    public double eval(Number value) {
        return (a == value.doubleValue()) ? 1.0 : 0.0;
    }

    @Override
    public Singleton copy() {
        return new Singleton(a, editable);
    }

    @Override
    public Double[] toArray() {
        return new Double[] { a };
    }

    @Override
    public String toString() {
        return "Singleton [a=" + a + "]";
    }

}
