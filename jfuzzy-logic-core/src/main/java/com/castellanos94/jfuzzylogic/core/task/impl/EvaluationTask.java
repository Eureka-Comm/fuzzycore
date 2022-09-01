package com.castellanos94.jfuzzylogic.core.task.impl;

import com.castellanos94.jfuzzylogic.core.task.Task;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class EvaluationTask extends Task {
    protected boolean extended;
}
