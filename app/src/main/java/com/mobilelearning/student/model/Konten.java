package com.mobilelearning.student.model;

public class Konten {
    int kontenId;
    String kontenName;
    String kontenType;
    String file;
    private String color, colorLight;

    public int getKontenId() {
        return kontenId;
    }

    public void setKontenId(int kontenId) {
        this.kontenId = kontenId;
    }

    public String getKontenName() {
        return kontenName;
    }

    public void setKontenName(String kontenName) {
        this.kontenName = kontenName;
    }

    public String getKontenType() {
        return kontenType;
    }

    public void setKontenType(String kontenType) {
        this.kontenType = kontenType;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColorLight() {
        return colorLight;
    }

    public void setColorLight(String colorLight) {
        this.colorLight = colorLight;
    }
}
