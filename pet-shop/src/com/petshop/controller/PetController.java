package com.petshop.controller;

import com.petshop.exception.BusinessException;
import com.petshop.model.Pet;
import com.petshop.service.PetService;
import com.petshop.view.PetManagementView;
import java.util.List;

public class PetController {
    private PetService petService;
    private PetManagementView view;

    public PetController(PetManagementView view) {
        this.petService = new PetService();
        this.view = view;
    }

    public PetController(PetManagementView view, PetService petService) {
        this.petService = petService;
        this.view = view;
    }

    public void run() {
        boolean running = true;
        while (running) {
            int choice = view.showMenu();
            switch (choice) {
                case 1: addPet(); break;
                case 2: deletePet(); break;
                case 3: updatePet(); break;
                case 4: queryPet(); break;
                case 5: listAllPets(); break;
                case 6: listBySpecies(); break;
                case 7: listByStatus(); break;
                case 0: running = false; break;
                default: view.showMessage("无效的选择，请重新输入");
            }
        }
    }

    private void addPet() {
        try {
            String[] info = view.getAddPetInfo();
            Pet pet = petService.addPet(info[0], info[1], info[2], Integer.parseInt(info[3]),
                    Integer.parseInt(info[4]), Double.parseDouble(info[5]), info[6]);
            // 使用getBreed()代替getName()
            view.showMessage("添加成功：" + pet.getBreed());
        } catch (BusinessException e) { view.showMessage("添加失败：" + e.getMessage());
        } catch (NumberFormatException e) { view.showMessage("输入格式错误"); }
    }

    private void deletePet() {
        try {
            String id = view.getPetId();
            Pet pet = petService.getPet(id);
            // 使用getBreed()代替getName()
            if (view.confirmDelete(pet.getBreed())) {
                petService.deletePet(id);
                view.showMessage("删除成功");
            } else { view.showMessage("已取消删除"); }
        } catch (BusinessException e) { view.showMessage("删除失败：" + e.getMessage()); }
    }

    private void updatePet() {
        try {
            String id = view.getPetId();
            petService.getPet(id);
            String[] info = view.getUpdatePetInfo();
            int birthYear = info[2].isEmpty() ? -1 : Integer.parseInt(info[2]);
            int birthMonth = info[3].isEmpty() ? -1 : Integer.parseInt(info[3]);
            double price = info[4].isEmpty() ? -1 : Double.parseDouble(info[4]);
            petService.updatePet(id, info[0], info[1], birthYear, birthMonth, price, info[5]);
            view.showMessage("修改成功");
        } catch (BusinessException e) { view.showMessage("修改失败：" + e.getMessage());
        } catch (NumberFormatException e) { view.showMessage("输入格式错误"); }
    }

    private void queryPet() {
        try {
            Pet pet = petService.getPet(view.getPetId());
            view.showPetDetail(pet);
        } catch (BusinessException e) { view.showMessage("查询失败：" + e.getMessage()); }
    }

    private void listAllPets() { view.showPetList(petService.getAllPets()); }

    private void listBySpecies() {
        view.showPetList(petService.getPetsBySpecies(view.getFilterCondition("种类")));
    }

    private void listByStatus() {
        view.showPetList(petService.getPetsByStatus(view.getFilterCondition("状态")));
    }
}
