package com.castellanos94.jfuzzylogic.algorithm;

import com.castellanos94.jfuzzylogic.core.base.Result;

public abstract class Algorithm {
    protected long startTime;
    protected long endTime;

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

    /**
     * Get computation time
     * 
     * @return time ms
     */
    public long getComputeTime() {
        return this.endTime - this.startTime;
    }

}
