package com.castellanos94.jfuzzylogic.core.task;

import java.util.HashSet;

import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.logic.impl.LogicBuilder;
import com.castellanos94.jfuzzylogic.core.task.impl.DiscoveryTask;
import com.castellanos94.jfuzzylogic.core.task.impl.EvaluationTask;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonTypeInfo(include = As.PROPERTY, use = Id.NAME)
@JsonSubTypes(value = {
        @Type(value = DiscoveryTask.class, names = { "discovery", "DISCOVERY" }),
        @Type(value = EvaluationTask.class, names = { "evaluation", "EVALUATION" })
})
@Getter
@Setter
@EqualsAndHashCode
@ToString
public abstract class Task {
    @EqualsAndHashCode.Include
    protected String name;
    protected String descripton;
    @EqualsAndHashCode.Include
    protected String dataset;
    @EqualsAndHashCode.Include
    protected Operator predicate;
    @EqualsAndHashCode.Include
    protected LogicBuilder logicBuilder;
    protected HashSet<String> tags;
}
