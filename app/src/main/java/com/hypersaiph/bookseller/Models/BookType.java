package com.hypersaiph.bookseller.Models;

import java.util.ArrayList;

public class BookType {
    private String type;
    private int type_id;
    private Double price;
    private int pages;
    private String isbn10;
    private String isbn13;
    private String serial_cd;
    private String duration;
    private Double weight;
    private Double width;
    private Double height;
    private Double depth;
    private ArrayList<String> publishers;
    private Book book;

    public BookType(String type, int type_id, Double price, int pages, String isbn10, String isbn13, String serial_cd, String duration, Double weight, Double width, Double height, Double depth, ArrayList<String> publishers) {
        this.type = type;
        this.type_id = type_id;
        this.price = price;
        this.pages = pages;
        this.isbn10 = isbn10;
        this.isbn13 = isbn13;
        this.serial_cd = serial_cd;
        this.duration = duration;
        this.weight = weight;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.publishers = publishers;
    }

    public BookType(String type, int type_id, String isbn10, String isbn13, String serial_cd) {
        this.type = type;
        this.type_id = type_id;
        this.isbn10 = isbn10;
        this.isbn13 = isbn13;
        this.serial_cd = serial_cd;
    }

    public BookType(String type, int type_id, Double price, String isbn10, String isbn13, String serial_cd, Book book) {
        this.type = type;
        this.type_id = type_id;
        this.price = price;
        this.isbn10 = isbn10;
        this.isbn13 = isbn13;
        this.serial_cd = serial_cd;
        this.book = book;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public BookType(int type_id) {
        this.type_id = type_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getType_id() {
        return type_id;
    }

    public void setType_id(int type_id) {
        this.type_id = type_id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getIsbn10() {
        return isbn10;
    }

    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getSerial_cd() {
        return serial_cd;
    }

    public void setSerial_cd(String serial_cd) {
        this.serial_cd = serial_cd;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getDepth() {
        return depth;
    }

    public void setDepth(Double depth) {
        this.depth = depth;
    }

    public ArrayList<String> getPublishers() {
        return publishers;
    }

    public void setPublishers(ArrayList<String> publishers) {
        this.publishers = publishers;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }
}
