package com.castellanos94.jfuzzylogic.core.base;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Abstract class representing the basic properties of an element.
 * 
 * @version 1.0.0
 */
@Getter
@Setter
public abstract class AElement {
    protected String label;
    protected String description;
    protected boolean editable;

    @JsonIgnore
    public abstract AElement copy();

}
