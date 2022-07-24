package com.castellanos94.jfuzzylogic.core;

/**
 * Thrown to indicate that the method has encountered a controlled error.
 * 
 * @version 1.0.0
 */
public class JFuzzyLogicError extends RuntimeException {
    public static String UNSUPPORTED = "Unsupported operation for ";

    public JFuzzyLogicError(String message) {
        super(message);
    }
}
