package com.castellanos94.jfuzzylogic.algorithm.impl;

import java.util.Random;

public class Utils {
    public static final double DELTA = 0.00001;

    /**
     * Generate a random number between lower and upper
     * 
     * @param random
     * @param lower
     * @param upper
     * @return
     */
    public static double randomNumber(Random random, double lower, double upper) {
        return random.doubles(lower, upper).findAny().getAsDouble();
    }

    /**
     * Generate a random number [0,1]
     * 
     * @param random
     * @return
     */
    public static double randomNumber(Random random) {
        return random.nextInt(1001) / 1001.0;
    }

    /**
     * Equals with delta tolerance
     * 
     * @param a
     * @param b
     * @return true if are equals, otherwise, false
     */
    public static boolean equals(double a, double b) {
        return Math.abs(a - b) <= Utils.DELTA;
    }
}
