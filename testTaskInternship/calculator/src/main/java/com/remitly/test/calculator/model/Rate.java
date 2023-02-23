package com.remitly.test.calculator.model;

public class Rate {
    private final String no;
    private final String effectiveDate;
    private final double mid;

    public Rate(){
        this("","", 0.0);
    }

    public Rate(String no, String effectiveDate, double mid) {
        this.no = no;
        this.effectiveDate = effectiveDate;
        this.mid = mid;
    }

    public double getMid() {
        return mid;
    }

    @Override
    public String toString() {
        return "Rate{" +
                "no='" + no + '\'' +
                ", effectiveDate='" + effectiveDate + '\'' +
                ", mid=" + mid +
                '}';
    }
}
