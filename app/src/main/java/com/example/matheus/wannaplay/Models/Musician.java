package com.example.matheus.wannaplay.Models;

public class Musician {

    private String name;
    private int age;
    private double latitude;
    private double longitude;
    private String photoUrl;

    public Musician() {

    }

    public Musician(String name, int age, double latitude, double longitude, String photoUrl) {
        this.name = name;
        this.age = age;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
