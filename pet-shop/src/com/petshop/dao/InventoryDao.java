package com.petshop.dao;

import com.petshop.model.Inventory;
import java.io.*;
import java.util.*;

public class InventoryDao {
    private static final String DATA_FILE = "data/inventory.dat";
    private Map<String, Inventory> inventoryMap; // key: species_breed 组合键

    public InventoryDao() {
        this.inventoryMap = new HashMap<>();
        loadInventory();
    }

    private void loadInventory() {
        try {
            Object obj = FileDao.loadObject(DATA_FILE);
            if (obj != null) {
                this.inventoryMap = (Map<String, Inventory>) obj;
            }
        } catch (Exception e) {
            this.inventoryMap = new HashMap<>();
        }
    }

    private void saveInventory() {
        try {
            new File("data").mkdirs();
            FileDao.saveObject(DATA_FILE, inventoryMap);
        } catch (IOException e) {
            System.out.println("保存库存数据失败：" + e.getMessage());
        }
    }

    public void save(Inventory inventory) {
        String key = generateKey(inventory.getSpecies(), inventory.getBreed());
        inventoryMap.put(key, inventory);
        saveInventory();
    }

    /**
     * 根据种类和品种查找库存
     */
    public Inventory findBySpeciesAndBreed(String species, String breed) {
        String key = generateKey(species, breed);
        return inventoryMap.get(key);
    }

    /**
     * 生成库存键值：species_breed
     */
    private String generateKey(String species, String breed) {
        return species + "_" + breed;
    }

    public List<Inventory> findAll() { return new ArrayList<>(inventoryMap.values()); }

    public boolean delete(String petId) {
        if (inventoryMap.containsKey(petId)) {
            inventoryMap.remove(petId);
            saveInventory();
            return true;
        }
        return false;
    }
}
