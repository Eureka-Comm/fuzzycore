package com.castellanos94.jfuzzylogic.core.base.impl;

import com.castellanos94.jfuzzylogic.core.base.AElement;
import com.castellanos94.jfuzzylogic.core.base.Operator;

public class Or extends Operator {
    public Or() {
        super();
    }

    public Or(AElement one, AElement two, AElement... others) {
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
    public Or copy() {
        Or cpy = new Or();
        this.children.forEach(c -> cpy.add(c.copy()));
        cpy.setEditable(editable);
        cpy.setFitness(fitness);
        return cpy;
    }

}
