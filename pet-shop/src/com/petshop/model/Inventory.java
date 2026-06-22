package com.petshop.model;

import java.io.Serializable;

public class Inventory implements Serializable {
    private static final long serialVersionUID = 1L;

    private String petId;
    private int quantity;
    private int warningThreshold;

    public Inventory() {}

    public Inventory(String petId, int quantity, int warningThreshold) {
        this.petId = petId;
        this.quantity = quantity;
        this.warningThreshold = warningThreshold;
    }

    public String getPetId() { return petId; }
    public void setPetId(String petId) { this.petId = petId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getWarningThreshold() { return warningThreshold; }
    public void setWarningThreshold(int warningThreshold) { this.warningThreshold = warningThreshold; }

    public void addQuantity(int amount) { this.quantity += amount; }

    public boolean reduceQuantity(int amount) {
        if (amount > quantity) return false;
        this.quantity -= amount;
        return true;
    }

    public boolean needWarning() { return quantity <= warningThreshold; }

    @Override
    public String toString() {
        return "Inventory{petId='" + petId + "', quantity=" + quantity +
               ", warningThreshold=" + warningThreshold + "}";
    }
}
