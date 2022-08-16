package com.castellanos94;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.castellanos94.jfuzzylogic.core.base.OperatorType;
import com.castellanos94.jfuzzylogic.core.base.impl.Generator;
import com.castellanos94.jfuzzylogic.core.base.impl.State;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GeneratorTest {
    @Test
    public void operatrSerialization() throws JsonProcessingException {
        State alcohol = new State("alcohol", "acohol");
        State q = new State("quality", "quality");
        Generator generator = new Generator();
        generator.setLabel("mi generador");
        generator.setDepth(3);
        generator.setMaxChild(10);
        generator.add(alcohol, q);
        generator.add(OperatorType.OR, OperatorType.AND, OperatorType.EQV);
        System.out.println(generator);
        ObjectMapper mapper = new ObjectMapper();
        String str = mapper.writeValueAsString(generator);
        System.out.println(str);
        Generator g = mapper.readValue(str, Generator.class);
        assertEquals(generator, g);
    }
}
