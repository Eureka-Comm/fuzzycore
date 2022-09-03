package com.castellanos94;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

import com.castellanos94.jfuzzylogic.algorithm.JFuzzyLogicAlgorithmError;
import com.castellanos94.jfuzzylogic.algorithm.impl.EvaluationAlgorithm;
import com.castellanos94.jfuzzylogic.algorithm.impl.MembershipFunctionOptimizer;
import com.castellanos94.jfuzzylogic.core.OperatorUtil;
import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.impl.And;
import com.castellanos94.jfuzzylogic.core.base.impl.Imp;
import com.castellanos94.jfuzzylogic.core.base.impl.State;
import com.castellanos94.jfuzzylogic.core.logic.Logic;
import com.castellanos94.jfuzzylogic.core.logic.LogicType;
import com.castellanos94.jfuzzylogic.core.logic.impl.LogicBuilder;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.FPG;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.Sigmoid;

import tech.tablesaw.api.Table;

public class MembershipFunctionOptimizerTest {
    @Test
    public void discoveryFPG() {
        ClassLoader classLoader = App.class.getClassLoader();
        File file = new File(classLoader.getResource("datasets/tinto.csv").getFile());
        Table table = Table.read().csv(file);
        Operator predicate = new Imp(
                new And(new State("low alcohol", "alcohol", new FPG(1.0, null, 0.4)),
                        new State("acid", "citric_acid", new FPG(null, 0.5, null)),
                        new State("pH"), new State("volatile_acidity"),
                        new State("free_sulfur_dioxide"), new State("sulphates"),
                        new State("residual_sugar")),
                new State("high quality", "quality", new Sigmoid(5.5, 4.)));
        Logic logic = LogicBuilder.newBuilder(LogicType.GMBC).create();
        MembershipFunctionOptimizer optimizer = new MembershipFunctionOptimizer(logic, table, 100, 20, 0.98,
                0.9, 0.1);
        Operator op = optimizer.execute(predicate);
        OperatorUtil.getNodesByClass(op, State.class).forEach(s -> {
            assertNotNull(s.getMembershipFunction());
        });
        Operator p = (Operator) op.copy();
        EvaluationAlgorithm algorithm = new EvaluationAlgorithm(p, logic, table);
        algorithm.execute();
        assertEquals(op.getFitness(), p.getFitness(), 0);
    }

    @Test(expected = JFuzzyLogicAlgorithmError.class)
    public void discoverNotRegisterFunctionOperator() {
        ClassLoader classLoader = App.class.getClassLoader();
        File file = new File(classLoader.getResource("datasets/tinto.csv").getFile());
        Table table = Table.read().csv(file);
        Operator predicate = new Imp(
                new And(new State("low alcohol", "alcohol", new FPG(1.0, null, 0.4)),
                        new State("acid", "citric_acid", new FPG(null, 0.5, null)),
                        new State("pH"), new State("volatile_acidity", "volatile_acidity", new Sigmoid()),
                        new State("free_sulfur_dioxide"), new State("sulphates"),
                        new State("residual_sugar")),
                new State("high quality", "quality", new Sigmoid(5.5, 4.)));
        Logic logic = LogicBuilder.newBuilder(LogicType.GMBC).create();
        MembershipFunctionOptimizer optimizer = new MembershipFunctionOptimizer(logic, table, 100, 20, 0.98,
                0.9, 0.1);
        Operator op = optimizer.execute(predicate);
    }
}
