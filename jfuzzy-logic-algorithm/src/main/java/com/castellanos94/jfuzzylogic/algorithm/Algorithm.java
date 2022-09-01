package com.castellanos94.jfuzzylogic.algorithm;

import com.castellanos94.jfuzzylogic.core.base.Result;
import com.castellanos94.jfuzzylogic.core.task.Task;

public abstract class Algorithm {
    protected long startTime;
    protected long endTime;
    protected Task task;

    public abstract void execute();

    public abstract Result getResult();

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

}
