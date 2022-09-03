package com.castellanos94.jfuzzylogic.algorithm;

import java.util.Arrays;

import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;

/**
 * Auxiliary class that represents a solution in the algorithm. Each allele
 * belonging to the chromosome represents a membership function to be
 * discovered/optimized.
 */
public class MembershipFunctionChromosome implements Comparable<MembershipFunctionChromosome> {
    protected Double fitness;
    protected String[] id;
    protected MembershipFunction[] functions;

    public MembershipFunctionChromosome(int size) {
        this.id = new String[size];
        this.functions = new MembershipFunction[size];
    }

    protected MembershipFunctionChromosome(MembershipFunctionChromosome chromosome) {
        this(chromosome.id.length);
        this.fitness = chromosome.getFitness();
        System.arraycopy(chromosome.id, 0, this.id, 0, chromosome.id.length);
        for (int i = 0; i < functions.length; i++) {
            setFunction(i, chromosome.getFunction(i).copy());
        }
    }

    public MembershipFunction getFunction(String uuid) {
        if (uuid == null)
            return null;
        for (int i = 0; i < functions.length; i++) {
            if (id[i].equals(uuid)) {
                return functions[i];
            }
        }
        return null;
    }

    public MembershipFunction getFunction(int index) {
        return this.functions[index];
    }

    public void setFunction(int index, MembershipFunction value) {
        this.functions[index] = value;
    }

    public String getId(int index) {
        return this.id[index];
    }

    public void setId(int index, String value) {
        this.id[index] = value;
    }

    public Double getFitness() {
        return fitness;
    }

    public void setFitness(Double fitness) {
        this.fitness = fitness;
    }

    @Override
    public int compareTo(MembershipFunctionChromosome o) {
        return Double.compare(this.fitness, o.fitness);
    }

    public MembershipFunctionChromosome copy() {
        return new MembershipFunctionChromosome(this);
    }

    @Override
    public String toString() {
        return "MembershipFunctionChromosome [fitness=" + fitness + ", functions=" + Arrays.toString(functions) + "]";
    }
    
}
