package com.castellanos94;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import org.junit.Test;

import com.castellanos94.jfuzzylogic.core.OperatorUtil;
import com.castellanos94.jfuzzylogic.core.base.AElement;
import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.impl.And;
import com.castellanos94.jfuzzylogic.core.base.impl.Eqv;
import com.castellanos94.jfuzzylogic.core.base.impl.Imp;
import com.castellanos94.jfuzzylogic.core.base.impl.Not;
import com.castellanos94.jfuzzylogic.core.base.impl.Or;
import com.castellanos94.jfuzzylogic.core.base.impl.State;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.FPG;

public class OperatorUtilTest {
    @Test
    public void getMaxDept(){
        And and = new And();
        int level = OperatorUtil.getMaximumLeafLevel(and);
        and.add(new State("a"));
        and.add(new Not(new State("b")));
        and.add(new Or(new State("c"), new State("d"), new Not(new Imp(new State("e"), new Not(new State("f"))))));
        and.add(new Not());
        and.add(new Not());

        System.out.println(and);

        level = OperatorUtil.getMaximumLeafLevel(and);
        System.out.println(level);
        Operator tmp = OperatorUtil.getRoot(and, new State("e"));
        System.out.println(tmp);
        level = OperatorUtil.getMaximumLeafLevel(tmp);
        System.out.println(level);
    }
    @Test
    public void checkValidOperators() {
        And and = new And();
        assertEquals(false, OperatorUtil.isValid(and));
        State alcohol = new State("alcohol", "acohol");
        and.add(alcohol);
        assertEquals(false, OperatorUtil.isValid(and));
        State q = new State("quality", "quality", new FPG());
        and.add(q);
        System.out.println(and);
        assertEquals(true, OperatorUtil.isValid(and));
        assertEquals(false, OperatorUtil.isValid(and,true));
        assertEquals(true, OperatorUtil.isValid(and,false));
        State d = new State("d", "d", new FPG(0.5,0.5,0.5));
        and.add(d);
        System.out.println(and);
        assertEquals(false, OperatorUtil.isValid(and,true));
        d = new State("d", "d", new FPG(0.5,0.7,0.5));
        and.add(d);
        System.out.println(and);
        assertEquals(false, OperatorUtil.isValid(and,true));
        Not not = new Not(d);
        assertEquals(true, OperatorUtil.isValid(not));
        assertEquals(true, OperatorUtil.isValid(not,true));
        Imp imp = new Imp(d, not);
        assertEquals(true, OperatorUtil.isValid(imp, true));
        Eqv eqv = new Eqv(alcohol, imp);
        assertEquals(false, OperatorUtil.isValid(eqv, true));
        assertEquals(true, OperatorUtil.isValid(eqv));
    }

    @Test
    public void checkValidStates() {
        State alcohol = new State("alcohol", "acohol");
        State q = new State("quality", "quality", new FPG());
        State d = new State("d", "d", new FPG(0.5,0.5,0.5));
        assertEquals(false, OperatorUtil.isValid(null));
        assertEquals(true, OperatorUtil.isValid(alcohol));
        assertEquals(false, OperatorUtil.isValid(q, true));
        assertEquals(true, OperatorUtil.isValid(d));
        assertEquals(false, OperatorUtil.isValid(d, true));
    }
    @Test
    public void dfs() {
        State a = new State("a");
        State b = new State("b");
        State c = new State("c");
        State d = new State("d");
        Not nd = new Not(d);
        Imp root = new Imp(new And(a, b, nd), c);
        System.out.println(root);
        assertEquals(-1, OperatorUtil.dfs(root, new Eqv()));
        assertEquals(1, OperatorUtil.dfs(root, root));
        assertEquals(2, OperatorUtil.dfs(root, b));
        assertEquals(2, OperatorUtil.dfs(root, nd));
        assertEquals(3, OperatorUtil.dfs(root, d));
    }

    @Test
    public void replaceAll() {
        State a = new State("a");
        State b = new State("b");
        State c = new State("c");
        And and = new And(a, b, c, new Not(c));
        ArrayList<State> states = OperatorUtil.getNodesByClass(and, State.class);
        System.out.println(states);
        System.out.println(and);
        assertEquals(0, states.stream().filter(s -> s.equals(new State("d"))).count());
        Operator replace = OperatorUtil.replace(and, c, new State("d"));
        System.out.println(replace);
        ArrayList<State> replaceState = OperatorUtil.getNodesByClass(replace, State.class);
        System.out.println(replaceState);
        assertEquals(0, replaceState.stream().filter(s -> s.equals(c)).count());
    }

    @Test
    public void getParent() {
        State a = new State("a");        
        State c = new State("c");
        State d = new State("d");
        Not nc = new Not(c);
        Or or = new Or(a, nc);
        Not not = new Not(or);
        Operator root = OperatorUtil.getRoot(not, c);
        assertEquals(nc, root);
        // replace child c -> d
        Operator replace = OperatorUtil.replace(not, c, d);
        System.out.println(not);
        System.out.println(replace);
        Operator expectedNull = OperatorUtil.getRoot(replace, c);
        assertNull(expectedNull);

    }

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
        assertEquals(0,
                OperatorUtil.getNodesByClass(n, AElement.class).stream().filter(e -> e.equals(alcohol)).count());
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
