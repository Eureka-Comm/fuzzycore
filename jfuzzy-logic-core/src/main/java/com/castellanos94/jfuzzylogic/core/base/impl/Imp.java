package com.castellanos94.jfuzzylogic.core.base.impl;

import com.castellanos94.jfuzzylogic.core.base.AElement;
import com.castellanos94.jfuzzylogic.core.base.JFuzzyLogicError;
import com.castellanos94.jfuzzylogic.core.base.Operator;

public class Imp extends Operator {
    public Imp() {
        super();
    }

    public Imp(AElement antecedent, AElement consequent) {
        this();
        add(antecedent);
        add(consequent);
    }

    @Override
    public boolean add(AElement e) {
        if (this.children.size() < 2)
            return _add(e);
        throw new JFuzzyLogicError("maximum arity reached for " + getClass().getSimpleName());
    }

    @Override
    public Imp copy() {
        Imp cpy = new Imp();
        this.children.forEach(c -> cpy.add(c.copy()));
        cpy.setEditable(editable);
        cpy.setFitness(fitness);
        return cpy;
    }

}