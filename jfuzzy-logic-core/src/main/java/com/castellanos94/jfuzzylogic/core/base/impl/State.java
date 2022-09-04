package com.castellanos94.jfuzzylogic.core.base.impl;

import java.util.UUID;

import com.castellanos94.jfuzzylogic.core.base.AElement;
import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((colName == null) ? 0 : colName.hashCode());
        result = prime * result + ((membershipFunction == null) ? 0 : membershipFunction.hashCode());
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        State other = (State) obj;
        if (colName == null) {
            if (other.colName != null)
                return false;
        } else if (!colName.equals(other.colName))
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (membershipFunction == null) {
            if (other.membershipFunction != null)
                return false;
        } else if (!membershipFunction.equals(other.membershipFunction))
            return false;
        if (from == null) {
            if (other.from != null)
                return false;
        } else if (!from.equals(other.from))
            return false;
        return editable == other.editable;
    }

}
