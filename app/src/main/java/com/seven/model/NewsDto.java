package com.seven.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Richi on 10/10/21.
 */
public class NewsDto {

    @Expose
    @SerializedName("data")
    private List<News> data;
    @Expose
    @SerializedName("isSuccess")
    private boolean isSuccess;

    public List<News> getData() {
        return data;
    }

    public void setData(List<News> data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
