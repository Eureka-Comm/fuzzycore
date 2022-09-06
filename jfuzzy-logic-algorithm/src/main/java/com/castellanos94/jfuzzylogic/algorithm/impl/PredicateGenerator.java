package com.castellanos94.jfuzzylogic.algorithm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.castellanos94.jfuzzylogic.algorithm.JFuzzyLogicAlgorithmError;
import com.castellanos94.jfuzzylogic.core.OperatorUtil;
import com.castellanos94.jfuzzylogic.core.base.AElement;
import com.castellanos94.jfuzzylogic.core.base.JFuzzyLogicError;
import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.OperatorType;
import com.castellanos94.jfuzzylogic.core.base.impl.And;
import com.castellanos94.jfuzzylogic.core.base.impl.Eqv;
import com.castellanos94.jfuzzylogic.core.base.impl.Generator;
import com.castellanos94.jfuzzylogic.core.base.impl.Imp;
import com.castellanos94.jfuzzylogic.core.base.impl.Not;
import com.castellanos94.jfuzzylogic.core.base.impl.Or;
import com.castellanos94.jfuzzylogic.core.base.impl.State;

/**
 * Default predicate generator from Geneators
 * 
 * @see Generator
 * @version 0.1.0
 */
public class PredicateGenerator {
    public static void main(String[] args) {
        State b = new State("d");
        State a = (State) new State("d").setEditable(true);
        System.out.println(a.hashCode());
        System.out.println(b.hashCode());
        System.out.println(a.equals(b));
        Generator generator = new Generator();
        generator.setLabel("mi generador");
        generator.setDepth(2);
        generator.add(new State("a"), new State("b"), new State("c"));
        generator.add(new State("d"), new State("d"));
        generator.add(OperatorType.IMP, OperatorType.EQV, OperatorType.AND);
        Generator orNot = new Generator();
        orNot.setLabel("disyuntivo");
        orNot.setDepth(2);
        orNot.add(new State("e"), new State("f"), new State("g"));
        orNot.add(OperatorType.OR, OperatorType.NOT);
        generator.add(orNot);
        Random random = new Random(1l);
        random.setSeed(1l);

        int size = 1;
        for (int i = 0; i < size; i++) {
            System.out.println(String.format("%d - %s", (i + 1), generate(random, null, generator, i < size / 2)));
        }

    }

    /**
     * Generate a new operattor from generator.
     * 
     * @param random    generator number
     * @param predicate guide, can be null
     * @param generator generator
     * @param balanced
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
                .toArray(new OperatorType[generator.getOperators().size()]);
        return generateChild(random, predicate, generator, states, generators, operatorTypes, balanced);
    }

    private static AElement generateChild(Random random, Operator predicate, Generator generator, List<State> states,
            List<Generator> generators,
            OperatorType[] operatorTypes, boolean balanced) {
        Operator current;

        if (predicate != null && !balanced && random.nextDouble() < 0.65) {
            List<State> sibilings = OperatorUtil.getNodesByClass(predicate, State.class);
            State state;
            List<State> avalaible = new ArrayList<>();
            if (sibilings.isEmpty()) {
                avalaible = states;
            } else {
                for (State s : states) {
                    if (!sibilings.contains(s)) {
                        avalaible.add(s);
                    }
                }
            }
            if (!avalaible.isEmpty()) {
                state = avalaible.get(random.nextInt(avalaible.size())).copy();
                state.setFrom(generator.getUuid());
                state.setEditable(true);
                return state;
            } else {
                if (sibilings.size() < 2) {
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
        ArrayList<AElement> chidren;
        int index = 0;
        while (index < pending.size()) {
            current = pending.get(index++);
            chidren = OperatorUtil.getNodesByClass(current, AElement.class);
            if (!chidren.contains(current)) {
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
            // System.out.println(String.format("Current %s, Size %d",
            // current.getClass().getSimpleName(), size));
            for (int i = 0; i < size; i++) {
                if (currentLevel >= generator.getDepth() || (!balanced && random.nextDouble() < 0.45)) {
                    State state;
                    List<State> sibilings = OperatorUtil.getNodesByClass(current, State.class);
                    List<State> avalaible = new ArrayList<>();
                    if (sibilings.isEmpty()) {
                        avalaible = states;
                    } else {
                        for (State s : states) {
                            if (!sibilings.contains(s)) {
                                avalaible.add(s);
                            }
                        }
                    }
                    // System.out.println(String.format("\t\tRoot %s, avalaible %d, %s", current,
                    // avalaible.size(), avalaible));
                    if (!avalaible.isEmpty()) {
                        state = avalaible.get(random.nextInt(avalaible.size())).copy();
                        state.setFrom(generator.getUuid());
                        state.setEditable(true);
                        // System.out.println(String.format("\t\t\tTo add %s", state));
                        current.add(state);
                    } else {
                        if (sibilings.size() < 2) {
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
                        current.add(tmp);// checar si es falso agregar otro
                        pending.add(tmp);
                    } else {
                        tmp = (Operator) generate(random, predicate, generators.get(random.nextInt(generators.size())),
                                balanced);
                        tmp.setEditable(true);
                        tmp.setFrom(generator.getUuid());
                        current.add(tmp);
                    }
                    // System.out.println(String.format("\t\t\tTo add %s", tmp));
                }
                // System.out.println(String.format("\t%3d - Current %s", i, current));
            }
        }

        return root;
    }

}
