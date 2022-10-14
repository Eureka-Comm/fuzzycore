package com.castellanos94.jfuzzylogic.core.membershipfunction.impl;

import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * The class {@code GBELL} is Generalized Bell function fuzzy membership
 * generator.
 * 
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class GBell extends MembershipFunction {
    @EqualsAndHashCode.Include
    protected Double width;
    @EqualsAndHashCode.Include
    protected Double slope;
    @EqualsAndHashCode.Include
    protected Double center;

    /**
     * Parameters
     * 
     * @param width  Double Bell function parameter controlling width. See Note for
     *               definition.
     * @param slope  Double Bell function parameter controlling slope. See Note for
     *               definition.
     * @param center Double Bell function parameter defining the center. See Note
     *               for definition.
     */
    public GBell(Double width, Double slope, Double center) {
        this(width, slope, center, false);
    }

    public GBell() {
    }

    public GBell(Double width, Double slope, Double center, boolean editable) {
        this.width = width;
        this.slope = slope;
        this.center = center;
        this.editable = editable;
    }

    @Override
    public boolean isValid() {
        return !(width == null || slope == null || center == null);
    }

    @Override
    public double eval(Number value) {
        Double v = value.doubleValue();
        return 1. / (1. + Math.pow(Math.abs((v - center) / width), (2 * slope)));
    }

    @Override
    public GBell copy() {
        return new GBell(width, slope, center, editable);
    }

    @Override
    public Double[] toArray() {
        return new Double[] { width, slope, center };
    }

    @Override
    public String toString() {
        return "GBell [width=" + width + ", slope=" + slope + ", center=" + center + "]";
    }

}
