package com.castellanos94.jfuzzylogic.core.base.impl;

import java.util.HashSet;
import java.util.Set;

import com.castellanos94.jfuzzylogic.core.base.AElement;
import com.castellanos94.jfuzzylogic.core.base.OperatorType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Generator extends AElement {
    protected Integer depth;
    protected Integer maxChild;
    protected Set<AElement> children;
    protected Set<OperatorType> operators;

    public Generator() {
        this.children = new HashSet<>();
        this.operators = new HashSet<>();
    }

    protected Generator(String label, String description, boolean editable, Integer depth, Integer maxChild,
            Set<AElement> child, Set<OperatorType> operators) {
        this();
        this.label = label;
        this.description = description;
        this.editable = editable;
        this.depth = depth;
        this.maxChild = maxChild;
        if (child != null) {
            this.children = child;
        }
        this.operators = operators;
    }

    public void add(AElement e, AElement... others) {
        if (e != null) {
            this.children.add(e);
        }
        for (AElement o : others) {
            if (o != null) {
                this.children.add(o);
            }
        }
    }

    public void add(OperatorType e, OperatorType... others) {
        if (e != null) {
            this.operators.add(e);
        }
        for (OperatorType o : others) {
            if (o != null) {
                this.operators.add(o);
            }
        }
    }

    @Override
    public Generator copy() {
        Set<AElement> cpy = new HashSet<>();
        children.forEach(c -> cpy.add(c.copy()));
        return new Generator(label, description, editable, depth, maxChild, cpy, operators);
    }

}
