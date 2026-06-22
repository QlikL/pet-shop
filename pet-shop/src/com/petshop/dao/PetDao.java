package com.petshop.dao;

import com.petshop.model.Pet;
import java.io.*;
import java.util.*;

public class PetDao {
    public static final String DATA_FILE = "data/pets.dat";
    private List<Pet> pets;

    public PetDao() {
        this.pets = new ArrayList<>();
        loadPets();
    }

    private void loadPets() {
        try {
            Object obj = FileDao.loadObject(DATA_FILE);
            if (obj != null) {
                this.pets = (List<Pet>) obj;
            }
        } catch (Exception e) {
            this.pets = new ArrayList<>();
        }
    }

    private void savePets() {
        try {
            new File("data").mkdirs();
            FileDao.saveObject(DATA_FILE, pets);
        } catch (IOException e) {
            System.out.println("保存宠物数据失败：" + e.getMessage());
        }
    }

    public boolean add(Pet pet) {
        if (findById(pet.getId()) != null) return false;
        pets.add(pet);
        savePets();
        return true;
    }

    public boolean delete(String id) {
        Pet pet = findById(id);
        if (pet != null) {
            pets.remove(pet);
            savePets();
            return true;
        }
        return false;
    }

    public boolean update(Pet pet) {
        for (int i = 0; i < pets.size(); i++) {
            if (pets.get(i).getId().equals(pet.getId())) {
                pets.set(i, pet);
                savePets();
                return true;
            }
        }
        return false;
    }

    public Pet findById(String id) {
        for (Pet pet : pets) {
            if (pet.getId().equals(id)) return pet;
        }
        return null;
    }

    public List<Pet> findAll() { return new ArrayList<>(pets); }

    public List<Pet> findBySpecies(String species) {
        List<Pet> result = new ArrayList<>();
        for (Pet pet : pets) {
            if (pet.getSpecies().equals(species)) result.add(pet);
        }
        return result;
    }

    public List<Pet> findByStatus(String status) {
        List<Pet> result = new ArrayList<>();
        for (Pet pet : pets) {
            if (pet.getStatus().equals(status)) result.add(pet);
        }
        return result;
    }
}
