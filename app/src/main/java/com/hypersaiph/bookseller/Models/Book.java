package com.hypersaiph.bookseller.Models;

import android.util.Log;

import java.util.ArrayList;

public class Book {
    private String title;
    private String description;
    private int book_id;
    private int edition;
    private String publication_date;
    private String cover_image;
    private ArrayList<String> authors;
    private ArrayList<String> genres;
    private ArrayList<BookType> types;
    private String Language;

    public Book(String title, String description, int book_id, int edition, String publication_date, String cover_image, ArrayList<String> authors, ArrayList<String> genres, ArrayList<BookType> types) {
        this.title = title;
        this.description = description;
        this.book_id = book_id;
        this.edition = edition;
        this.publication_date = publication_date;
        this.cover_image = cover_image;
        this.authors = authors;
        this.genres = genres;
        this.types = types;
    }

    public Book(String title, int book_id) {
        this.title = title;
        this.book_id = book_id;
    }

    public Book(String title, int book_id, String language) {
        this.title = title;
        this.book_id = book_id;
        this.Language = language;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public int getEdition() {
        return edition;
    }

    public void setEdition(int edition) {
        this.edition = edition;
    }

    public String getPublication_date() {
        return publication_date;
    }

    public void setPublication_date(String publication_date) {
        this.publication_date = publication_date;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<String> authors) {
        this.authors = authors;
    }
    public String getAuthorsString() {
        String authors = "";
        for(int i=0; i<this.getAuthors().size(); i++){
            authors = authors.concat(this.getAuthors().get(i) + ", ");
        }
        authors = deleteLastChar(authors.trim());
        return authors;
    }
    public ArrayList<String> getGenres() {
        return genres;
    }
    public String getGenresString() {
        String genres = "";
        for(int i=0; i<this.getGenres().size(); i++){
            genres = genres.concat(this.getGenres().get(i) + ", ");
        }
        genres = deleteLastChar(genres.trim());
        return genres;
    }
    public String deleteLastChar(String str) {
        if (str != null && str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }
    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public ArrayList<BookType> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<BookType> types) {
        this.types = types;
    }
    public String getTypesString() {
        String types = "";
        for(int i=0; i<this.getTypes().size(); i++){
            types = types.concat(this.getTypes().get(i).getType() + ", ");
        }
        types = deleteLastChar(types.trim());
        return types;
    }
    public String getPublishersString() {
        ArrayList<String> publishers_a = new ArrayList<String>();
        ArrayList<String> publishers_b = new ArrayList<String>();
        String publishers = "";
        for(int i=0; i<this.getTypes().size(); i++){
            publishers_a.addAll(this.getTypes().get(i).getPublishers());
        }
        for(int i=0; i<publishers_a.size();i++){
            if(!publishers_b.contains(publishers_a.get(i))){
                publishers_b.add(publishers_a.get(i));
            }
        }
        for(int i=0; i<publishers_b.size(); i++){
            publishers = publishers.concat(publishers_b.get(i) + ", ");
        }
        publishers = deleteLastChar(publishers.trim());
        return publishers;
    }
}
