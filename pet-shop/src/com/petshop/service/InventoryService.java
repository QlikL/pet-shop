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

    /**
     * 重新计算并更新所有库存（根据宠物列表自动统计）
     * 注意：只统计"可售"和"预留"状态的宠物，"已售"宠物不计入库存
     */
    public void recalculateAllInventory() {
        List<Pet> allPets = petDao.findAll();
            
        // 统计每个品种的数量（排除已售状态）
        java.util.Map<String, int[]> speciesBreedCountMap = new java.util.HashMap<>();
        for (Pet pet : allPets) {
            // 只统计可售和预留的宠物
            if ("可售".equals(pet.getStatus()) || "预留".equals(pet.getStatus())) {
                String key = pet.getSpecies() + "_" + pet.getBreed();
                if (!speciesBreedCountMap.containsKey(key)) {
                    speciesBreedCountMap.put(key, new int[]{0});
                }
                speciesBreedCountMap.get(key)[0]++;
            }
        }
        
        // 更新库存记录
        for (java.util.Map.Entry<String, int[]> entry : speciesBreedCountMap.entrySet()) {
            String[] parts = entry.getKey().split("_", 2);
            String species = parts[0];
            String breed = parts[1];
            int count = entry.getValue()[0];
            
            Inventory inventory = inventoryDao.findBySpeciesAndBreed(species, breed);
            if (inventory == null) {
                inventory = new Inventory(species, breed, count, 5); // 默认预警阈值为5
            } else {
                inventory.setQuantity(count); // 只更新数量，不修改预警阈值
            }
            inventoryDao.save(inventory);
        }
        
        // 删除已经不存在的品种的库存记录
        List<Inventory> allInventory = inventoryDao.findAll();
        for (Inventory inv : allInventory) {
            String key = inv.getSpecies() + "_" + inv.getBreed();
            if (!speciesBreedCountMap.containsKey(key)) {
                inventoryDao.delete(inv.getSpecies() + "_" + inv.getBreed());
            }
        }
    }

    /**
     * 获取所有库存（先重新计算）
     */
    public List<Inventory> getAllInventory() {
        recalculateAllInventory();
        return inventoryDao.findAll();
    }

    /**
     * 设置预警阈值（按品种）
     */
    public void setWarningThreshold(String species, String breed, int threshold) throws BusinessException {
        if (threshold < 0) throw new BusinessException("预警阈值不能为负数");
        Inventory inventory = inventoryDao.findBySpeciesAndBreed(species, breed);
        if (inventory == null) {
            inventory = new Inventory(species, breed, 0, threshold);
        } else {
            inventory.setWarningThreshold(threshold);
        }
        inventoryDao.save(inventory);
    }

    public List<Inventory> getWarningInventory() {
        List<Inventory> warnings = new ArrayList<>();
        for (Inventory inv : getAllInventory()) {
            if (inv.needWarning()) warnings.add(inv);
        }
        return warnings;
    }

    public double getTotalValue() {
        double total = 0;
        List<Inventory> allInventory = getAllInventory();
        for (Inventory inv : allInventory) {
            // 查找该品种的任意一只宠物获取价格
            List<Pet> allPets = petDao.findAll();
            for (Pet pet : allPets) {
                if (pet.getSpecies().equals(inv.getSpecies()) && pet.getBreed().equals(inv.getBreed())) {
                    total += pet.getPrice() * inv.getQuantity();
                    break;
                }
            }
        }
        return total;
    }
}
