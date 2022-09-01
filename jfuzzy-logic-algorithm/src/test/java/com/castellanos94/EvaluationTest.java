package com.castellanos94;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.castellanos94.jfuzzylogic.algorithm.impl.EvaluationAlgorithm;
import com.castellanos94.jfuzzylogic.core.base.impl.EvaluationResult;
import com.castellanos94.jfuzzylogic.core.base.impl.Imp;
import com.castellanos94.jfuzzylogic.core.base.impl.State;
import com.castellanos94.jfuzzylogic.core.logic.LogicType;
import com.castellanos94.jfuzzylogic.core.logic.impl.LogicBuilder;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.Sigmoid;
import com.castellanos94.jfuzzylogic.core.task.impl.EvaluationTask;

import tech.tablesaw.api.Table;

public class EvaluationTest {
    @Test
    public void simpleEvaluationIMP() {
        EvaluationTask task = new EvaluationTask();
        task.setLogicBuilder(LogicBuilder.newBuilder(LogicType.GMBC));
        task.setPredicate(new Imp(new State("high alcohol", "alcohol", new Sigmoid(11.65, 9.0)),
                new State("high quality", "quality", new Sigmoid(5.5, 4.))));
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("datasets/tinto.csv").getFile());
        Table table = Table.read().csv(file);
        EvaluationAlgorithm algorithm = new EvaluationAlgorithm(task.getPredicate(), task.getLogicBuilder().create(),
                table);
        algorithm.execute();
        EvaluationResult result = algorithm.getResult();
        final long maxTimeMS = 500;
        long elapsedTime = (result.getEndTime() - result.getStartTime());
        System.out.println(
                String.format("The elapsed time %5ds must be less than or equal to %5ds", elapsedTime, maxTimeMS));
        assertTrue(
                String.format("The elapsed time %5ds must be less than or equal to %5ds", elapsedTime, maxTimeMS),
                elapsedTime <= maxTimeMS);
        double epsilon = 0.000001d;
        assertEquals(0.72721, result.getPredicate().getFitness(), epsilon);
    }
}
