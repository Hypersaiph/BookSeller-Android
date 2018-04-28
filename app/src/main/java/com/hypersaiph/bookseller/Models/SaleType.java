package com.hypersaiph.bookseller.Models;

public class SaleType {
    private String type;
    private int sale_type_id;

    public SaleType(String type, int sale_type_id) {
        this.type = type;
        this.sale_type_id = sale_type_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSale_type_id() {
        return sale_type_id;
    }

    public void setSale_type_id(int sale_type_id) {
        this.sale_type_id = sale_type_id;
    }
}
