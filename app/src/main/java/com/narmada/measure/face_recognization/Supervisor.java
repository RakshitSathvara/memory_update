package com.narmada.measure.face_recognization;

public class Supervisor {

    int id;
    String name;
    String code;
    String image;

    public Supervisor(int id, String name, String code, String image) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getImage() {
        return image;
    }
}
