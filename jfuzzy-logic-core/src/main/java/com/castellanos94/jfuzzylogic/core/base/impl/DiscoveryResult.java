package com.castellanos94.jfuzzylogic.core.base.impl;

import java.util.LinkedList;

import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.Result;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscoveryResult extends Result {
    protected LinkedList<Operator> data;
}
