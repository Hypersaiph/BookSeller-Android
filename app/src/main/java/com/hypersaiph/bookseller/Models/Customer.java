package com.hypersaiph.bookseller.Models;

public class Customer {
    private String name;
    private String surname;
    private String nit;
    private String email;
    private String address;
    private String phone;
    private String note;
    private Double latitude;
    private Double longitude;
    private int customer_id;
    private int created_by;

    public Customer(String name, String surname, String nit, String email, String address, String phone, String note, Double latitude, Double longitude, int customer_id, int created_by) {
        this.name = name;
        this.surname = surname;
        this.nit = nit;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.note = note;
        this.latitude = latitude;
        this.longitude = longitude;
        this.customer_id = customer_id;
        this.created_by = created_by;
    }

    public Customer(String surname, String nit, int customer_id) {
        this.surname = surname;
        this.nit = nit;
        this.customer_id = customer_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }
}
