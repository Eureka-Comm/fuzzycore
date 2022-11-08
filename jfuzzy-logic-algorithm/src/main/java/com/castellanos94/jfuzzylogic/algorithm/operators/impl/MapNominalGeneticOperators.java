package com.castellanos94.jfuzzylogic.algorithm.operators.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import com.castellanos94.jfuzzylogic.algorithm.impl.Utils;
import com.castellanos94.jfuzzylogic.algorithm.operators.IMembershipFunctionCrossover;
import com.castellanos94.jfuzzylogic.algorithm.operators.IMembershipFunctionGenerator;
import com.castellanos94.jfuzzylogic.algorithm.operators.IMembershipFunctionMutation;
import com.castellanos94.jfuzzylogic.algorithm.operators.IMembershipFunctionRepair;
import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.MapNominal;

public class MapNominalGeneticOperators {
    public static class Generator implements IMembershipFunctionGenerator<MapNominal> {
        protected Random random;

        public Generator(Random random) {
            this.random = random;
        }

        public Generator() {
            this(new Random());
        }

        @Override
        public MapNominal[] generateBoundaries(MembershipFunction function, Double... referenceValues) {
            throw new UnsupportedOperationException("Not supported for " + MapNominal.class.getSimpleName());
        }

        @Override
        public MapNominal[] generateBoundaries(MembershipFunction function, HashMap<String, Object> map) {
            MapNominal[] reference = new MapNominal[2];
            reference[0] = new MapNominal();
            reference[1] = new MapNominal();
            map.forEach((k, v) -> {
                reference[0].add(k, 0);
                reference[1].add(k, 1.0);
            });
            return reference;
        }

        @Override
        public MapNominal generate(MembershipFunction lower, MembershipFunction upper) {
            MapNominal function = new MapNominal();
            MapNominal ref = (MapNominal) lower;
            ref.getValues().forEach((k, v) -> {
                function.add(k, Utils.randomNumber(random));
            });
            return function;
        }

    }

    public static class Crossover implements IMembershipFunctionCrossover<MapNominal> {
        protected Random random;
        protected double probability;

        public Crossover(double probability, Random random) {
            this.random = random;
            this.probability = probability;
        }

        public Crossover() {
            this(0.95, new Random());
        }

        @Override
        public MapNominal execute(MembershipFunction a, MembershipFunction b, MembershipFunction lower,
                MembershipFunction upper) {
            MapNominal nominal = new MapNominal();
            nominal.setEditable(a.isEditable());
            MapNominal c = (MapNominal) a;
            MapNominal d = (MapNominal) b;
            nominal.setNotFoundValue(
                    this.probability < random.nextDouble() ? c.getNotFoundValue() : d.getNotFoundValue());
            Iterator<String> iterator = c.getValues().keySet().iterator();
            String key;
            while (iterator.hasNext()) {
                key = iterator.next();
                nominal.add(key,
                        random.nextDouble() < this.probability ? c.getValues().get(key) : d.getValues().get(key));
            }
            return nominal;
        }
    }

    public static class Mutation implements IMembershipFunctionMutation<MapNominal> {
        protected double probability;
        protected Random random;

        public Mutation(double probability, Random random) {
            this.probability = probability;
            this.random = random;
        }

        public Mutation() {
            this(0.1, new Random());
        }

        @Override
        public MapNominal execute(MembershipFunction function) {
            MapNominal f = (MapNominal) function.copy();
            Iterator<String> iterator = f.getValues().keySet().iterator();
            String key;
            while (iterator.hasNext()) {
                key = iterator.next();
                if (this.random.nextDouble() <= this.probability) {
                    f.getValues().put(key, Utils.randomNumber(random));
                }
            }
            return f;
        }

    }

    public static class Repair implements IMembershipFunctionRepair<MapNominal> {
        protected Random random;

        public Repair(Random random) {
            this.random = random;
        }

        public Repair() {
            this(new Random());
        }

        @Override
        public MapNominal execute(MembershipFunction f, MembershipFunction l, MembershipFunction u) {
            MapNominal a = (MapNominal) f;
            MapNominal b = (MapNominal) l;
            Iterator<String> iterator = b.getValues().keySet().iterator();
            String key;
            while (iterator.hasNext()) {
                key = iterator.next();
                if (!a.getValues().containsKey(key)) {
                    a.add(key, Utils.randomNumber(random));
                }
                if (Double.compare(0, a.getValues().get(key)) > 0) {
                    a.getValues().put(key, Utils.randomNumber(random));
                } else if (Double.compare(1, a.getValues().get(key)) < 0) {
                    a.getValues().put(key, Utils.randomNumber(random));
                }
            }
            return a;
        }

    }
}
