package com.example.mytiltok.domain;

import java.util.List;

public class Result {

    private String total;
    private List<VideoMessage> list;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<VideoMessage> getList() {
        return list;
    }

    public void setList(List<VideoMessage> list) {
        this.list = list;
    }
}
