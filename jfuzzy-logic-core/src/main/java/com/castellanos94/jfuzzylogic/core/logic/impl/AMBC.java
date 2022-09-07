package com.castellanos94.jfuzzylogic.core.logic.impl;

import java.util.Collection;

import com.castellanos94.jfuzzylogic.core.logic.ImplicationType;
import com.castellanos94.jfuzzylogic.core.logic.Logic;

/**
 * AMBC logic: <a href="https://doi.org/10.1142/S1469026811003070">article</a> -based implementation.
 *
 * @author Castellanos-Alvarez, Alejandro.
 * @version 1.0
 */
public class AMBC extends Logic {
    public AMBC(ImplicationType type) {
        super(type);
    }

    public AMBC() {
        super();
    }

    @Override
    public double not(double v1) {
        return 1 - v1;
    }

    @Override
    public double and(double v1, double v2) {
        return Math.sqrt(Math.min(v1, v2) * (0.5) * (v1 + v2));
    }

    @Override
    public double or(double v1, double v2) {
        return (1.0 - Math.sqrt(Math.min((1.0 - v1), (1.0 - v2)) * (0.5) * ((1.0 - v1) + (1.0 - v2))));
    }

    @Override
    public double forAll(Collection<Double> values) {
        return Math.sqrt(values.stream().min(Double::compare).get() * (1.0 / values.size())
                * (values.stream().mapToDouble(Double::doubleValue).sum()));
    }

    @Override
    public double exist(Collection<Double> values) {
        double min = 1.0;
        double sum = 0.0;
        for (Double valDouble : values) {
            double tmp = 1.0 - valDouble;
            sum += tmp;
            if (min > tmp)
                min = tmp;
        }
        return 1.0 - Math.sqrt((min * (1.0 / values.size()) * sum));
    }

    @Override
    public double or(Collection<Double> values) {
        double min = 1.0;
        double sum = 0.0;
        for (Double valDouble : values) {
            double tmp = 1.0 - valDouble;
            sum += tmp;
            if (min > tmp) {
                min = tmp;
            }
        }
        return (1.0 - Math.sqrt(min * (1.0 / values.size()) * sum));
    }

    @Override
    public double and(Collection<Double> values) {
        return Math.sqrt(values.stream().min(Double::compare).get() * (1.0 / values.size())
                * values.stream().mapToDouble(Double::doubleValue).sum());
    }

}
