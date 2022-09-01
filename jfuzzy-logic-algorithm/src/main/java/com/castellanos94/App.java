package com.castellanos94;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.castellanos94.jfuzzylogic.algorithm.impl.EvaluationAlgorithm;
import com.castellanos94.jfuzzylogic.core.base.impl.EvaluationResult;
import com.castellanos94.jfuzzylogic.core.base.impl.Imp;
import com.castellanos94.jfuzzylogic.core.base.impl.State;
import com.castellanos94.jfuzzylogic.core.logic.LogicType;
import com.castellanos94.jfuzzylogic.core.logic.impl.LogicBuilder;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.Sigmoid;
import com.castellanos94.jfuzzylogic.core.task.impl.EvaluationTask;

import tech.tablesaw.api.Table;
/**
 * Hello world!
 *
 */

public class App {
    private static final Logger log = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        EvaluationTask task = new EvaluationTask();
        task.setLogicBuilder(LogicBuilder.newBuilder(LogicType.GMBC));
        task.setPredicate(new Imp(new State("high alcohol", "alcohol", new Sigmoid(11.65, 9.0)),
                new State("high quality", "quality", new Sigmoid(5.5, 4.))));
        Table table = Table.read().csv(new File("jfuzzy-logic-algorithm/src/main/resources/datasets/tinto.csv"));
        EvaluationAlgorithm algorithm = new EvaluationAlgorithm(task, table);
        algorithm.execute();
        EvaluationResult result = algorithm.getResult();
        log.error("Elapsed time {}s",((result.getEndTime() - result.getStartTime()) / 1000.0));
        log.error("{} - {}", result.getPredicate().getFitness(),result.getPredicate());
    }
}
