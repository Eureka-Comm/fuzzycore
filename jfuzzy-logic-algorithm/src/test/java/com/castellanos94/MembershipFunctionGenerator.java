package com.castellanos94;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.castellanos94.jfuzzylogic.algorithm.impl.FPGGenerator;
import com.castellanos94.jfuzzylogic.algorithm.impl.Utils;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.FPG;

public class MembershipFunctionGenerator {
    @Test
    public void fpgBoundaries() {
        FPGGenerator generator = new FPGGenerator();
        FPG[] boundary = generator.generateBoundaries(10., 5., 20.);
        System.out.println("lower: " + boundary[0]);
        System.out.println("upper: " + boundary[1]);
        assertEquals(10.0, boundary[0].getBeta(), Utils.DELTA);
        assertEquals(10.0, boundary[0].getGamma(), Utils.DELTA);
        assertEquals(1, 1, Utils.DELTA);

        assertEquals(20.0, boundary[1].getBeta(), Utils.DELTA);
        assertEquals(20.0, boundary[1].getGamma(), Utils.DELTA);
        assertEquals(1, 1, Utils.DELTA);
    }

    @Test
    public void generateFPG() {
        FPGGenerator generator = new FPGGenerator();
        FPG[] boundary = generator.generateBoundaries(10., 5., 20.);
        System.out.println("lower: " + boundary[0]);
        System.out.println("upper: " + boundary[1]);
        FPG fpg = generator.generate(boundary[0], boundary[1], null);
        System.out.println("Random generation: " + fpg);
        assertNotEquals(fpg.getBeta(), fpg.getGamma(), Utils.DELTA);
        assertNotEquals(1, fpg.getM(), Utils.DELTA);
    }

}
