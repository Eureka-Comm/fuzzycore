package com.castellanos94.jfuzzylogic.algorithm.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.castellanos94.jfuzzylogic.algorithm.Algorithm;
import com.castellanos94.jfuzzylogic.algorithm.JFuzzyLogicAlgorithmError;
import com.castellanos94.jfuzzylogic.core.OperatorUtil;
import com.castellanos94.jfuzzylogic.core.base.AElement;
import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.impl.And;
import com.castellanos94.jfuzzylogic.core.base.impl.Eqv;
import com.castellanos94.jfuzzylogic.core.base.impl.EvaluationResult;
import com.castellanos94.jfuzzylogic.core.base.impl.Imp;
import com.castellanos94.jfuzzylogic.core.base.impl.Not;
import com.castellanos94.jfuzzylogic.core.base.impl.Or;
import com.castellanos94.jfuzzylogic.core.base.impl.State;
import com.castellanos94.jfuzzylogic.core.logic.Logic;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

/**
 * Algorithm for evaluate predicate
 * 
 * @version 1.0.0
 */
public class EvaluationAlgorithm extends Algorithm {
    private static final Logger log = LogManager.getLogger(EvaluationAlgorithm.class);
    private static final List<ColumnType> numeriColumnTypes = Arrays.asList(ColumnType.DOUBLE, ColumnType.FLOAT,
            ColumnType.INTEGER,
            ColumnType.LONG, ColumnType.SHORT);
    protected HashMap<String, List<Double>> data;
    protected EvaluationResult result;
    protected Table table;
    protected Logic logic;
    protected Operator predicate;

    /**
     * Default constructor
     * 
     * @param predicate to evaluate
     * @param logic     to applies
     * @param table     dataset
     */
    public EvaluationAlgorithm(Operator predicate, Logic logic, Table table) {
        this.predicate = predicate;
        this.table = table;
        this.logic = logic;
        this.result = new EvaluationResult();
    }

    /**
     * Performs the evaluation of the predicate, assigning the truth value given the
     * data set.
     * 
     * @throws JFuzzyLogicAlgorithmError in case of: missing column, a null element in the
     *                          predicate (node or membership function) and the
     *                          evaluation of a non-compatible element.
     */
    @Override
    public void execute() {
        this.startTime = System.currentTimeMillis();
        this.result.setStartTime(startTime);
        fuzzyData();
        evaluate();
        this.endTime = System.currentTimeMillis();
        this.result.setEndTime(endTime);
    }

    private void evaluate() {
        List<Double> result = new ArrayList<>(table.rowCount());
        for (int i = 0; i < table.rowCount(); i++) {
            result.add(fitValue(predicate, i));
        }
        double forAll = logic.forAll(result);
        double exists = logic.exist(result);
        this.result.setForAll(forAll);
        this.result.setExists(exists);
        predicate.setFitness(forAll);
        this.result.setPredicate(predicate);
    }

    private Double fitValue(AElement node, int index) {
        if (node == null) {
            throw new JFuzzyLogicAlgorithmError(String.format("The element to be evaluated cannot be null, in the predicate %s",
                    predicate));
        }
        List<Double> values = new ArrayList<>();
        if (node instanceof And) {
            ((And) node).forEach(n -> values.add(fitValue(n, index)));
            return logic.and(values);
        } else if (node instanceof Or) {
            ((Or) node).forEach(n -> values.add(fitValue(n, index)));
            return logic.or(values);
        } else if (node instanceof Imp) {
            Imp imp = (Imp) node;
            return logic.imp(fitValue(imp.getAntecedent(), index), fitValue(imp.getConsequent(), index));
        } else if (node instanceof Eqv) {
            Eqv eqv = (Eqv) node;
            Iterator<AElement> iterator = eqv.iterator();
            return logic.eqv(fitValue(iterator.next(), index), fitValue(iterator.next(), index));
        } else if (node instanceof Not) {
            return logic.not(fitValue(((Not) node).iterator().next(), index));
        } else if (node instanceof State) {
            return data.get(((State) node).getUuid()).get(index);
        } else {
            log.error("FitValue for {} at {}", node, predicate);
            throw new JFuzzyLogicAlgorithmError(JFuzzyLogicAlgorithmError.UNSUPPORTED + " " + node + " from " + predicate
                    + " at evaluate:fitValue");
        }
    }

    private void fuzzyData() {
        this.data = new HashMap<>();
        ArrayList<State> states = OperatorUtil.getNodesByClass(predicate, State.class);

        for (State state : states) {
            if (!table.containsColumn(state.getColName())) {
                throw new JFuzzyLogicAlgorithmError(String.format("Column %s does not exist for the state %s",
                        state.getColName(), state.toString()));
            }

            List<Double> fuzzy = data.getOrDefault(state.getUuid(), new ArrayList<>(table.rowCount()));
            ColumnType type = table.column(state.getColName()).type();
            if (numeriColumnTypes.contains(type)) {
                @SuppressWarnings("rawtypes")
                NumericColumn column = table.numberColumn(state.getColName());
                for (int i = 0; i < column.size(); i++) {
                    fuzzy.add(state.getMembershipFunction().eval(column.getDouble(i)));
                }
            } else {
                Column<?> column = table.column(state.getColName());
                for (int i = 0; i < column.size(); i++) {
                    fuzzy.add(state.getMembershipFunction().eval(column.getString(i)));
                }
            }
            data.putIfAbsent(state.getUuid(), fuzzy);
        }
    }

    @Override
    public EvaluationResult getResult() {
        return result;
    }

}
