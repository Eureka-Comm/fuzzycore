package com.castellanos94;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;

import org.junit.Test;

import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.OperatorType;
import com.castellanos94.jfuzzylogic.core.base.impl.And;
import com.castellanos94.jfuzzylogic.core.base.impl.Eqv;
import com.castellanos94.jfuzzylogic.core.base.impl.Generator;
import com.castellanos94.jfuzzylogic.core.base.impl.Imp;
import com.castellanos94.jfuzzylogic.core.base.impl.State;
import com.castellanos94.jfuzzylogic.core.logic.LogicType;
import com.castellanos94.jfuzzylogic.core.logic.impl.LogicBuilder;
import com.castellanos94.jfuzzylogic.core.task.impl.DiscoveryTask;
import com.castellanos94.jfuzzylogic.core.task.impl.EvaluationTask;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class TaskTest {
    public HashSet<String> getTags() {
        HashSet<String> tag = new HashSet<>();
        tag.add("test");
        tag.add("junit");
        return tag;
    }

    public Operator getPredicate() {
        And antecedent = new And(new State("quality"), new State("acidity"), new State("citric"));

        return new Eqv(antecedent, new State("High quality", "quality").setDescription("buscando alto alcohol"));
    }

    @Test
    public void simpleTask() throws JsonProcessingException {
        EvaluationTask evaluationTask = new EvaluationTask();
        evaluationTask.setDataset("tinto.csv")
                .setDescription("simple evaluation task")
                .setName("Evaluation test")
                .setPredicate(getPredicate())
                .setLogicBuilder(LogicBuilder.newBuilder(LogicType.GMBC))
                .setTags(getTags());
        evaluationTask.setExtended(true);

        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        String str = mapper.writeValueAsString(evaluationTask);
        System.out.println(str);
        assertEquals(evaluationTask, mapper.readValue(str, EvaluationTask.class));
    }

    @Test
    public void discoveryTask() throws JsonProcessingException {
        Generator generator = new Generator()
                .setDepth(2);
        generator.setLabel("mi generador")
                .setDescription("Generador de descubrimiento");
        generator.add(new State("acidity"), new State("citric_acidity"), new State("density"), new State("alcohol"));
        generator.add(OperatorType.AND, OperatorType.IMP,OperatorType.EQV);
        DiscoveryTask discoveryTask = new DiscoveryTask();
        discoveryTask.setDataset("tinto.csv")
                .setDescription("simple discovery task")
                .setName("Discovery test")
                .setPredicate(getPredicate())
                .setLogicBuilder(LogicBuilder.newBuilder(LogicType.GMBC))
                .setTags(getTags());
        discoveryTask.setMaxTime(1000 * 5l)
                .setMinTruthValue(0.95)
                .setMutationRate(0.05)
                .setPopulationSize(100)
                .setAdjMaxIterations(20)
                .setAdjMinTruthValue(0.3)
                .setAdjPopulationSize(10)
                .setNumResult(10)
                .setPredicate(new Imp(generator, new State("quality")));

        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        String str = mapper.writeValueAsString(discoveryTask);
        System.out.println(str);
        assertEquals(discoveryTask, mapper.readValue(str, DiscoveryTask.class));
    }
}
