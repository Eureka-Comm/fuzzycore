package com.castellanos94.jfuzzylogic.core.base.impl;

import com.castellanos94.jfuzzylogic.core.base.AElement;
import com.castellanos94.jfuzzylogic.core.base.JFuzzyLogicError;
import com.castellanos94.jfuzzylogic.core.base.Operator;

public class Not extends Operator {
    public Not() {
        super();
    }

    public Not(AElement child) {
        this();
        add(child);
    }

    @Override
    public boolean add(AElement e) {
        if (this.children.isEmpty())
            return _add(e);
        throw new JFuzzyLogicError("maximum arity reached for " + getClass().getSimpleName());
    }

    @Override
    public Not copy() {
        Not cpy = new Not();
        this.children.forEach(c -> cpy.add(c.copy()));
        cpy.setEditable(editable);
        cpy.setFitness(fitness);
        cpy.setLabel(label);
        cpy.setDescription(description);
        
        return cpy;
    }

}