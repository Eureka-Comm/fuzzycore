package com.castellanos94;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.castellanos94.jfuzzylogic.algorithm.impl.EvaluationAlgorithm;
import com.castellanos94.jfuzzylogic.algorithm.impl.MembershipFunctionOptimizer;
import com.castellanos94.jfuzzylogic.core.OperatorUtil;
import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.impl.And;
import com.castellanos94.jfuzzylogic.core.base.impl.EvaluationResult;
import com.castellanos94.jfuzzylogic.core.base.impl.Imp;
import com.castellanos94.jfuzzylogic.core.base.impl.Not;
import com.castellanos94.jfuzzylogic.core.base.impl.State;
import com.castellanos94.jfuzzylogic.core.logic.Logic;
import com.castellanos94.jfuzzylogic.core.logic.LogicType;
import com.castellanos94.jfuzzylogic.core.logic.impl.LogicBuilder;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.Sigmoid;

import tech.tablesaw.api.Table;

/**
 * Hello world!
 *
 */

public class App {
    private static final Logger log = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        ClassLoader classLoader = App.class.getClassLoader();
        File file = new File(classLoader.getResource("datasets/tinto.csv").getFile());
        Table table = Table.read().csv(file);
        Operator predicate = new Imp(
                new And(new State("alcohol"), new State("citric_acid"), new State("pH"), new State("volatile_acidity"),
                        new State("free_sulfur_dioxide"), new State("sulphates"), new State("residual_sugar")),
                new State("high quality", "quality", new Sigmoid(5.5, 4.)));
        Logic logic = LogicBuilder.newBuilder(LogicType.GMBC).create();
        MembershipFunctionOptimizer optimizer = new MembershipFunctionOptimizer( logic, table, 100, 20, 0.98,
                0.9, 0.1);
        Operator op = optimizer.execute(predicate);
        log.error("Elapsed time {} ms", optimizer.getComputeTime());
        log.error("Original: {} - {}", predicate.getFitness(), predicate.toString());
        log.error("Result : {} - {}", op.getFitness(),
                op.toString());
        log.error("States");
        OperatorUtil.getNodesByClass(op, State.class).forEach(s -> {
            log.error("{}", s);
        });
        log.error("Check");
        Operator p = (Operator) op.copy();
        log.error("Result : {} - {}", p.getFitness(),
                p.toString());
        EvaluationAlgorithm algorithm = new EvaluationAlgorithm(p, logic, table);
        algorithm.execute();
        log.error("Result : {} - {}", p.getFitness(),
                p.toString());
    }
}
