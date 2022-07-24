package com.castellanos94.jfuzzylogic.core.membershipfunction;

import com.castellanos94.jfuzzylogic.core.base.JFuzzyLogicError;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.FPG;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Abstract class representing a membership function definition
 * 
 * @version 1.0
 */
@JsonTypeInfo(include = As.PROPERTY, use = Id.NAME)
@JsonSubTypes(value = {
        @Type(value = FPG.class, names = { "fpg", "FPG" })
})
@Getter
@Setter
@ToString
public abstract class MembershipFunction {
    protected boolean editable;

    public double eval(Number value) {
        throw new JFuzzyLogicError(JFuzzyLogicError.UNSUPPORTED + " number values -> " + getClass().getSimpleName());
    }

    public double eval(String value) {
        throw new JFuzzyLogicError(JFuzzyLogicError.UNSUPPORTED + " string values -> " + getClass().getSimpleName());
    }

    @JsonIgnore
    public abstract boolean isValid();

    @JsonIgnore
    public abstract MembershipFunction copy();

    /**
     * method for assigning properties to an sorted array
     * 
     * @return array values
     */
    @JsonIgnore
    public Double[] toArray() {
        throw new JFuzzyLogicError(JFuzzyLogicError.UNSUPPORTED + getClass().getSimpleName());
    }
}
