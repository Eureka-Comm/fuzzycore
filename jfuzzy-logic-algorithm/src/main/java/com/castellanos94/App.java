package com.castellanos94;

import java.io.File;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
                log.error("Predicate guide {}", predicate);
                long maximumTime = 60 * 1000 * 5; // 5 min
                DiscoveryAlgorithm algorithm = new DiscoveryAlgorithm(predicate, maximumTime, logic, table, 0.95, 0.95,
                                0.1, 100, 100, 0.1, 0.95, 0.2, 20, 20);
                algorithm.execute();

                log.error("Elapsed time {} ms, limit time {} ms, diff {} ms", algorithm.getComputeTime(), maximumTime,
                                (maximumTime - algorithm.getComputeTime()));
                DiscoveryResult result = algorithm.getResult();
                log.error("Number of predicates discovery {}", result.getData().size());

                log.error("Start time {}", new Date(result.getStartTime()));
                log.error("End time {}", new Date(result.getEndTime()));
                log.error("Fitness - Discovery Predicate");
                for (Operator p : result.getData()) {
                        log.error("Valid? {} :{} - {} - {} ", OperatorUtil.isValid(p, true),p.getFitness(), p.toString(), OperatorUtil.getSuccessors(p).stream().filter(e -> e.equals(q)).map(e-> (State)q).findFirst().get().getMembershipFunction());
                }
        }
}
