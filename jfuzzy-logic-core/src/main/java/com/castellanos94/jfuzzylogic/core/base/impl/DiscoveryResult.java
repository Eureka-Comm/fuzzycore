package com.castellanos94.jfuzzylogic.core.base.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.Result;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscoveryResult extends Result {
    protected LinkedList<Operator> data;
}
