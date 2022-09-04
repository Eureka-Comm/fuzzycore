package com.castellanos94;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.castellanos94.jfuzzylogic.core.base.JFuzzyLogicError;
import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.impl.And;
import com.castellanos94.jfuzzylogic.core.base.impl.Eqv;
import com.castellanos94.jfuzzylogic.core.base.impl.Imp;
import com.castellanos94.jfuzzylogic.core.base.impl.Not;
import com.castellanos94.jfuzzylogic.core.base.impl.Or;
import com.castellanos94.jfuzzylogic.core.base.impl.State;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OperatorTest {
   

    @Test
    public void toStringOperator() {
        State alcohol = new State("alcohol", "acohol");
        State q = new State("quality", "quality");
        And and = new And(alcohol, q);
        assertEquals("(AND \"alcohol\" \"quality\")", and.toString());

        Or or = new Or(alcohol, q);
        assertEquals("(OR \"alcohol\" \"quality\")", or.toString());

        Not not = new Not(alcohol);
        assertEquals("(NOT \"alcohol\")", not.toString());

        Imp imp = new Imp(alcohol, q);
        assertEquals("(IMP \"alcohol\" \"quality\")", imp.toString());

        Eqv eqv = new Eqv(alcohol, q);
        assertEquals("(EQV \"alcohol\" \"quality\")", eqv.toString());
    }

    @Test(expected = JFuzzyLogicError.class)
    public void notMaxArity() {
        State alcohol = new State("alcohol", "acohol");
        State q = new State("quality", "quality");
        Not not = new Not(alcohol);
        not.add(q);
    }

    @Test(expected = JFuzzyLogicError.class)
    public void impMaxArity() {
        State alcohol = new State("alcohol", "acohol");
        State q = new State("quality", "quality");
        State a = new State("acidity", "acidity");
        Imp imp = new Imp();
        imp.add(alcohol);
        imp.add(q);
        System.out.println(imp);
        imp.setAntecedent(new Or());
        System.out.println(imp);
        imp.setConsequent(alcohol);
        System.out.println(imp);
        imp.add(a);
    }

    @Test(expected = JFuzzyLogicError.class)
    public void eqvMaxArity() {
        State alcohol = new State("alcohol", "acohol");
        State q = new State("quality", "quality");
        State a = new State("acidity", "acidity");
        Eqv eqv = new Eqv();
        eqv.add(alcohol);
        eqv.add(q);
        eqv.add(a);
    }

    @Test(expected = NullPointerException.class)
    public void notNullCondition() {
        State alcohol = new State("alcohol", "acohol");
        State q = new State("quality", "quality");
        State a = new State("acidity", "acidity");
        Eqv eqv = new Eqv();
        eqv.add(alcohol);
        eqv.add(null);
    }

    @Test
    public void operatorEquals() {
        State alcohol = new State("alcohol", "acohol");
        State q = new State("quality", "quality");
        State a = new State("acidity", "acidity");
        Eqv eqv = new Eqv();
        eqv.add(alcohol);
        eqv.add(q);
        Eqv eqv2 = new Eqv(alcohol, q);
        assertNotEquals(eqv, eqv2);

        assertEquals(eqv, eqv.copy());
    }

    @Test
    public void operatrSerialization() throws JsonProcessingException {
        State alcohol = new State("alcohol", "acohol");
        State q = new State("quality", "quality");
        And and = new And(alcohol, q);

        ObjectMapper mapper = new ObjectMapper();
        String str = mapper.writeValueAsString(and);
        Operator operator = mapper.readValue(
                str,
                Operator.class);
        assertEquals(and, operator);
    }
}
