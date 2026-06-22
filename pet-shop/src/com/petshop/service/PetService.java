package com.petshop.service;

import com.petshop.dao.InventoryDao;
import com.petshop.dao.PetDao;
import com.petshop.exception.BusinessException;
import com.petshop.model.Inventory;
import com.petshop.model.Pet;
import java.util.List;

public class PetService {
    private PetDao petDao;
    private InventoryDao inventoryDao;

    public PetService() {
        this.petDao = new PetDao();
        this.inventoryDao = new InventoryDao();
    }

    public PetService(PetDao petDao, InventoryDao inventoryDao) {
        this.petDao = petDao;
        this.inventoryDao = inventoryDao;
    }

    public Pet addPet(String id, String name, String species, int age, double price, String status)
            throws BusinessException {
        if (id == null || id.trim().isEmpty()) throw new BusinessException("宠物ID不能为空");
        if (name == null || name.trim().isEmpty()) throw new BusinessException("宠物名称不能为空");
        if (age < 0) throw new BusinessException("年龄不能为负数");
        if (price < 0) throw new BusinessException("价格不能为负数");

        Pet pet = new Pet(id, name, species, age, price, status);
        if (!petDao.add(pet)) throw new BusinessException("宠物ID已存在");
        inventoryDao.save(new Inventory(id, 10, 5));
        return pet;
    }

    public void deletePet(String id) throws BusinessException {
        if (petDao.findById(id) == null) throw new BusinessException("宠物不存在");
        petDao.delete(id);
    }

    public Pet updatePet(String id, String name, String species, int age, double price, String status)
            throws BusinessException {
        Pet pet = petDao.findById(id);
        if (pet == null) throw new BusinessException("宠物不存在");
        if (name != null && !name.trim().isEmpty()) pet.setName(name);
        if (species != null && !species.trim().isEmpty()) pet.setSpecies(species);
        if (age >= 0) pet.setAge(age);
        if (price >= 0) pet.setPrice(price);
        if (status != null && !status.trim().isEmpty()) pet.setStatus(status);
        petDao.update(pet);
        return pet;
    }

    public Pet getPet(String id) throws BusinessException {
        Pet pet = petDao.findById(id);
        if (pet == null) throw new BusinessException("宠物不存在");
        return pet;
    }

    public List<Pet> getAllPets() { return petDao.findAll(); }
    public List<Pet> getPetsBySpecies(String species) { return petDao.findBySpecies(species); }
    public List<Pet> getPetsByStatus(String status) { return petDao.findByStatus(status); }
}
