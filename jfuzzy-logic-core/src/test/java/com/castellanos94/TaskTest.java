package com.castellanos94;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;

import org.junit.Test;

import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.impl.And;
import com.castellanos94.jfuzzylogic.core.base.impl.Eqv;
import com.castellanos94.jfuzzylogic.core.base.impl.State;
import com.castellanos94.jfuzzylogic.core.logic.LogicType;
import com.castellanos94.jfuzzylogic.core.logic.impl.LogicBuilder;
import com.castellanos94.jfuzzylogic.core.query.impl.EvaluationTask;
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
                .setDescripton("simple evaluation task")
                .setName("Evaluation test")
                .setPredicate(getPredicate())
                .setLogicBuilder(LogicBuilder.newBuilder(LogicType.GMBC))
                .setTags(getTags());

        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        String str = mapper.writeValueAsString(evaluationTask);
        System.out.println(str);
        assertEquals(evaluationTask, mapper.readValue(str, EvaluationTask.class));
    }
}
