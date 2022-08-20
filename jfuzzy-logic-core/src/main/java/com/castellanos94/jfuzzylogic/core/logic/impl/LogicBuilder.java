package com.castellanos94.jfuzzylogic.core.logic.impl;

import com.castellanos94.jfuzzylogic.core.logic.ImplicationType;
import com.castellanos94.jfuzzylogic.core.logic.Logic;
import com.castellanos94.jfuzzylogic.core.logic.LogicType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.EqualsAndHashCode;

/**
 * Builder for <code>Logic</code>
 * 
 * @version 0.5.0
 */
@JsonPOJOBuilder(buildMethodName = "create", withPrefix = "set")
@EqualsAndHashCode
public class LogicBuilder {
    protected LogicType type;
    protected Integer exponent;
    protected ImplicationType implicationType;

    protected LogicBuilder() {
    }

    public static LogicBuilder newBuilder(LogicType type) {
        return new LogicBuilder(type);
    }

    private LogicBuilder(LogicType type) {
        this.type = type;
    }

    public LogicBuilder setExponent(Integer exponent) {
        this.exponent = exponent;
        return this;
    }

    public LogicBuilder setImplicationType(ImplicationType implicationType) {
        this.implicationType = implicationType;
        return this;
    }

    public LogicBuilder setType(LogicType type) {
        this.type = type;
        return this;
    }

    @JsonIgnore
    public Logic create() {
        if (implicationType == null) {
            implicationType = ImplicationType.Zadeh;
        }
        if (exponent == null) {
            exponent = -1;
        }
        switch (type) {
            case GMBC:
                return new GMBC(implicationType);
            case ZADEH:
                return new Zadeh();
            case AMBC:
                return new AMBC(implicationType);
            case GMBCV:
                return new GMBCFA(exponent, implicationType);
            default:
                return null;
        }
    }

    public LogicType getType() {
        return type;
    }

    public Integer getExponent() {
        return exponent;
    }

    public ImplicationType getImplicationType() {
        return implicationType;
    }

    @Override
    public String toString() {
        return "LogicBuilder [exponent=" + exponent + ", implication type=" + implicationType + ", type=" + type
                + "]";
    }
}
