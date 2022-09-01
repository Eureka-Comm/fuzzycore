package com.castellanos94.jfuzzylogic.core.task.impl;

import java.util.HashSet;

import com.castellanos94.jfuzzylogic.core.base.impl.Generator;
import com.castellanos94.jfuzzylogic.core.task.Task;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class DiscoveryTask extends Task {
    protected Integer populationSize;
    protected Integer numResult;
    protected Float minTruthValue;
    protected Float mutationRate;
    protected Long maxTime;

    protected Integer adjPopulationSize;
    protected Integer adjMaxIter;
    protected Float adjMinTruthValue;

    protected HashSet<Generator> generators;
}
