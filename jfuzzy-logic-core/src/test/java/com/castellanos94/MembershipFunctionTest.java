package com.castellanos94;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.FPG;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MembershipFunctionTest {
    /**
     * Rigorous Test :-)
     * 
     * @throws JsonProcessingException
     */
    @Test
    public void fpgSerialization() throws JsonProcessingException {
        FPG fpg = new FPG(3.0, 5.0, 0.5);

        ObjectMapper mapper = new ObjectMapper();
        String str = mapper.writeValueAsString(fpg);
        System.out.println(str);
        MembershipFunction fpg2 = mapper.readValue(
                "{\"@type\":\"fpg\",\"editable\":false,\"beta\":3.0,\"gamma\":5.0,\"m\":0.5}",
                MembershipFunction.class);
        System.out.println(fpg2);
        assertEquals(fpg, fpg2);
    }
}
