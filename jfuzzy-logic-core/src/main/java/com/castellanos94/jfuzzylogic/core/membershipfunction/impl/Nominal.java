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
public class Nominal extends MembershipFunction {
    @EqualsAndHashCode.Include
    protected String key;
    @EqualsAndHashCode.Include
    protected Double value;
    @EqualsAndHashCode.Include
    protected Double notFoundValue = 0.0;

    public Nominal(String key, Double value) {
        this(key, value, false);
    }

    public Nominal(String key, Double value, boolean editable) {
        if (value > 1.0 || value < 0.0) {
            throw new IllegalArgumentException("Value must be in [0,1]");
        }
        this.key = key;
        this.value = value;
        this.editable = editable;
    }

    @Override
    public double eval(String value) {
        if (value.equalsIgnoreCase(key)) {
            return this.value;
        }
        return notFoundValue;
    }

    @Override
    public boolean isValid() {
        return key != null && value != null;
    }

    @Override
    public Nominal copy() {
        return new Nominal(key, notFoundValue, editable);
    }

    @Override
    public Double[] toArray() {
        return new Double[] { value };
    }

    @Override
    public String toString() {
        return "Nominal [key=" + key + ", value=" + value + ", notFoundValue=" + notFoundValue + "]";
    }

}
