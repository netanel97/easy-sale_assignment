package com.example.easy_sale;

import com.google.gson.annotations.SerializedName;

public class UpdateUserResponse {
    @SerializedName("name")
    private String name;

    @SerializedName("updatedAt")
    private String updatedAt;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "UpdateUserResponse{" +
                "name='" + name + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
