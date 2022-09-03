package com.castellanos94.jfuzzylogic.algorithm.impl;

import java.util.List;
import java.util.Random;

import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.impl.Generator;
import com.castellanos94.jfuzzylogic.core.base.impl.State;

public class PredicateGenerator {
    public static Operator generate(Random random,Generator generator, boolean balanced) {
        if(generator.getMaxChild() ==null || generator.getMaxChild() < 2){
            generator.setMaxChild(generator.getOperators().size() + 2);
        }
        List<State> states = generator.getStates();
        List<Ge
        return null;
    }
}
