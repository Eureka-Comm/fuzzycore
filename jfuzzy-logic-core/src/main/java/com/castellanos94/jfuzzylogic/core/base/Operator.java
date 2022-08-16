package com.castellanos94.jfuzzylogic.core.base;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public abstract class Operator extends AElement implements Comparable<Operator>, Iterable<AElement> {
    protected LinkedHashSet<AElement> children;
    protected Double fitness;

    protected Operator() {
        this.children = new LinkedHashSet<>();
    }

    public abstract boolean add(AElement e);

    protected boolean _add(AElement e) {
        Objects.requireNonNull(e);
        return this.children.add(e);
    }

    @Override
    public int compareTo(Operator o) {
        return Double.compare(fitness, o.fitness);
    }

    @Override
    public Iterator<AElement> iterator() {
        return this.children.iterator();
    }

    public Double getFitness() {
        return fitness;
    }

    public void setFitness(Double fitness) {
        this.fitness = fitness;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(String.format("(%s", getClass().getSimpleName().toUpperCase()));
        this.children.forEach(c -> {
            if (c instanceof Operator) {
                buffer.append(c.toString());
            } else {
                buffer.append(String.format(" \"%s\"", c.getLabel()));
            }
        });
        buffer.append(")");
        return buffer.toString();
    }
}
