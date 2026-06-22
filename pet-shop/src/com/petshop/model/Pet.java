package com.petshop.model;

import java.io.Serializable;

public class Pet implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String species;
    private int age;
    private double price;
    private String status;

    public Pet() {
    }

    public Pet(String id, String name, String species, int age, double price, String status) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.age = age;
        this.price = price;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Pet{id='" + id + "', name='" + name + "', species='" + species +
               "', age=" + age + ", price=" + price + ", status='" + status + "'}";
    }
}
