package com.castellanos94.jfuzzylogic.core.task.impl;

import com.castellanos94.jfuzzylogic.core.task.Task;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class DiscoveryTask extends Task {
    @EqualsAndHashCode.Include
    protected Integer populationSize;
    @EqualsAndHashCode.Include
    protected Integer numResult;
    @EqualsAndHashCode.Include
    protected Double minTruthValue;
    @EqualsAndHashCode.Include
    protected Double mutationRate;
    @EqualsAndHashCode.Include
    protected Long maxTime;
    @EqualsAndHashCode.Include
    protected Integer adjPopulationSize;
    @EqualsAndHashCode.Include
    protected Integer adjMaxIter;
    @EqualsAndHashCode.Include
    protected Double adjMinTruthValue;    
}
