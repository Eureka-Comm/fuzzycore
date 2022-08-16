package com.castellanos94;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.castellanos94.jfuzzylogic.core.base.impl.State;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.FPG;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StateTest {
    @Test
    public void simpleState() throws JsonProcessingException{
        State state = new State("alcohol","alcohol");

        ObjectMapper mapper = new ObjectMapper();
        String str = mapper.writeValueAsString(state);
        State read = mapper.readValue(str, State.class);
        System.out.println(str);
        assertEquals(state, read);
    }
    @Test
    public void notTheSameState() throws JsonProcessingException{
        State state = new State("alcohol","alcohol");

        ObjectMapper mapper = new ObjectMapper();
        State read = mapper.readValue("{\"@type\":\"STATE\",\"label\":\"alcohol\",\"description\":null,\"editable\":true,\"colName\":\"alcohol\",\"membershipFunction\":null}", State.class);
        assertNotEquals(state, read);
    }

    @Test
    public void copyStateWithoutFunction() throws JsonProcessingException{
        State state = new State("alcohol","alcohol");        
        assertEquals(state, state.copy());
    }

    @Test
    public void copyStateWithFunction() throws JsonProcessingException{
        State state = new State("alcohol","alcohol", new FPG(3., 5., 1.));     
        System.out.println(state);   
        assertEquals(state, state.copy());
    }
}
