package com.castellanos94.jfuzzylogic.core.base.impl;

import java.util.Iterator;
import java.util.Objects;

import com.castellanos94.jfuzzylogic.core.base.AElement;
import com.castellanos94.jfuzzylogic.core.base.JFuzzyLogicError;
import com.castellanos94.jfuzzylogic.core.base.Operator;

public class Imp extends Operator {

    public Imp() {
        super();
    }

    public Imp(AElement antecedent, AElement consequent) {
        this();
        Objects.requireNonNull(antecedent);
        Objects.requireNonNull(consequent);
        this.children.add(antecedent);
        this.children.add(consequent);
    }

    public AElement getAntecedent() {
        Iterator<AElement> iterator = this.children.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    public void setAntecedent(AElement antecedent) {
        if (this.children.isEmpty()) {
            this.children.add(antecedent);
        } else {
            AElement consequent = getConsequent();
            this.children.clear();
            this.children.add(antecedent);
            this.children.add(consequent);
        }
    }

    public AElement getConsequent() {
        Iterator<AElement> iterator = this.children.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            count++;
            AElement e = iterator.next();
            if (count == 2)
                return e;
        }
        return null;
    }

    public void setConsequent(AElement consequent) {
        if (this.children.isEmpty()) {
            this.children.add(null);
            this.children.add(consequent);
        } else {
            AElement antecedent = getAntecedent();
            this.children.clear();
            this.children.add(antecedent);
            this.children.add(consequent);
        }
    }

    @Override
    public boolean add(AElement e) {
        if (this.children.size() < 2) {
            return this.children.add(e);
        } else {
            throw new JFuzzyLogicError(JFuzzyLogicError.UNSUPPORTED + getClass().getSimpleName());
        }
    }

    @Override
    public Imp copy() {
        Imp cpy = new Imp();
        this.children.forEach(c -> cpy.add(c.copy()));
        cpy.setEditable(editable);
        cpy.setFitness(fitness);
        cpy.setLabel(label);
        cpy.setDescription(description);
        cpy.setUuid(uuid);
        return cpy;
    }

}