package com.castellanos94.jfuzzylogic.core.membershipfunction;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MembershipFunctionType {
    @JsonProperty("gclv")
    GCLV("gclv"), 
    @JsonProperty("fpg")
    FPG("fpg"), 
    @JsonProperty("sigmoid")
    SIGMOID("sigmoid"), 
    @JsonProperty("-sigmoid")
    NSIGMOID("-sigmoid"), 
    @JsonProperty("singleton")
    SINGLETON("singleton"),
    @JsonProperty("map-nominal")
    MAPNOMIAL("map-nominal"), 
    @JsonProperty("triangular")
    TRIANGULAR("triangular"), 
    @JsonProperty("trapezoidal")
    TRAPEZOIDAL("trapezoidal"), 
    @JsonProperty("rtrapezoidal")
    RTRAPEZOIDAL("rtrapezoidal"),
    @JsonProperty("ltrapezoidal")
    LTRAPEZOIDAL("ltrapezoidal"), 
    @JsonProperty("gamma")
    GAMMA("gamma"), 
    @JsonProperty("lgamma")
    LGAMMA("lgamma"), 
    @JsonProperty("pseudo-exp")
    PSEUDOEXP("pseudo-exp"), 
    @JsonProperty("gaussian")
    GAUSSIAN("gaussian"),
    @JsonProperty("zform")
    ZFORM("zform"), 
    @JsonProperty("sform")
    SFORM("sform"), 
    @JsonProperty("nominal")
    NOMINAL("nominal"), 
    @JsonProperty("gbell")
    GBELL("gbell");

    private final String str;

    private MembershipFunctionType(String str) {
        this.str = str.toLowerCase();
    }
    
    @Override
    public String toString() {
        return this.str;
    }

}
