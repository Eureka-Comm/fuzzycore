package com.castellanos94.jfuzzylogic.core.base.impl;

import com.castellanos94.jfuzzylogic.core.base.AElement;
import com.castellanos94.jfuzzylogic.core.base.JFuzzyLogicError;
import com.castellanos94.jfuzzylogic.core.base.Operator;

public class Eqv extends Operator {
    public Eqv() {
        super();
    }

    public Eqv(AElement antecedent, AElement consequent) {
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
    public Eqv copy() {
        Eqv cpy = new Eqv();
        this.children.forEach(c -> cpy.add(c.copy()));
        cpy.setEditable(editable);
        cpy.setFitness(fitness);
        cpy.setLabel(label);
        cpy.setDescription(description);
        cpy.setUuid(uuid);
        return cpy;
    }

}