package com.castellanos94.jfuzzylogic.core.base.impl;

import com.castellanos94.jfuzzylogic.core.base.AElement;
import com.castellanos94.jfuzzylogic.core.base.Operator;

public class And extends Operator {
    public And() {
        super();
    }

    public And(AElement one, AElement two, AElement... others) {
        this();
        add(one);
        add(two);
        for (AElement aElement : others) {
            add(aElement);
        }
    }

    @Override
    public boolean add(AElement e) {
        return _add(e);
    }

    @Override
    public And copy() {
        And cpy = new And();
        this.children.forEach(c -> cpy.add(c.copy()));
        cpy.setEditable(editable);
        cpy.setFitness(fitness);
        return cpy;
    }

}
