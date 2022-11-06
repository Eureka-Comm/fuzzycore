package com.castellanos94.jfuzzylogic.core.membershipfunction.impl;

import java.util.HashMap;

import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MapNominal extends MembershipFunction {

    @EqualsAndHashCode.Include
    protected HashMap<String, Double> values = new HashMap<>();
    @EqualsAndHashCode.Include
    protected Double notFoundValue = 0.0;

    public void add(String key, double value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException("Value must be in [0,1].");
        }
        this.values.put(key.trim(), value);
    }

    @Override
    public boolean isValid() {
        return notFoundValue != null && notFoundValue >= 0.0 && notFoundValue <= 1.0;
    }

    @Override
    public MapNominal copy() {
        MapNominal mp = new MapNominal();
        this.values.forEach((k, v) -> mp.add(k, v));
        mp.setNotFoundValue(this.notFoundValue);
        mp.setEditable(this.editable);
        return mp;
    }

    @Override
    public double eval(String value) {
        if (this.values.containsKey(value.trim())) {
            return this.values.get(value);
        }
        return notFoundValue;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[map-nominal {");
        for (String kString : values.keySet()) {
            builder.append(String.format("\"%s\" : %d,", kString, values.get(kString)));
        }
        String st = builder.toString();
        return "[map-nominal {" + st.toString().substring(0, st.lastIndexOf(",")) + "} " + notFoundValue + "]";
    }
}
