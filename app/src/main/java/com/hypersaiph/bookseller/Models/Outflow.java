package com.hypersaiph.bookseller.Models;

public class Outflow {
    private int quantity;
    private Double selling_price;
    private BookType bookType;
    private Book book;

    public Outflow(int quantity, Double selling_price, BookType bookType, Book book) {
        this.quantity = quantity;
        this.selling_price = selling_price;
        this.bookType = bookType;
        this.book = book;
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

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
