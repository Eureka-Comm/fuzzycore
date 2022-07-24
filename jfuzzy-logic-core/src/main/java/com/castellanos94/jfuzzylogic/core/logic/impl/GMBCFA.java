package com.castellanos94.jfuzzylogic.core.logic.impl;

import java.util.Collection;

import com.castellanos94.jfuzzylogic.core.logic.ImplicationType;

/**
 * Custom GMBC with exponent
 * 
 * @see GMBC
 * @version 0.5.0
 */
public class GMBCFA extends GMBC {
    protected int exponent;

    public GMBCFA(int coefficient, ImplicationType implicationType) {
        super(implicationType);
        this.exponent = coefficient;
    }

    public GMBCFA() {
        this(3, ImplicationType.Zadeh);
    }

    @Override
    public double forAll(Collection<Double> values) {
        double pe = 0.0;
        for (double v : values) {
            if (v != 0) {
                pe += Math.log(v);
            } else {
                return 0;
            }
        }
        pe /= values.size();
        double r = 0;
        for (double v : values) {
            r += (v - pe) * (v - pe);
        }
        r = Math.sqrt(r / values.size());
        return Math.exp(pe - exponent * r);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " " + type + " " + exponent;
    }
}
