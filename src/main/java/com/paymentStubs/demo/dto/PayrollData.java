package com.paymentStubs.demo.dto;

import com.opencsv.bean.CsvBindByName;

// Mude de 'public record' para 'public class'
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

    // Construtor sem argumentos (obrigatório para o OpenCSV)
    public PayrollData() {
    }

    // Getters e Setters (necessários para o OpenCSV preencher os dados)
    // Você pode gerar todos eles automaticamente no seu IDE
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

    // ... adicione getters e setters para TODOS os outros campos ...

    public Double getNetPayment() {
        return getNetPayment();
    }

    public void setNetPayment(Double netPayment) {
        this.net_payment = netPayment;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return "PayrollData [fullName=" + fullName + ", email=" + email + ", position=" + position
                + ", healthDiscountAmount=" + healthDiscountAmount + ", socialDiscountAmount=" + socialDiscountAmount
                + ", taxesDiscountAmount=" + taxesDiscountAmount + ", otherDiscountAmount=" + otherDiscountAmount
                + ", grossSalary=" + grossSalary + ", grossPayment=" + grossPayment + ", netPayment=" + netPayment
                + ", period=" + period + ", net_payment=" + net_payment + "]";
    }
}