package com.petshop.model;

public class Dog extends Pet {
    private String breed;

    public Dog() {}

    public Dog(String id, String name, double age, double price, String status, String breed) {
        super(id, name, "狗", age, price, status);
        this.breed = breed;
    }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    @Override
    public String toString() {
        return super.toString() + ", breed='" + breed + "'}";
    }
}
