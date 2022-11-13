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
    protected double c;
    protected double ci;

    public GMBCFA(int coefficient, ImplicationType implicationType) {
        super(implicationType);
        this.exponent = coefficient;
    }

    public GMBCFA() {
        this(3, ImplicationType.Zadeh);
    }

    @Override
    public double forAll(Collection<Double> values) {
        // u = sum(ln x_i)/n
        // v = sqrt(1/n*sum(u-x_i)^2)
        // c = exp(u)
        // ci = exp(u - a (v/ sqrt(n)))
        // fo = sqrt(c * ci)
        double u = 0, v = 0;
        for (Double x : values) {
            if (x != 0) {
                u = +Math.log(x);
            } else {
                return 0;
            }
        }
        u = u / values.size();
        for (Double x : values) {
            v += Math.pow(u - x, 2);
        }
        v = v / values.size();
        c = Math.exp(u);
        ci = Math.exp(u - exponent * (v / Math.sqrt(values.size())));
        return Math.sqrt(c * ci);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " " + type + " " + exponent;
    }

    public double getC() {
        return c;
    }

    public double getCi() {
        return ci;
    }

    @Override
    public GMBCFA copy() {
        return new GMBCFA(exponent, type);
    }
}
