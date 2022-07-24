package com.castellanos94.jfuzzylogic.core.logic.impl;

import java.util.Collection;

import com.castellanos94.jfuzzylogic.core.logic.ImplicationType;
import com.castellanos94.jfuzzylogic.core.logic.Logic;

/**
 * Logica Compensatoria Basada en la Media Geometrica.
 * 
 * @version 1.0
 */
public class GMBC extends Logic {
    public GMBC(ImplicationType type) {
        super(type);
    }

    /**
     * @see Logic
     */
    public GMBC() {
        super();
    }

    @Override
    public double not(double v1) {
        return 1.0 - v1;
    }

    @Override
    public double and(double v1, double v2) {
        return (Math.pow(v1 * v2, 0.5));
    }

    @Override
    public double and(Collection<Double> values) {
        return Math.pow(values.stream().parallel().reduce(1.0, (a, b) -> a * b), 1.0 / values.size());
    }

    @Override
    public double or(double v1, double v2) {
        return (1.0 - Math.pow((1 - v1) * (1 - v2), 0.5));
    }

    @Override
    public double or(Collection<Double> values) {
        double x = 1.0;
        for (Double value : values) {
            x *= (1 - value);
        }
        return (1.0 - Math.pow(x, 1.0 / values.size()));
    }

    @Override
    public double forAll(Collection<Double> values) {
        double result = 0.0;
        for (Double d : values) {
            if (d == 0) {
                return 0;
            } else {
                result += Math.log(d);
            }
        }
        if (result == 0)
            return 0;
        return Math.pow(Math.E, ((1.0 / values.size()) * result));
    }

    @Override
    public double exist(Collection<Double> values) {
        double result = 0.0;
        result = values.stream().filter((value) -> (value != 0)).map((value) -> (Math.log(1 - value))).reduce(result,
                (accumulator, _item) -> accumulator + _item);
        return (1 - Math.pow(Math.E, ((1.0 / values.size()) * result)));
    }
}
