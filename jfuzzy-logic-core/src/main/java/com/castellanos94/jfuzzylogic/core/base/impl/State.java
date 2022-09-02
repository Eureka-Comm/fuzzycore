package com.castellanos94.jfuzzylogic.core.base.impl;

import java.util.UUID;

import com.castellanos94.jfuzzylogic.core.base.AElement;
import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class State extends AElement {
    @Exclude
    protected String uuid;

    protected String colName;
    protected MembershipFunction membershipFunction;

    public State(String colName) {
        this(colName, colName, null, false);
    }

    public State(String label, String colName) {
        this(label, colName, null, false);
    }

    public State(String label, String colName, MembershipFunction function) {
        this(label, colName, function, false);
    }

    protected State(String label, String colName, MembershipFunction function, boolean editable) {
        this.label = label;
        this.colName = colName;
        this.membershipFunction = function == null ? null : function.copy();
        this.editable = editable;
        this.uuid = UUID.randomUUID().toString();
    }

    @Override
    public State copy() {
        return new State(label, colName, membershipFunction, editable);
    }

    @Override
    public String toString() {
        return String.format("%s - %s %s", label, colName, membershipFunction);
    }

}
