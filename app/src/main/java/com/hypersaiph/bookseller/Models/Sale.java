package com.hypersaiph.bookseller.Models;

import java.util.ArrayList;

public class Sale {
    private String code;
    private boolean is_billed;
    private int months;
    private Double anual_interest;
    private int sale_id;
    private SaleType saleType;
    private Customer customer;
    private ArrayList<Account> accounts;
    private ArrayList<Outflow> outflows;

    public Sale(String code, boolean is_billed, int months, Double anual_interest, int sale_id, SaleType saleType, Customer customer, ArrayList<Account> accounts, ArrayList<Outflow> outflows) {
        this.code = code;
        this.is_billed = is_billed;
        this.months = months;
        this.anual_interest = anual_interest;
        this.sale_id = sale_id;
        this.saleType = saleType;
        this.customer = customer;
        this.accounts = accounts;
        this.outflows = outflows;
    }

    public Sale(int months, Double anual_interest, Customer customer, SaleType saleType, ArrayList<Outflow> outflows) {
        this.months = months;
        this.anual_interest = anual_interest;
        this.customer = customer;
        this.saleType = saleType;
        this.outflows = outflows;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isIs_billed() {
        return is_billed;
    }

    public void setIs_billed(boolean is_billed) {
        this.is_billed = is_billed;
    }

    public int getMonths() {
        return months;
    }

    public void setMonths(int months) {
        this.months = months;
    }

    public Double getAnual_interest() {
        return anual_interest;
    }

    public void setAnual_interest(Double anual_interest) {
        this.anual_interest = anual_interest;
    }

    public int getSale_id() {
        return sale_id;
    }

    public void setSale_id(int sale_id) {
        this.sale_id = sale_id;
    }

    public SaleType getSaleType() {
        return saleType;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setSaleType(SaleType saleType) {
        this.saleType = saleType;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    public ArrayList<Outflow> getOutflows() {
        return outflows;
    }

    public void setOutflows(ArrayList<Outflow> outflows) {
        this.outflows = outflows;
    }
    public String getAmountPerAccount(){
        Double payment = 0.0;
        for(int i=0; i<this.getAccounts().size();i++){
            payment = this.getAccounts().get(i).getAmount();
            break;
        }
        return "Pago: "+payment+" Bs.";
    }
    public String getRemainingAccounts(){
        int c = 0;
        for(int i=0;i<this.getAccounts().size();i++){
            if(this.getAccounts().get(i).isIs_active()){
                c++;
            }
        }
        return "Restantes: "+c;
    }
    public Double getTotal(){
        Double total = 0.0;
        for(int i=0;i<this.getAccounts().size();i++){
            total += (this.getAccounts().get(i).getAmount() + this.getAccounts().get(i).getPenalty());
        }
        return total;
    }
    public boolean hasInterest(){
        for(int i=0;i<this.getAccounts().size();i++){
            if(this.getAccounts().get(i).getPenalty() != 0){
                return true;
            }
        }
        return false;
    }
    public int getProductQuantity(){
        return this.getOutflows().size();
    }
    public String getNextPaymentDate(){
        for (int i=0; i<this.getAccounts().size();i++){
            if(this.getAccounts().get(i).isIs_active() && this.getAccounts().get(i).getPenalty()==0){
                return this.getAccounts().get(i).getPayment_date();
            }
        }
        return "";
    }
    public boolean getStatus(){
        for(int i=0;i<this.getAccounts().size();i++){
            if(this.getAccounts().get(i).isIs_active()){
                return true;
            }
        }
        return false;
    }
    public boolean hasAtLeastOnePaymentCompleted(){
        for (int i=0; i<this.getAccounts().size();i++){
            if(!this.getAccounts().get(i).isIs_active())
                return true;
        }
        return false;
    }
}
