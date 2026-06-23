package com.petshop.model;

import java.io.Serializable;

public class Inventory implements Serializable {
    private static final long serialVersionUID = 1L;

    private String species; // 种类（狗、猫、其他）
    private String breed; // 品种
    private int quantity; // 该品种的总数量（自动统计，不可手动修改）
    private int warningThreshold; // 预警阈值

    public Inventory() {}

    public Inventory(String species, String breed, int quantity, int warningThreshold) {
        this.species = species;
        this.breed = breed;
        this.quantity = quantity;
        this.warningThreshold = warningThreshold;
    }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getWarningThreshold() { return warningThreshold; }
    public void setWarningThreshold(int warningThreshold) { this.warningThreshold = warningThreshold; }

    // 注意：addQuantity和reduceQuantity方法已废弃，库存数量应通过自动统计获得
    @Deprecated
    public void addQuantity(int amount) { this.quantity += amount; }

    @Deprecated
    public boolean reduceQuantity(int amount) {
        if (amount > quantity) return false;
        this.quantity -= amount;
        return true;
    }

    public boolean needWarning() { return quantity <= warningThreshold; }

    @Override
    public String toString() {
        return "Inventory{species='" + species + "', breed='" + breed +
               "', quantity=" + quantity + ", warningThreshold=" + warningThreshold + "}";
    }
}
