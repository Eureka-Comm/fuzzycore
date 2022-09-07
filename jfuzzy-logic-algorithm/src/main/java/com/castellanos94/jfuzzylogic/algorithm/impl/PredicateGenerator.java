package com.castellanos94.jfuzzylogic.algorithm.impl;

import com.castellanos94.jfuzzylogic.algorithm.JFuzzyLogicAlgorithmError;
import com.castellanos94.jfuzzylogic.core.OperatorUtil;
import com.castellanos94.jfuzzylogic.core.base.AElement;
import com.castellanos94.jfuzzylogic.core.base.JFuzzyLogicError;
import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.OperatorType;
import com.castellanos94.jfuzzylogic.core.base.impl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Default predicate generator from Generators
 * 
 * @see Generator
 * @version 0.1.0
 */
public class PredicateGenerator {

    /**
     * Generate a new operator from generator.
     * 
     * @param random    generator number
     * @param predicate guide, can be null
     * @param generator generator
     * @param balanced full or growth
     * @return if predicate is null, can be return a state, otherwise, operator
     */
    public static AElement generate(Random random, Operator predicate, Generator generator, boolean balanced) {
        if (generator.getMaxChild() == null || generator.getMaxChild() < 2) {
            generator.setMaxChild(generator.getOperators().size() + 2);
        }
        List<State> states = generator.getStates();
        if (states.size() < 2) {
            throw new JFuzzyLogicError("At least 2 variables are required to generate");
        }
        List<Generator> generators = generator.getGenerators();
        OperatorType[] operatorTypes = generator.getOperators()
                .toArray(new OperatorType[0]);
        return generateChild(random, predicate, generator, states, generators, operatorTypes, balanced);
    }

    private static AElement generateChild(Random random, Operator predicate, Generator generator, List<State> states,
            List<Generator> generators,
            OperatorType[] operatorTypes, boolean balanced) {
        Operator current;

        if (predicate != null && !balanced && random.nextDouble() < 0.65) {
            List<State> siblings = OperatorUtil.getNodesByClass(predicate, State.class);
            State state;
            List<State> available = new ArrayList<>();
            if (siblings.isEmpty()) {
                available = states;
            } else {
                for (State s : states) {
                    if (!siblings.contains(s)) {
                        available.add(s);
                    }
                }
            }
            if (!available.isEmpty()) {
                state = available.get(random.nextInt(available.size())).copy();
                state.setFrom(generator.getUuid());
                state.setEditable(true);
                return state;
            } else {
                if (siblings.size() < 2) {
                    Not nt = new Not();
                    nt.setEditable(true);
                    nt.setFrom(generator.getFrom());
                    state = states.get(random.nextInt(states.size())).copy();
                    state.setEditable(true);
                    state.setFrom(generator.getFrom());
                    nt.add(state);
                    return nt;
                }
            }
        }
        Operator root = OperatorUtil.getInstance(operatorTypes[random.nextInt(operatorTypes.length)]);
        root.setEditable(true);
        root.setFrom(generator.getUuid());

        int size;
        List<Operator> pending = new ArrayList<>();
        pending.add(root);
        int currentLevel = 0;
        ArrayList<AElement> children;
        int index = 0;
        while (index < pending.size()) {
            current = pending.get(index++);
            children = OperatorUtil.getNodesByClass(current, AElement.class);
            if (!children.contains(current)) {
                currentLevel++;
            }
            if (current instanceof And || current instanceof Or) {
                size = Math.max(2, random.nextInt(generator.getMaxChild()));
            } else if (current instanceof Imp || current instanceof Eqv) {
                size = 2;
            } else if (current instanceof Not) {
                size = 1;
            } else {
                throw new JFuzzyLogicAlgorithmError("Not supported generation for " + root.getClass());
            }
            for (int i = 0; i < size; i++) {
                if (currentLevel >= generator.getDepth() || (!balanced && random.nextDouble() < 0.45)) {
                    State state;
                    List<State> siblings = OperatorUtil.getNodesByClass(current, State.class);
                    List<State> available = new ArrayList<>();
                    if (siblings.isEmpty()) {
                        available = states;
                    } else {
                        for (State s : states) {
                            if (!siblings.contains(s)) {
                                available.add(s);
                            }
                        }
                    }
                    if (!available.isEmpty()) {
                        state = available.get(random.nextInt(available.size())).copy();
                        state.setFrom(generator.getUuid());
                        state.setEditable(true);
                        // System.out.println(String.format("\t\t\tTo add %s", state));
                        current.add(state);
                    } else {
                        if (siblings.size() < 2) {
                            Not nt = new Not();
                            nt.setEditable(true);
                            nt.setFrom(generator.getFrom());
                            state = states.get(random.nextInt(states.size())).copy();
                            state.setEditable(true);
                            state.setFrom(generator.getFrom());
                            nt.add(state);
                            current.add(nt);
                        }
                    }
                } else {
                    Operator tmp;
                    if (generators.isEmpty() || random.nextDouble() > 0.45) {
                        tmp = OperatorUtil.getInstance(operatorTypes[random.nextInt(operatorTypes.length)]);
                        tmp.setEditable(true);
                        tmp.setFrom(generator.getUuid());
                        current.add(tmp);
                        pending.add(tmp);
                    } else {
                        tmp = (Operator) generate(random, predicate, generators.get(random.nextInt(generators.size())),
                                balanced);
                        tmp.setEditable(true);
                        tmp.setFrom(generator.getUuid());
                        current.add(tmp);
                    }
                }
            }
        }

        return root;
    }

}
