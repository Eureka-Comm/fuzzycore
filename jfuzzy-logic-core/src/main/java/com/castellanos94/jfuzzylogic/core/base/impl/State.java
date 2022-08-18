package com.castellanos94.jfuzzylogic.core.base.impl;

import com.castellanos94.jfuzzylogic.core.base.AElement;
import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class State extends AElement {
    protected String colName;
    protected MembershipFunction membershipFunction;

    public State(String label) {
        this.label = label;
        this.colName = label;
    }

    public State(String label, String colName) {
        this.label = label;
        this.colName = colName;
    }

    public State(String label, String colName, MembershipFunction function) {
        this.label = label;
        this.colName = colName;
        this.membershipFunction = function;
    }

    protected State(String label, String colName, MembershipFunction function, boolean editable) {
        this.label = label;
        this.colName = colName;
        this.membershipFunction = function == null ? null : function.copy();
        this.editable = editable;
    }

    @Override
    public State copy() {
        return new State(label, colName, membershipFunction, editable);
    }

}
