package com.petshop.model;

import java.io.Serializable;

public class Pet implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String breed; // 品种（原来是name）
    private String species; // 种类（狗、猫、其他）
    private double age;
    private double price;
    private String status;

    public Pet() {
    }

    public Pet(String id, String breed, String species, double age, double price, String status) {
        this.id = id;
        this.breed = breed;
        this.species = species;
        this.age = age;
        this.price = price;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public double getAge() { return age; }
    public void setAge(double age) { this.age = age; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Pet{id='" + id + "', breed='" + breed + "', species='" + species +
               "', age=" + age + ", price=" + price + ", status='" + status + "'}";
    }
}
