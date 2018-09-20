package com.mobilelearning.student.model;

import com.mobilelearning.student.MainActivity;

public class Topik {
    private int topikId;
    private String topikNama;
    private int topikStatus;
    private int color;

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

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
