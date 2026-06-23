package com.petshop.model;

public class Cat extends Pet {
    private String color;

    public Cat() {}

    public Cat(String id, String name, double age, double price, String status, String color) {
        super(id, name, "猫", age, price, status);
        this.color = color;
    }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    @Override
    public String toString() {
        return super.toString() + ", color='" + color + "'}";
    }
}
