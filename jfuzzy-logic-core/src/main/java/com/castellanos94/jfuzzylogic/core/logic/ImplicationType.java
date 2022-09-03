package com.castellanos94.jfuzzylogic.core.logic;

/**
 * Enumeration for implication types
 * 
 * @version 1.0.0
 */
public enum ImplicationType {
    /**
     * S-Implication {@code x -> y = d(n(x),y)}
     */
    Natural,
    /**
     * QL-Implication {@code x -> y = d(n(x),c(x,y))}
     */
    Zadeh,
    /**
     * Reichenbach (S-implication) {@code x -> y = 1 - x + x * y}
     */
    Reichenbach,
    /**
     * Klir-Yuan implication (a variation of Reichenbach without a classification) {@code x -> y = 1 - x + x^2 * y}
     */
    KlirYuan,
    /**
     * A-Implication {@code x -> y = y^x}
     */
    Yager;

    public static ImplicationType searchEnum(String value) {
        for (ImplicationType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
