package com.castellanos94;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.castellanos94.jfuzzylogic.algorithm.impl.EvaluationAlgorithm;
import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.impl.And;
import com.castellanos94.jfuzzylogic.core.base.impl.Eqv;
import com.castellanos94.jfuzzylogic.core.base.impl.EvaluationResult;
import com.castellanos94.jfuzzylogic.core.base.impl.Imp;
import com.castellanos94.jfuzzylogic.core.base.impl.Not;
import com.castellanos94.jfuzzylogic.core.base.impl.Or;
import com.castellanos94.jfuzzylogic.core.base.impl.State;
import com.castellanos94.jfuzzylogic.core.logic.LogicType;
import com.castellanos94.jfuzzylogic.core.logic.impl.LogicBuilder;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.NSigmoid;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.Sigmoid;
import com.castellanos94.jfuzzylogic.core.task.impl.EvaluationTask;

import tech.tablesaw.api.Table;

public class EvaluationTest {
        @Test
        public void andEvaluation() {
                ClassLoader classLoader = getClass().getClassLoader();
                File file = new File(classLoader.getResource("datasets/tinto.csv").getFile());
                Table table = Table.read().csv(file);
                Operator predicate = new And(new State("high alcohol", "alcohol", new Sigmoid(11.65, 9.0)),
                                new State("high quality", "quality", new Sigmoid(5.5, 4.)));
                EvaluationAlgorithm algorithm = new EvaluationAlgorithm(predicate,
                                LogicBuilder.newBuilder(LogicType.GMBC).create(),
                                table);
                algorithm.execute();
                EvaluationResult result = algorithm.getResult();
                final long maxTimeMS = 500;
                long elapsedTime = (result.getEndTime() - result.getStartTime());
                System.out.println(predicate);
                System.out.println(
                                String.format("The elapsed time %5dms must be less than or equal to %5dms", elapsedTime,
                                                maxTimeMS));
                assertTrue(
                                String.format("The elapsed time %5dms must be less than or equal to %5dms", elapsedTime,
                                                maxTimeMS),
                                elapsedTime <= maxTimeMS);
                double epsilon = 0.000001d;
                System.out.println(result.getPredicate().getFitness());
                assertEquals(0.175709, result.getPredicate().getFitness(), epsilon);
        }

        @Test
        public void orEvaluation() {
                ClassLoader classLoader = getClass().getClassLoader();
                File file = new File(classLoader.getResource("datasets/tinto.csv").getFile());
                Table table = Table.read().csv(file);
                Operator predicate = new Or(new State("high alcohol", "alcohol", new Sigmoid(11.65, 9.0)),
                                new State("high quality", "quality", new Sigmoid(5.5, 4.)));
                EvaluationAlgorithm algorithm = new EvaluationAlgorithm(predicate,
                                LogicBuilder.newBuilder(LogicType.GMBC).create(),
                                table);
                algorithm.execute();
                EvaluationResult result = algorithm.getResult();
                final long maxTimeMS = 500;
                long elapsedTime = (result.getEndTime() - result.getStartTime());
                System.out.println(predicate);
                System.out.println(
                                String.format("The elapsed time %5dms must be less than or equal to %5dms", elapsedTime,
                                                maxTimeMS));
                assertTrue(
                                String.format("The elapsed time %5dms must be less than or equal to %5dms", elapsedTime,
                                                maxTimeMS),
                                elapsedTime <= maxTimeMS);
                double epsilon = 0.000001d;
                System.out.println(result.getPredicate().getFitness());
                assertEquals(0.3012477, result.getPredicate().getFitness(), epsilon);
        }

        @Test
        public void notEvaluation() {
                ClassLoader classLoader = getClass().getClassLoader();
                File file = new File(classLoader.getResource("datasets/tinto.csv").getFile());
                Table table = Table.read().csv(file);
                Operator predicate = new Not(new And(new State("high alcohol", "alcohol", new Sigmoid(11.65, 9.0)),
                                new State("high quality", "quality", new Sigmoid(5.5, 4.))));
                EvaluationAlgorithm algorithm = new EvaluationAlgorithm(predicate,
                                LogicBuilder.newBuilder(LogicType.GMBC).create(),
                                table);
                algorithm.execute();
                EvaluationResult result = algorithm.getResult();
                final long maxTimeMS = 500;
                long elapsedTime = (result.getEndTime() - result.getStartTime());
                System.out.println(predicate);
                System.out.println(
                                String.format("The elapsed time %5dms must be less than or equal to %5dms", elapsedTime,
                                                maxTimeMS));
                assertTrue(
                                String.format("The elapsed time %5dms must be less than or equal to %5dms", elapsedTime,
                                                maxTimeMS),
                                elapsedTime <= maxTimeMS);
                double epsilon = 0.000001d;
                System.out.println(result.getPredicate().getFitness());
                assertEquals(0.6315428, result.getPredicate().getFitness(), epsilon);
        }

        @Test
        public void eqvEvaluation() {
                ClassLoader classLoader = getClass().getClassLoader();
                File file = new File(classLoader.getResource("datasets/tinto.csv").getFile());
                Table table = Table.read().csv(file);
                Operator predicate = new Eqv(
                                new Not(new And(new State("high alcohol", "alcohol", new Sigmoid(11.65, 9.0)),
                                                new State("high quality", "quality", new Sigmoid(5.5, 4.)))),
                                new State("low pH", "pH", new NSigmoid(3.375, 2.93)));
                EvaluationAlgorithm algorithm = new EvaluationAlgorithm(predicate,
                                LogicBuilder.newBuilder(LogicType.GMBC).create(),
                                table);
                algorithm.execute();
                EvaluationResult result = algorithm.getResult();
                final long maxTimeMS = 500;
                long elapsedTime = (result.getEndTime() - result.getStartTime());
                System.out.println(predicate);
                System.out.println(
                                String.format("The elapsed time %5dms must be less than or equal to %5dms", elapsedTime,
                                                maxTimeMS));
                assertTrue(
                                String.format("The elapsed time %5dms must be less than or equal to %5dms", elapsedTime,
                                                maxTimeMS),
                                elapsedTime <= maxTimeMS);
                double epsilon = 0.000001d;
                System.out.println(result.getPredicate().getFitness());
                assertEquals(0.6339971, result.getPredicate().getFitness(), epsilon);
        }

        @Test
        public void simpleEvaluationIMP() {
                EvaluationTask task = new EvaluationTask();
                task.setLogicBuilder(LogicBuilder.newBuilder(LogicType.GMBC));
                task.setPredicate(new Imp(new State("high alcohol", "alcohol", new Sigmoid(11.65, 9.0)),
                                new State("high quality", "quality", new Sigmoid(5.5, 4.))));
                ClassLoader classLoader = getClass().getClassLoader();
                File file = new File(classLoader.getResource("datasets/tinto.csv").getFile());
                Table table = Table.read().csv(file);
                EvaluationAlgorithm algorithm = new EvaluationAlgorithm(task.getPredicate(),
                                task.getLogicBuilder().create(),
                                table);
                algorithm.execute();
                EvaluationResult result = algorithm.getResult();
                final long maxTimeMS = 500;
                long elapsedTime = (result.getEndTime() - result.getStartTime());
                System.out.println(task.getPredicate());
                System.out.println(
                                String.format("The elapsed time %5dms must be less than or equal to %5dms", elapsedTime,
                                                maxTimeMS));
                assertTrue(
                                String.format("The elapsed time %5dms must be less than or equal to %5dms", elapsedTime,
                                                maxTimeMS),
                                elapsedTime <= maxTimeMS);
                double epsilon = 0.000001d;
                assertEquals(0.72721, result.getPredicate().getFitness(), epsilon);
        }
}
