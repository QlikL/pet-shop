package com.petshop.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SalesRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private String recordId;
    private String petId;
    private String petName;
    private String species; // 种类（狗、猫、其他）
    private double totalPrice;
    private LocalDateTime saleTime;

    public SalesRecord() {}

    public SalesRecord(String recordId, String petId, String petName, String species, double totalPrice) {
        this.recordId = recordId;
        this.petId = petId;
        this.petName = petName;
        this.species = species;
        this.totalPrice = totalPrice;
        this.saleTime = LocalDateTime.now();
    }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }
    public String getPetId() { return petId; }
    public void setPetId(String petId) { this.petId = petId; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public LocalDateTime getSaleTime() { return saleTime; }
    public void setSaleTime(LocalDateTime saleTime) { this.saleTime = saleTime; }

    @Override
    public String toString() {
        return "SalesRecord{recordId='" + recordId + "', petId='" + petId +
               "', petName='" + petName + "', species='" + species +
               "', totalPrice=" + totalPrice + ", saleTime=" + saleTime + "}";
    }
}
