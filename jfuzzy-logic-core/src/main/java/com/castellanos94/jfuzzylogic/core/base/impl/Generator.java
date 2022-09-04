package com.castellanos94.jfuzzylogic.core.base.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.castellanos94.jfuzzylogic.core.base.AElement;
import com.castellanos94.jfuzzylogic.core.base.JFuzzyLogicError;
import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.OperatorType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Generator extends Operator {
    protected Integer depth;
    protected Integer maxChild;
    protected Set<OperatorType> operators;

    public Generator() {
        super();
        this.operators = new HashSet<>();
    }

    protected Generator(String label, String description, boolean editable, Integer depth, Integer maxChild,
            Set<AElement> child, Set<OperatorType> operators, String uuid) {
        this();
        this.uuid = uuid;
        this.label = label;
        this.description = description;
        this.editable = editable;
        this.depth = depth;
        this.maxChild = maxChild;
        if (child != null) {
            this.children = new LinkedHashSet<>(child);
        }
        this.operators = operators;
    }

    @Deprecated
    @Override
    @JsonIgnore
    public Double getFitness() {
        return Double.NaN;
    }

    @JsonIgnore
    public List<State> getStates() {
        Set<State> states = new HashSet<>();
        for (AElement c : children) {
            if (c instanceof State) {
                states.add((State) c);
            }
        }
        return new ArrayList<>(states);
    }

    @JsonIgnore
    public List<Generator> getGenerators() {
        List<Generator> generators = new ArrayList<>();
        for (AElement c : children) {
            if (c instanceof Generator) {
                generators.add((Generator) c);
            }
        }
        return generators;
    }

    @Deprecated
    @Override
    @JsonIgnore
    public void setFitness(Double fitness) {

    }

    @Deprecated
    @Override
    public int compareTo(Operator o) {
        throw new JFuzzyLogicError(JFuzzyLogicError.UNSUPPORTED + getClass().getSimpleName());
    }

    public void add(AElement e, AElement... others) {
        add(e);
        for (AElement o : others) {
            if (o != null) {
                add(o);
            }
        }
    }

    @Override
    public boolean add(AElement e) {
        if (e instanceof Operator && !(e instanceof Generator)) {
            throw new JFuzzyLogicError("Operators cannot be added");
        }
        e.setEditable(true);
        e.setFrom(this.uuid);
        return _add(e);
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
        return new Generator(label, description, editable, depth, maxChild, cpy, operators, uuid);
    }

}
