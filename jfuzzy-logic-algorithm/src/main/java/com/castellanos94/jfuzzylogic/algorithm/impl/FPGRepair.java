package com.castellanos94.jfuzzylogic.algorithm.impl;

import com.castellanos94.jfuzzylogic.algorithm.IRepairFunction;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.FPG;

public class FPGRepair implements IRepairFunction<FPG> {
    @Override
    public void execute(FPG function, FPG lower, FPG upper) {
        int isSmallerThan = Double.compare(function.getBeta(), function.getGamma());
        if (isSmallerThan >= 0) {
            // generar uno nuevo entre el minimo y gama
        } else if (Double.compare(lower.getBeta(), function.getBeta()) > 0) {
            // generar beta y gamma
        }
        if (Double.compare(lower.getGamma(), function.getGamma()) > 0) {
            // generar uno nuevo entre el rango beta y el maximo
        }
        if (Double.compare(function.getBeta(), upper.getBeta()) > 0) {
            // generar uno nuevo entre el rango lower y gamma
        }
        if (Double.compare(function.getGamma(), upper.getGamma()) > 0) {
            // generar uno nuevo entre el rango beta y maximo
        }
        if (Double.compare(function.getM(), 1.0) > 0 || Double.compare(function.getM(), 0.0) < 0) {
            function.setM(Math.random());
        }
    }

}
