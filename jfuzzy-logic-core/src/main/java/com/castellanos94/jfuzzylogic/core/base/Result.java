package com.castellanos94.jfuzzylogic.core.base;

import com.castellanos94.jfuzzylogic.core.task.Task;

import lombok.Data;

@Data
public abstract class Result {
    protected Task task;
    protected long startTime;
    protected long endTime;    
}
