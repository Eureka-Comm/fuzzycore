package com.castellanos94.jfuzzylogic.core.base.impl;

import java.util.UUID;

import com.castellanos94.jfuzzylogic.core.base.AElement;
import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class State extends AElement {

    protected String colName;
    protected MembershipFunction membershipFunction;

    public State(String colName) {
        this(colName, colName, null, false, null, null);
    }

    public State(String label, String colName) {
        this(label, colName, null, false, null, null);
    }

    public State(String label, String colName, MembershipFunction function) {
        this(label, colName, function, false, null, null);
    }

    protected State(String label, String colName, MembershipFunction function, boolean editable, String uuid,
            String from) {
        this.label = label;
        this.colName = colName;
        this.membershipFunction = function == null ? null : function.copy();
        this.editable = editable;
        this.from = from;
        if (uuid == null)
            this.uuid = UUID.randomUUID().toString();
        else
            this.uuid = uuid;
    }

    @Override
    public State copy() {
        return new State(label, colName, membershipFunction, editable, uuid, from);
    }

    @Override
    public String toString() {
        return String.format("%s - %s %s", label, colName, membershipFunction == null ? "" : membershipFunction);
    }

}
