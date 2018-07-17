package com.mobilelearning.student.model;

public class Topik {
    private int topikId;
    private String topikNama;
    private int topikStatus;
    private String color;

    public int getTopikId() {
        return topikId;
    }

    public void setTopikId(int topikId) {
        this.topikId = topikId;
    }

    public String getTopikNama() {
        return topikNama;
    }

    public void setTopikNama(String topikNama) {
        this.topikNama = topikNama;
    }

    public int getTopikStatus() {
        return topikStatus;
    }

    public void setTopikStatus(int topikStatus) {
        this.topikStatus = topikStatus;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
