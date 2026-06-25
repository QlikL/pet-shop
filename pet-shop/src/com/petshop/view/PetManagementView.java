package com.petshop.view;

import com.petshop.model.Pet;
import com.petshop.util.TableUtil;
import java.util.List;
import java.util.Scanner;

public class PetManagementView {
    private Scanner scanner;

    public PetManagementView(Scanner scanner) {
        this.scanner = scanner;
    }

    public int showMenu() {
        System.out.println("\n---------- 宠物管理 ----------");
        System.out.println("1. 添加宠物");
        System.out.println("2. 删除宠物");
        System.out.println("3. 修改宠物");
        System.out.println("4. 查询宠物");
        System.out.println("5. 列出所有宠物");
        System.out.println("6. 按种类筛选");
        System.out.println("7. 按状态筛选");
        System.out.println("0. 返回主菜单");
        System.out.println("------------------------------");
        System.out.print("请选择功能：");
        int choice = -1;
        try { choice = Integer.parseInt(scanner.nextLine()); } catch (NumberFormatException e) {}
        return choice;
    }

    public String[] getAddPetInfo() {
        String[] info = new String[7];
        System.out.print("请输入宠物ID："); info[0] = scanner.nextLine();
        System.out.print("请输入宠物品种："); info[1] = scanner.nextLine();
        System.out.print("请输入种类（狗/猫/其他）："); info[2] = scanner.nextLine();
        System.out.print("请输入出生年份："); info[3] = scanner.nextLine();
        System.out.print("请输入出生月份（1-12）："); info[4] = scanner.nextLine();
        System.out.print("请输入价格："); info[5] = scanner.nextLine();
        System.out.print("请输入状态（可售/已售/预留）："); info[6] = scanner.nextLine();
        return info;
    }

    public String getPetId() {
        System.out.print("请输入宠物ID：");
        return scanner.nextLine();
    }

    public String[] getUpdatePetInfo() {
        String[] info = new String[6];
        System.out.print("请输入新品种（直接回车不修改）："); info[0] = scanner.nextLine();
        System.out.print("请输入新种类（直接回车不修改）："); info[1] = scanner.nextLine();
        System.out.print("请输入新出生年份（直接回车不修改）："); info[2] = scanner.nextLine();
        System.out.print("请输入新出生月份（直接回车不修改）："); info[3] = scanner.nextLine();
        System.out.print("请输入新价格（直接回车不修改）："); info[4] = scanner.nextLine();
        System.out.print("请输入新状态（直接回车不修改）："); info[5] = scanner.nextLine();
        return info;
    }

    public String getFilterCondition(String type) {
        System.out.print("请输入" + type + "：");
        return scanner.nextLine();
    }

    public boolean confirmDelete(String petName) {
        System.out.print("确定要删除宠物 " + petName + " 吗？(y/n): ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }

    public void showPetList(List<Pet> pets) {
        if (pets.isEmpty()) { System.out.println("没有宠物数据"); return; }
        // 修改表头：名称改为品种
        String[] headers = {"ID", "品种", "种类", "年龄", "价格", "状态"};
        String[][] data = new String[pets.size()][6];
        for (int i = 0; i < pets.size(); i++) {
            Pet pet = pets.get(i);
            // 使用getBreed()代替getName()
            data[i][0] = pet.getId();
            data[i][1] = pet.getBreed();
            data[i][2] = pet.getSpecies();
            data[i][3] = String.valueOf(pet.getAge());
            data[i][4] = String.valueOf(pet.getPrice());
            data[i][5] = pet.getStatus();
        }
        TableUtil.printTable(headers, data);
    }

    public void showPetDetail(Pet pet) {
        System.out.println("\n---------- 宠物详情 ----------");
        System.out.println("ID：" + pet.getId());
        // 使用getBreed()代替getName()
        System.out.println("品种：" + pet.getBreed());
        System.out.println("种类：" + pet.getSpecies());
        System.out.println("年龄：" + pet.getAge());
        System.out.println("价格：" + pet.getPrice());
        System.out.println("状态：" + pet.getStatus());
        System.out.println("------------------------------");
    }

    public void showMessage(String message) { System.out.println(message); }
}
