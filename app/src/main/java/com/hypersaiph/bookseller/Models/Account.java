package com.hypersaiph.bookseller.Models;

public class Account {
    private String code;
    private Double amount;
    private Double penalty;
    private String payment_date;
    private String limit_payment_date;
    private boolean is_active;
    private int account_id;

    public Account(String code, Double amount, Double penalty, String payment_date, String limit_payment_date, boolean is_active, int account_id) {
        this.code = code;
        this.amount = amount;
        this.penalty = penalty;
        this.payment_date = payment_date;
        this.limit_payment_date = limit_payment_date;
        this.is_active = is_active;
        this.account_id = account_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getPenalty() {
        return penalty;
    }

    public void setPenalty(Double penalty) {
        this.penalty = penalty;
    }

    public String getPayment_date() {
        return payment_date;
    }

    public void setPayment_date(String payment_date) {
        this.payment_date = payment_date;
    }

    public String getLimit_payment_date() {
        return limit_payment_date;
    }

    public void setLimit_payment_date(String limit_payment_date) {
        this.limit_payment_date = limit_payment_date;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }
}
