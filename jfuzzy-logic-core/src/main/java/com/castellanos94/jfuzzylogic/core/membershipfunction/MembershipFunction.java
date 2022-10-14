package com.castellanos94.jfuzzylogic.core.membershipfunction;

import com.castellanos94.jfuzzylogic.core.base.JFuzzyLogicError;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.FPG;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.GBell;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.Gamma;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.Gaussian;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.LGamma;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.LTrapezoidal;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.NSigmoid;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.Nominal;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.PseudoExp;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.RTrapezoidal;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.SForm;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.Sigmoid;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.Singleton;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.Trapezoidal;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.Triangular;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.ZForm;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Abstract class representing a membership function definition
 * 
 * @version 1.0
 */
@JsonTypeInfo(use = Id.NAME)
@JsonSubTypes(value = {
        @Type(value = FPG.class, names = { "fpg", "FPG" }),
        @Type(value = Gamma.class, names = { "gamma", "Gamma", "GAMMA" }),
        @Type(value = Gaussian.class, names = { "gaussian", "Gaussian", "GAUSSIAN" }),
        @Type(value = GBell.class, names = { "gbell", "GBELL", "GBell" }),
        @Type(value = LGamma.class, names = { "lgamma", "LGamma", "LGAMMA" }),
        @Type(value = LTrapezoidal.class, names = { "ltrapezoidal", "LTrapezoidal", "LTRAPEZOIDAL" }),
        @Type(value = Nominal.class, names = { "nominal", "Nominal", "NOMINAL" }),
        @Type(value = NSigmoid.class, names = { "nsigmoid", "-sigmoid", "NSigmoid", "-SIGMOID" }),
        @Type(value = PseudoExp.class, names = { "pseudoexp", "PseudoExp", "PSeudoExp" }),
        @Type(value = RTrapezoidal.class, names = { "rtrapezoidal", "RTrapezoidal", "RTRAPEZOIDAL" }),
        @Type(value = SForm.class, names = { "sform", "SForm", "SFORM" }),
        @Type(value = Sigmoid.class, names = { "sigmoid", "SIGMOID" }),
        @Type(value = Singleton.class, names = { "singleton", "Singleton" }),
        @Type(value = Trapezoidal.class, names = { "Trapezoidal", "trapezoidal", "TRAPEZOIDAL" }),
        @Type(value = Triangular.class, names = { "triangular", "Triangular", "TRIANGULAR" }),
        @Type(value = ZForm.class, names = { "zform", "ZForm", "ZFORM" }),
})
@Getter
@Setter
@ToString
@EqualsAndHashCode
public abstract class MembershipFunction {
    protected boolean editable;

    public double eval(Number value) {
        throw new JFuzzyLogicError(JFuzzyLogicError.UNSUPPORTED + " number values -> " + getClass().getSimpleName());
    }

    public double eval(String value) {
        throw new JFuzzyLogicError(JFuzzyLogicError.UNSUPPORTED + " string values -> " + getClass().getSimpleName());
    }

    @JsonIgnore
    public abstract boolean isValid();

    @JsonIgnore
    public abstract MembershipFunction copy();

    /**
     * method for assigning properties to a sorted array
     * 
     * @return array values
     */
    @JsonIgnore
    public Double[] toArray() {
        throw new JFuzzyLogicError(JFuzzyLogicError.UNSUPPORTED + getClass().getSimpleName());
    }
}
