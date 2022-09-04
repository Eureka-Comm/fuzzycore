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
 * TODO : pendiente de corregir
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
            System.out.println(String.format("%d - %s", (i + 1), generate(random, generator, i < size / 2)));
        }

    }

    public static Operator generate(Random random, Generator generator, boolean balanced) {
        if (generator.getMaxChild() == null || generator.getMaxChild() < 2) {
            generator.setMaxChild(generator.getOperators().size() + 2);
            System.out.println("Max child: " + generator.getMaxChild());
        }
        List<State> states = generator.getStates();
        System.out.println(generator.getLabel() + " " + states);
        if (states.size() < 2) {
            throw new JFuzzyLogicError("At least 2 variables are required to generate");
        }
        List<Generator> generators = generator.getGenerators();
        OperatorType[] operatorTypes = generator.getOperators()
                .toArray(new OperatorType[generator.getOperators().size()]);
        return generateChild(random, generator, states, generators, operatorTypes, balanced);
    }

    private static Operator generateChild(Random random, Generator generator, List<State> states,
            List<Generator> generators,
            OperatorType[] operatorTypes, boolean balanced) {
        Operator root = getInstance(random, operatorTypes[random.nextInt(operatorTypes.length)]);
        root.setEditable(true);
        root.setFrom(generator.getUuid());

        int size;
        List<Operator> pending = new ArrayList<>();
        pending.add(root);
        Operator current;
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
                        // System.out.println(String.format("\t\t\tTo add %s", state));
                        current.add(state);
                    } else {
                        if (sibilings.size() < 2) {
                            Not nt = new Not();
                            state = states.get(random.nextInt(states.size())).copy();
                            nt.add(state);
                            current.add(nt);
                        }
                    }
                } else {
                    Operator tmp;
                    if (generators.isEmpty() || random.nextDouble() > 0.45) {
                        tmp = getInstance(random, operatorTypes[random.nextInt(operatorTypes.length)]);
                        tmp.setEditable(true);
                        tmp.setFrom(generator.getUuid());
                        current.add(tmp);
                        pending.add(tmp);
                    } else {
                        tmp = generate(random, generators.get(random.nextInt(generators.size())), balanced);
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

    private static Operator getInstance(Random random, OperatorType type) {
        switch (type) {
            case AND:
                return new And();
            case OR:
                return new Or();
            case IMP:
                return new Imp();
            case EQV:
                return new Eqv();
            case NOT:
                return new Not();
            default:
                throw new JFuzzyLogicAlgorithmError("No class registered for " + type);
        }
    }
}
