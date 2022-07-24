package com.castellanos94.jfuzzylogic.core;

import java.util.Collection;

/**
 * Abstract class representing an logic
 * 
 * @version 1.0.0
 * @see ImplicationType
 */
public abstract class Logic {
    protected ImplicationType type;

    /**
     * Default implication <code>ImplicationType.Zade</code>
     */
    public Logic() {
        this(ImplicationType.Zadeh);
    }

    public Logic(ImplicationType type) {
        this.type = type;
    }

    public abstract double and(double a, double b);

    public abstract double and(Collection<Double> values);

    public abstract double or(double a, double b);

    public abstract double or(Collection<Double> values);

    public double imp(double a, double b) {
        switch (type) {
            case Natural:
                return or(not(a), b);
            case Zadeh:
                return or(not(a), and(a, b));
            case Reichenbach:
                return 1.0 - a + a * b;
            case KlirYuan:
                return 1.0 - a + (Math.pow(a, 2) * b);
            case Yager:
                return Math.pow(a, b);
            default:
                throw new IllegalArgumentException("Invalid implication type: " + type);
        }
    }

    public double eqv(double a, double b) {
        return this.and(this.imp(a, b), this.imp(a, b));
    }

    public abstract double not(double a);

    public abstract double forAll(Collection<Double> values);

    public abstract double exits(Collection<Double> values);

    public ImplicationType getType() {
        return type;
    }

    public void setType(ImplicationType type) {
        this.type = type;
    }
}
