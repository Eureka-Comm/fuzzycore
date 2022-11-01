package com.castellanos94;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.HashSet;

import org.junit.Test;

import com.castellanos94.jfuzzylogic.algorithm.impl.DiscoveryAlgorithm;
import com.castellanos94.jfuzzylogic.core.OperatorUtil;
import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.OperatorType;
import com.castellanos94.jfuzzylogic.core.base.impl.DiscoveryResult;
import com.castellanos94.jfuzzylogic.core.base.impl.Generator;
import com.castellanos94.jfuzzylogic.core.base.impl.Imp;
import com.castellanos94.jfuzzylogic.core.base.impl.State;
import com.castellanos94.jfuzzylogic.core.logic.Logic;
import com.castellanos94.jfuzzylogic.core.logic.LogicType;
import com.castellanos94.jfuzzylogic.core.logic.impl.LogicBuilder;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.Sigmoid;

import tech.tablesaw.api.Table;

public class DiscoveryAlgorithmTest {
        @Test
        public void performanceDiscoveryWithPredicateGuide() {
                ClassLoader classLoader = App.class.getClassLoader();
                File file = new File(classLoader.getResource("datasets/tinto.csv").getFile());
                Table table = Table.read().csv(file);
                Logic logic = LogicBuilder.newBuilder(LogicType.GMBC).create();
                Generator generator = new Generator();
                generator.setLabel("Mi generador");
                generator.setDepth(2);
                table.columnNames().forEach(cn -> {
                        if (!cn.equalsIgnoreCase("quality"))
                                generator.add(new State(cn));
                });
                generator.add(OperatorType.AND, OperatorType.EQV, OperatorType.IMP, OperatorType.OR, OperatorType.NOT);
                Operator predicate = new Imp(generator,
                                new State("high quality", "quality", new Sigmoid(5.5, 4.)));
                System.out.println("Predicate guide " + predicate);
                long maximumTime = 60 * 1000 * 5; // 5 min
                DiscoveryAlgorithm algorithm = new DiscoveryAlgorithm(predicate, maximumTime, logic, table, 0.95, 0.95,
                                0.1, 100, 100, 0.1, 0.95, 0.2, 20, 20);
                algorithm.execute();

                System.out.printf("Elapsed time %d ms, limit time %d ms, diff %d ms", algorithm.getComputeTime(),
                                maximumTime,
                                (maximumTime - algorithm.getComputeTime()));
                DiscoveryResult result = algorithm.getResult();

                assertEquals(true, result.getData().size() > 2);
                int index = 0;
                for (Operator op : result.getData()) {
                        System.out.println((index++) + " " + op);
                        assertEquals(true, OperatorUtil.isValid(op, true));
                        assertEquals(predicate.getClass(), op.getClass());
                }
        }

        @Test
        public void performanceEvaluationWithDiscoveryAlgorithm() {
                ClassLoader classLoader = App.class.getClassLoader();
                File file = new File(classLoader.getResource("datasets/tinto.csv").getFile());
                Table table = Table.read().csv(file);
                Logic logic = LogicBuilder.newBuilder(LogicType.GMBC).create();
                Operator predicate = new Imp(new State("high alcohol", "alcohol", new Sigmoid(11.65, 9.0)),
                                new State("high quality", "quality", new Sigmoid(5.5, 4.)));
                long maximumTime = 60 * 1000 * 2; // 5 min
                DiscoveryAlgorithm algorithm = new DiscoveryAlgorithm(predicate, maximumTime, logic, table, 0.95, 0.95,
                                0.1, 100, 100, 0.1, 0.95, 0.2, 20, 20);
                algorithm.execute();

                System.out.printf("Elapsed time %d ms, limit time %d ms, diff %d ms", algorithm.getComputeTime(),
                                maximumTime,
                                (maximumTime - algorithm.getComputeTime()));
                DiscoveryResult result = algorithm.getResult();

                assertEquals(1, result.getData().size());
                assertEquals(true, OperatorUtil.isValid(result.getData().getFirst(), true));
        }

        @Test
        public void performanceMembershipOptimizerWithDiscoveryAlgorithm() {
                ClassLoader classLoader = App.class.getClassLoader();
                File file = new File(classLoader.getResource("datasets/tinto.csv").getFile());
                Table table = Table.read().csv(file);
                Logic logic = LogicBuilder.newBuilder(LogicType.GMBC).create();
                Operator predicate = new Imp(new State("alcohol"),
                                new State("high quality", "quality", new Sigmoid(5.5, 4.)));
                long maximumTime = 60 * 1000 * 2; // 5 min
                int maximumNumberResult = 100;
                DiscoveryAlgorithm algorithm = new DiscoveryAlgorithm(predicate, maximumTime, logic, table, 0.95, 0.95,
                                0.1, maximumNumberResult, 100, 0.1, 0.95, 0.2, 20, 20);
                algorithm.execute();

                System.out.printf("Elapsed time %d ms, limit time %d ms, diff %d ms\n", algorithm.getComputeTime(),
                                maximumTime,
                                (maximumTime - algorithm.getComputeTime()));
                DiscoveryResult result = algorithm.getResult();

                assertEquals(maximumNumberResult, result.getData().size());
                for (Operator element : result.getData()) {
                        assertEquals(true, OperatorUtil.isValid(element, true));
                }
                System.out.println("Uniques predicates: " + new HashSet<>(result.getData()).size());
                assertEquals(maximumNumberResult, new HashSet<>(result.getData()).size());
        }

        @Test
        public void minTime() {
                ClassLoader classLoader = App.class.getClassLoader();
                File file = new File(classLoader.getResource("datasets/tinto.csv").getFile());
                Table table = Table.read().csv(file);
                Logic logic = LogicBuilder.newBuilder(LogicType.GMBC).create();
                Operator predicate = new Imp(new State("high alcohol", "alcohol", new Sigmoid(11.65, 9.0)),
                                new State("high quality", "quality", new Sigmoid(5.5, 4.)));
                predicate = new Imp(new State("alcohol"),
                                new State("high quality", "quality", new Sigmoid(5.5, 4.)));

                Generator generator = new Generator();
                generator.setLabel("Mi generador");
                generator.setDepth(2);
                // generator.setMaxChild(3);
                table.columnNames().forEach(cn -> {
                        generator.add(new State(cn));
                });
                generator.add(OperatorType.AND, OperatorType.EQV, OperatorType.IMP, OperatorType.OR, OperatorType.NOT);
                predicate = generator;
                State q = new State("high qualit0y", "quality", new Sigmoid(5.5, 4.));
                predicate = new Imp(generator, q);
                long maximumTime = 60 * 1000 * 2; // 2 min
                int maximumNumberResult = 100;
                DiscoveryAlgorithm algorithm = new DiscoveryAlgorithm(predicate, maximumTime, logic, table, 0.95, 0.95,
                                0.1, maximumNumberResult, 100, 0.1, 0.95, 0.2, 20, 20);
                algorithm.execute();

                System.out.printf("Elapsed time %d ms, limit time %d ms, diff %d ms\n", algorithm.getComputeTime(),
                                maximumTime,
                                (maximumTime - algorithm.getComputeTime()));
                DiscoveryResult result = algorithm.getResult();
                assertEquals(true, result.getData().size() == maximumNumberResult
                                || maximumTime <= algorithm.getComputeTime());
                assertEquals(true, !result.getData().isEmpty());
                for (Operator p : result.getData()) {
                        assertEquals(true, OperatorUtil.isValid(p, true));
                }
        }
}
