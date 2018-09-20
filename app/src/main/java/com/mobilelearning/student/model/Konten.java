package com.mobilelearning.student.model;

public class Konten {
    int kontenId;
    String kontenName;
    String kontenType;
    String value;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
