package com.paymentStubs.demo.dto;

import com.opencsv.bean.CsvBindByName;

public class PayrollData {

    @CsvBindByName(column = "full_name", required = true)
    private String fullName;

    @CsvBindByName(column = "email", required = true)
    private String email;

    @CsvBindByName(column = "position", required = true)
    private String position;

    @CsvBindByName(column = "health_discount_amount", required = true)
    private Double healthDiscountAmount;

    @CsvBindByName(column = "social_discount_amount", required = true)
    private Double socialDiscountAmount;

    @CsvBindByName(column = "taxes_discount_amount", required = true)
    private Double taxesDiscountAmount;

    @CsvBindByName(column = "other_discount_amount", required = true)
    private Double otherDiscountAmount;

    @CsvBindByName(column = "gross_salary", required = true)
    private Double grossSalary;

    @CsvBindByName(column = "gross_payment", required = true)
    private Double grossPayment;

    @CsvBindByName(column = "net_payment", required = true)
    private Double netPayment;

    @CsvBindByName(column = "period", required = true)
    private String period;

    private Double net_payment;

    public PayrollData() {
    }

    @Override
    public String toString() {
        return "PayrollData [fullName=" + fullName + ", email=" + email + ", position=" + position
                + ", healthDiscountAmount=" + healthDiscountAmount + ", socialDiscountAmount=" + socialDiscountAmount
                + ", taxesDiscountAmount=" + taxesDiscountAmount + ", otherDiscountAmount=" + otherDiscountAmount
                + ", grossSalary=" + grossSalary + ", grossPayment=" + grossPayment + ", netPayment=" + netPayment
                + ", period=" + period + ", net_payment=" + net_payment + "]";
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Double getHealthDiscountAmount() {
        return healthDiscountAmount;
    }

    public void setHealthDiscountAmount(Double healthDiscountAmount) {
        this.healthDiscountAmount = healthDiscountAmount;
    }

    public Double getSocialDiscountAmount() {
        return socialDiscountAmount;
    }

    public void setSocialDiscountAmount(Double socialDiscountAmount) {
        this.socialDiscountAmount = socialDiscountAmount;
    }

    public Double getTaxesDiscountAmount() {
        return taxesDiscountAmount;
    }

    public void setTaxesDiscountAmount(Double taxesDiscountAmount) {
        this.taxesDiscountAmount = taxesDiscountAmount;
    }

    public Double getOtherDiscountAmount() {
        return otherDiscountAmount;
    }

    public void setOtherDiscountAmount(Double otherDiscountAmount) {
        this.otherDiscountAmount = otherDiscountAmount;
    }

    public Double getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(Double grossSalary) {
        this.grossSalary = grossSalary;
    }

    public Double getGrossPayment() {
        return grossPayment;
    }

    public void setGrossPayment(Double grossPayment) {
        this.grossPayment = grossPayment;
    }

    public Double getNetPayment() {
        return netPayment;
    }

    public void setNetPayment(Double netPayment) {
        this.netPayment = netPayment;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Double getNet_payment() {
        return net_payment;
    }

    public void setNet_payment(Double net_payment) {
        this.net_payment = net_payment;
    }
}