package com.castellanos94.jfuzzylogic.core.logic;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @see Logic
 * @version 1.0
 */
public enum LogicType {
    @JsonProperty("gmbc")
    GMBC,
    @JsonProperty("ambc")
    AMBC,
    @JsonProperty("zadeh")
    ZADEH,
    @JsonProperty("gmbcv")
    GMBCV
}
