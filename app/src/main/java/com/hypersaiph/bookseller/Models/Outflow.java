package com.hypersaiph.bookseller.Models;

public class Outflow {
    private int quantity;
    private Double selling_price;
    private BookType bookType;

    public Outflow(int quantity, Double selling_price, BookType bookType) {
        this.quantity = quantity;
        this.selling_price = selling_price;
        this.bookType = bookType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getSelling_price() {
        return selling_price;
    }

    public void setSelling_price(Double selling_price) {
        this.selling_price = selling_price;
    }

    public BookType getBookType() {
        return bookType;
    }

    public void setBookType(BookType bookType) {
        this.bookType = bookType;
    }
}
