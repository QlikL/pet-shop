package com.petshop.service;

import com.petshop.dao.InventoryDao;
import com.petshop.dao.PetDao;
import com.petshop.dao.SalesRecordDao;
import com.petshop.exception.BusinessException;
import com.petshop.model.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class SalesService {
    private SalesRecordDao salesRecordDao;
    private PetDao petDao;
    private InventoryDao inventoryDao;

    public SalesService() {
        this.salesRecordDao = new SalesRecordDao();
        this.petDao = new PetDao();
        this.inventoryDao = new InventoryDao();
    }

    public SalesService(PetDao petDao, InventoryDao inventoryDao, SalesRecordDao salesRecordDao) {
        this.petDao = petDao;
        this.inventoryDao = inventoryDao;
        this.salesRecordDao = salesRecordDao;
    }

    public SalesRecord addSale(String petId, int quantity) throws BusinessException {
        Pet pet = petDao.findById(petId);
        if (pet == null) throw new BusinessException("宠物不存在");
        
        // 根据宠物的品种查找库存
        Inventory inventory = inventoryDao.findBySpeciesAndBreed(pet.getSpecies(), pet.getBreed());
        if (inventory == null || inventory.getQuantity() < quantity) throw new BusinessException("库存不足");
        
        // 减少库存数量
        inventory.reduceQuantity(quantity);
        inventoryDao.save(inventory);
        
        String recordId = UUID.randomUUID().toString().substring(0, 8);
        // 使用pet.getBreed()代替pet.getName()
        SalesRecord record = new SalesRecord(recordId, petId, pet.getBreed(), quantity, pet.getPrice());
        salesRecordDao.add(record);
        return record;
    }

    public SalesRecord getSaleRecord(String recordId) throws BusinessException {
        SalesRecord record = salesRecordDao.findById(recordId);
        if (record == null) throw new BusinessException("销售记录不存在");
        return record;
    }

    public List<SalesRecord> getAllSales() { return salesRecordDao.findAll(); }
    public List<SalesRecord> getSalesByTimeRange(LocalDateTime start, LocalDateTime end) {
        return salesRecordDao.findByTimeRange(start, end);
    }

    public double getTotalSalesAmount() {
        double total = 0;
        for (SalesRecord record : salesRecordDao.findAll()) total += record.getTotalPrice();
        return total;
    }

    public int getTotalSalesQuantity() {
        int total = 0;
        for (SalesRecord record : salesRecordDao.findAll()) total += record.getQuantity();
        return total;
    }
}
