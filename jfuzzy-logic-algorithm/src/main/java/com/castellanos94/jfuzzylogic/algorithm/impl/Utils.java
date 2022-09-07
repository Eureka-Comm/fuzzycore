package com.castellanos94.jfuzzylogic.algorithm.impl;

import java.util.Random;

public class Utils {
    public static final double DELTA = 0.00001;

    /**
     * Generate a random number between lower and upper
     * 
     * @param random number generator
     * @param lower limit
     * @param upper limit
     * @return random number
     */
    public static double randomNumber(Random random, double lower, double upper) {
        if (equals(lower, upper)) {
            return lower;
        }
        return random.doubles(lower, upper).findAny().getAsDouble();
    }

    /**
     * Generate a random number [0,1]
     * 
     * @param random number generator
     * @return number
     */
    public static double randomNumber(Random random) {
        return random.nextInt(1001) / 1001.0;
    }

    /**
     * Equals with delta tolerance
     * 
     * @param a value
     * @param b value
     * @return true if are equals, otherwise, false
     */
    public static boolean equals(double a, double b) {
        return Math.abs(a - b) <= Utils.DELTA;
    }
}
