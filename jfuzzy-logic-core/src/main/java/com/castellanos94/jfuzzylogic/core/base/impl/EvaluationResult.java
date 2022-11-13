package com.castellanos94.jfuzzylogic.core.base.impl;

import java.util.HashMap;
import java.util.List;

import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.Result;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvaluationResult extends Result {
    protected Operator predicate;
    protected Double forAll;
    protected Double exists;
    protected HashMap<String, List<Double>> data;
}
