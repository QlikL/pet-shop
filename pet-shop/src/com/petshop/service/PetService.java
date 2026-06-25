package com.petshop.service;

import com.petshop.dao.InventoryDao;
import com.petshop.dao.PetDao;
import com.petshop.dao.SalesRecordDao;
import com.petshop.exception.BusinessException;
import com.petshop.model.Inventory;
import com.petshop.model.Pet;
import com.petshop.model.SalesRecord;
import java.util.List;
import java.util.UUID;

public class PetService {
    private PetDao petDao;
    private InventoryDao inventoryDao;
    private SalesRecordDao salesRecordDao;

    public PetService() {
        this.petDao = new PetDao();
        this.inventoryDao = new InventoryDao();
        this.salesRecordDao = new SalesRecordDao();
    }

    public PetService(PetDao petDao, InventoryDao inventoryDao) {
        this.petDao = petDao;
        this.inventoryDao = inventoryDao;
        this.salesRecordDao = new SalesRecordDao();
    }

    public PetService(PetDao petDao, InventoryDao inventoryDao, SalesRecordDao salesRecordDao) {
        this.petDao = petDao;
        this.inventoryDao = inventoryDao;
        this.salesRecordDao = salesRecordDao;
    }

    /**
     * 添加宠物
     * @param id 宠物ID
     * @param breed 品种
     * @param species 种类
     * @param birthYear 出生年份
     * @param birthMonth 出生月份
     * @param price 价格
     * @param status 状态
     * @return 添加的宠物对象
     */
    public Pet addPet(String id, String breed, String species, int birthYear, int birthMonth, double price, String status)
            throws BusinessException {
        if (id == null || id.trim().isEmpty()) throw new BusinessException("宠物ID不能为空");
        if (breed == null || breed.trim().isEmpty()) throw new BusinessException("宠物品种不能为空");
        if (birthYear < 1900 || birthYear > 2100) throw new BusinessException("出生年份无效");
        if (birthMonth < 1 || birthMonth > 12) throw new BusinessException("出生月份无效");
        if (price < 0) throw new BusinessException("价格不能为负数");

        Pet pet = new Pet(id, breed, species, birthYear, birthMonth, price, status);
        if (!petDao.add(pet)) throw new BusinessException("宠物ID已存在");
        // 不再在添加宠物时创建库存记录，库存由InventoryService自动统计
        return pet;
    }

    public void deletePet(String id) throws BusinessException {
        if (petDao.findById(id) == null) throw new BusinessException("宠物不存在");
        petDao.delete(id);
    }

    /**
     * 更新宠物信息
     * @param id 宠物ID
     * @param breed 品种
     * @param species 种类
     * @param birthYear 出生年份
     * @param birthMonth 出生月份
     * @param price 价格
     * @param status 状态
     * @return 更新后的宠物对象
     */
    public Pet updatePet(String id, String breed, String species, int birthYear, int birthMonth, double price, String status)
            throws BusinessException {
        Pet pet = petDao.findById(id);
        if (pet == null) throw new BusinessException("宠物不存在");
        
        // 保存旧状态用于判断是否需要创建/删除销售记录
        String oldStatus = pet.getStatus();
        
        if (breed != null && !breed.trim().isEmpty()) pet.setBreed(breed);
        if (species != null && !species.trim().isEmpty()) pet.setSpecies(species);
        if (birthYear >= 1900 && birthYear <= 2100) pet.setBirthYear(birthYear);
        if (birthMonth >= 1 && birthMonth <= 12) pet.setBirthMonth(birthMonth);
        if (price >= 0) pet.setPrice(price);
        if (status != null && !status.trim().isEmpty()) pet.setStatus(status);
        // 重新计算年龄
        pet.updateAge();
        petDao.update(pet);
        
        // 处理状态变更时的销售记录
        handleStatusChange(pet, oldStatus, status);
        
        return pet;
    }

    /**
     * 处理宠物状态变更时的销售记录
     */
    private void handleStatusChange(Pet pet, String oldStatus, String newStatus) {
        // 如果从非已售变为已售，创建销售记录
        if (!"已售".equals(oldStatus) && "已售".equals(newStatus)) {
            createSalesRecord(pet);
        }
        // 如果从已售变为非可售，创建负数金额的销售记录（用于抵消）
        else if ("已售".equals(oldStatus) && !"已售".equals(newStatus)) {
            createNegativeSalesRecord(pet);
        }
    }

    /**
     * 创建销售记录（当宠物变为已售时）
     */
    private void createSalesRecord(Pet pet) {
        try {
            String recordId = UUID.randomUUID().toString().substring(0, 8);
            // 销售金额为正数：+售价
            SalesRecord record = new SalesRecord(recordId, pet.getId(), pet.getBreed(), 
                                                  pet.getSpecies(), pet.getPrice());
            salesRecordDao.add(record);
        } catch (Exception e) {
            System.out.println("创建销售记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建负数金额的销售记录（当宠物从可售改为其他状态时）
     */
    private void createNegativeSalesRecord(Pet pet) {
        try {
            String recordId = UUID.randomUUID().toString().substring(0, 8);
            // 销售金额为负数：-售价
            SalesRecord record = new SalesRecord(recordId, pet.getId(), pet.getBreed(), 
                                                  pet.getSpecies(), -pet.getPrice());
            salesRecordDao.add(record);
        } catch (Exception e) {
            System.out.println("创建负数销售记录失败：" + e.getMessage());
        }
    }

    public Pet getPet(String id) throws BusinessException {
        Pet pet = petDao.findById(id);
        if (pet == null) throw new BusinessException("宠物不存在");
        return pet;
    }

    public List<Pet> getAllPets() { return petDao.findAll(); }
    public List<Pet> getPetsBySpecies(String species) { return petDao.findBySpecies(species); }
    public List<Pet> getPetsByStatus(String status) { return petDao.findByStatus(status); }
    
    /**
     * 更新所有宠物的年龄（登录时调用）
     */
    public void updateAllPetsAge() {
        List<Pet> allPets = petDao.findAll();
        for (Pet pet : allPets) {
            pet.updateAge();
        }
        // 保存更新后的数据
        petDao.saveAll(allPets);
    }
}