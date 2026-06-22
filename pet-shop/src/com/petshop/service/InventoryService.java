package com.petshop.service;

import com.petshop.dao.InventoryDao;
import com.petshop.dao.PetDao;
import com.petshop.exception.BusinessException;
import com.petshop.model.Inventory;
import com.petshop.model.Pet;
import java.util.ArrayList;
import java.util.List;

public class InventoryService {
    private InventoryDao inventoryDao;
    private PetDao petDao;

    public InventoryService() {
        this.inventoryDao = new InventoryDao();
        this.petDao = new PetDao();
    }

    public InventoryService(PetDao petDao, InventoryDao inventoryDao) {
        this.petDao = petDao;
        this.inventoryDao = inventoryDao;
    }

    public void updateQuantity(String petId, int amount) throws BusinessException {
        Pet pet = petDao.findById(petId);
        if (pet == null) throw new BusinessException("宠物不存在");
        Inventory inventory = inventoryDao.findByPetId(petId);
        if (inventory == null) inventory = new Inventory(petId, 0, 5);
        if (amount < 0 && inventory.getQuantity() + amount < 0) throw new BusinessException("库存不足");
        inventory.setQuantity(inventory.getQuantity() + amount);
        inventoryDao.save(inventory);
    }

    public void setWarningThreshold(String petId, int threshold) throws BusinessException {
        if (threshold < 0) throw new BusinessException("预警阈值不能为负数");
        Pet pet = petDao.findById(petId);
        if (pet == null) throw new BusinessException("宠物不存在");
        Inventory inventory = inventoryDao.findByPetId(petId);
        if (inventory == null) inventory = new Inventory(petId, 0, threshold);
        else inventory.setWarningThreshold(threshold);
        inventoryDao.save(inventory);
    }

    public List<Inventory> getAllInventory() { return inventoryDao.findAll(); }

    public List<Inventory> getWarningInventory() {
        List<Inventory> warnings = new ArrayList<>();
        for (Inventory inv : inventoryDao.findAll()) {
            if (inv.needWarning()) warnings.add(inv);
        }
        return warnings;
    }

    public double getTotalValue() {
        double total = 0;
        for (Inventory inv : inventoryDao.findAll()) {
            Pet pet = petDao.findById(inv.getPetId());
            if (pet != null) total += pet.getPrice() * inv.getQuantity();
        }
        return total;
    }
}
