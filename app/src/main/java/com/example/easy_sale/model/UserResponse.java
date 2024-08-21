package com.example.easy_sale.model;

import java.util.List;

public class UserResponse {
    public int page;
    public int per_page;
    public int total;
    public int total_pages;
    public List<User> data;

    public UserResponse(int page, int per_page, int total, int total_pages, List<User> data) {
        this.page = page;
        this.per_page = per_page;
        this.total = total;
        this.total_pages = total_pages;
        this.data = data;
    }

    public UserResponse() {
    }

    public int getPage() {
        return page;
    }

    public UserResponse setPage(int page) {
        this.page = page;
        return this;
    }

    public int getPer_page() {
        return per_page;
    }

    public UserResponse setPer_page(int per_page) {
        this.per_page = per_page;
        return this;
    }

    public int getTotal() {
        return total;
    }

    public UserResponse setTotal(int total) {
        this.total = total;
        return this;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public UserResponse setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
        return this;
    }

    public List<User> getData() {
        return data;
    }

    public UserResponse setData(List<User> data) {
        this.data = data;
        return this;
    }
}
