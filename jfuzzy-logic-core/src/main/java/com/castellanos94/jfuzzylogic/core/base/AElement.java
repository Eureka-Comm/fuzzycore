package com.castellanos94.jfuzzylogic.core.base;

import java.util.UUID;

import com.castellanos94.jfuzzylogic.core.base.impl.And;
import com.castellanos94.jfuzzylogic.core.base.impl.Eqv;
import com.castellanos94.jfuzzylogic.core.base.impl.Generator;
import com.castellanos94.jfuzzylogic.core.base.impl.Imp;
import com.castellanos94.jfuzzylogic.core.base.impl.Not;
import com.castellanos94.jfuzzylogic.core.base.impl.Or;
import com.castellanos94.jfuzzylogic.core.base.impl.State;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Abstract class representing the basic properties of an element.
 * 
 * @version 1.0.0
 */
@JsonTypeInfo(use = Id.NAME)
@JsonSubTypes(value = {
        @Type(value = State.class, names = { "state", "STATE", "linguisticState" }),
        @Type(value = And.class, names = { "and", "AND" }),
        @Type(value = Or.class, names = { "or", "OR" }),
        @Type(value = Imp.class, names = { "imp", "IMP" }),
        @Type(value = Eqv.class, names = { "eqv", "EQV" }),
        @Type(value = Not.class, names = { "not", "NOT" }),
        @Type(value = Generator.class, names = { "generator", "Generator", "GENERATOR", "*", "comodin" })
})
@Getter
@Setter
@EqualsAndHashCode
@ToString
public abstract class AElement {
    @EqualsAndHashCode.Include
    protected String label;
    @EqualsAndHashCode.Include
    protected String description;

    @EqualsAndHashCode.Include
    protected String from;
    @EqualsAndHashCode.Include
    protected boolean editable;
    @EqualsAndHashCode.Include
    protected String uuid;

    public AElement() {
        this.uuid = UUID.randomUUID().toString();
    }

    @JsonIgnore
    public abstract AElement copy();

}
