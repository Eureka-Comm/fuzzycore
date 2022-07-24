package com.castellanos94.jfuzzylogic.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class MembershipFunction {
    protected final MembershipFunctionType type;
    protected boolean editable;

    public MembershipFunction(MembershipFunctionType type) {
        this.type = type;
    }

    public double eval(Number value) {
        throw new JFuzzyLogicError(JFuzzyLogicError.UNSUPPORTED + " number values -> " + getClass().getSimpleName());
    }

    public double eval(String value) {
        throw new JFuzzyLogicError(JFuzzyLogicError.UNSUPPORTED + " string values -> " + getClass().getSimpleName());
    }

    public MembershipFunctionType getType() {
        return type;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @JsonIgnore
    public abstract MembershipFunction copy();

    /**
     * method for assigning properties to an sorted array
     * 
     * @return array values
     */
    @JsonIgnore
    public Double[] toArray() {
        throw new JFuzzyLogicError(JFuzzyLogicError.UNSUPPORTED + type);
    }
}
