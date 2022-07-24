package com.castellanos94.jfuzzylogic.core.base;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Abstract class representing the basic properties of an element.
 * 
 * @version 1.0.0
 */
public abstract class AElement {
    protected String label;
    protected String description;
    protected boolean editable;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @JsonIgnore
    public abstract AElement copy();

    @Override
    public String toString() {
        return "AElement [description=" + description + ", editable=" + editable + ", label=" + label + "]";
    }

}
