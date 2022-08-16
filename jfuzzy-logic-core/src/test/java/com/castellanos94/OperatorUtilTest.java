package com.castellanos94;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.castellanos94.jfuzzylogic.core.OperatorUtil;
import com.castellanos94.jfuzzylogic.core.base.AElement;
import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.impl.Eqv;
import com.castellanos94.jfuzzylogic.core.base.impl.Not;
import com.castellanos94.jfuzzylogic.core.base.impl.Or;
import com.castellanos94.jfuzzylogic.core.base.impl.State;

public class OperatorUtilTest {
    /**
     * Testing {@link OperatorUtil#replace(Operator, AElement, AElement)}
     * Testing {@link OperatorUtil#getNodesByClass(Operator, Class)}
     */
    @Test
    public void replaceState() {
        State alcohol = new State("alcohol", "acohol");
        State q = new State("quality", "quality");
        State a = new State("acidity", "acidity");
        Eqv eqv = new Eqv(alcohol, new Not(new Or(q, a)));
        System.out.println(eqv);
        State c = new State("citrix", "citrix");
        Operator n = OperatorUtil.replace(eqv, alcohol, c);
        System.out.println(n);
        assertEquals(0, OperatorUtil.getNodesByClass(n, AElement.class).stream().filter(e -> e.equals(alcohol)).count());
    }

    @Test
    public void replaceOperator() {
        State alcohol = new State("alcohol", "acohol");
        State q = new State("quality", "quality");
        State a = new State("acidity", "acidity");
        Or or = new Or(q, a);
        Eqv eqv = new Eqv(alcohol, new Not(or));
        System.out.println(eqv);
        State c = new State("citrix", "citrix");
        Operator n = OperatorUtil.replace(eqv, or, c);
        System.out.println(n);
        assertEquals(0, OperatorUtil.getNodesByClass(n, AElement.class).stream().filter(e -> e.equals(or)).count());
        assertNotEquals(eqv, n);
    }
}
