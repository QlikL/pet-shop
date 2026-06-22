package com.petshop.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SalesRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private String recordId;
    private String petId;
    private String petName;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private LocalDateTime saleTime;

    public SalesRecord() {}

    public SalesRecord(String recordId, String petId, String petName, int quantity, double unitPrice) {
        this.recordId = recordId;
        this.petId = petId;
        this.petName = petName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
        this.saleTime = LocalDateTime.now();
    }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }
    public String getPetId() { return petId; }
    public void setPetId(String petId) { this.petId = petId; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public LocalDateTime getSaleTime() { return saleTime; }
    public void setSaleTime(LocalDateTime saleTime) { this.saleTime = saleTime; }

    @Override
    public String toString() {
        return "SalesRecord{recordId='" + recordId + "', petId='" + petId +
               "', petName='" + petName + "', quantity=" + quantity +
               ", totalPrice=" + totalPrice + ", saleTime=" + saleTime + "}";
    }
}
