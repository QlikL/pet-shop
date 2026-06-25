package com.petshop.model;

public class Dog extends Pet {
    private String breed;

    public Dog() {}

    public Dog(String id, String breed, String species, int birthYear, int birthMonth, double price, String status) {
        super(id, breed, species, birthYear, birthMonth, price, status);
        this.breed = breed;
    }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    @Override
    public String toString() {
        return super.toString() + ", breed='" + breed + "'}";
    }
}
