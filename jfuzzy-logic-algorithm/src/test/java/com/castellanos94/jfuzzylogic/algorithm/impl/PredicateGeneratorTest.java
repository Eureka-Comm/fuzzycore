package com.castellanos94.jfuzzylogic.algorithm.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import com.castellanos94.jfuzzylogic.core.OperatorUtil;
import com.castellanos94.jfuzzylogic.core.base.AElement;
import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.OperatorType;
import com.castellanos94.jfuzzylogic.core.base.impl.Generator;
import com.castellanos94.jfuzzylogic.core.base.impl.Imp;
import com.castellanos94.jfuzzylogic.core.base.impl.State;

public class PredicateGeneratorTest {
    @Test
    public void testGenerate() {
        Generator generator = new Generator();
        generator.setLabel("mi generador");
        generator.setDepth(2);
        generator.add(new State("a"), new State("b"), new State("c"));
        generator.add(new State("d"), new State("d"));
        generator.add(OperatorType.IMP, OperatorType.EQV, OperatorType.AND);
        Generator orNot = new Generator();
        orNot.setLabel("disyuntivo");
        orNot.setDepth(2);
        orNot.add(new State("e"), new State("f"), new State("g"));
        orNot.add(OperatorType.OR, OperatorType.NOT);
        generator.add(orNot);
        Random random = new Random(1l);
        random.setSeed(1l);

        int size = 1_000_000;
        AElement element;
        for (int i = 0; i < size; i++) {
            element = PredicateGenerator.generate(random, null, generator, i < size / 2);
            assertEquals(true, OperatorUtil.isValid(element));
            assertEquals(true, element instanceof Operator);
        }
    }

    @Test
    public void generatePredicateWithGuide() {
        List<String> labels = Arrays.asList("b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l");
        Generator generator = new Generator();
        generator.setDepth(3);
        generator.setLabel("abecedario");
        generator.add(OperatorType.AND, OperatorType.OR, OperatorType.EQV, OperatorType.IMP, OperatorType.NOT);
        labels.forEach(l -> generator.add(new State(l)));
        State a = new State("a");
        Operator predicate = new Imp(generator, a);
        System.out.println(predicate);
        int size = 1_000_000;
        int count = 0;
        AElement element;

        Random random = new Random(1l);
        random.setSeed(1l);
        for (int i = 0; i < size; i++) {
            element = PredicateGenerator.generate(random, predicate, generator, i < size / 2);
            assertEquals(true, OperatorUtil.isValid(element));
            if (element instanceof State) {
                count++;
            }
        }
        assertEquals(true, count > 1);
        System.out.println(count);
    }

}
