package com.petshop.view;

import com.petshop.model.Inventory;
import com.petshop.util.TableUtil;
import java.util.List;
import java.util.Scanner;

public class InventoryManagementView {
    private Scanner scanner;

    public InventoryManagementView(Scanner scanner) {
        this.scanner = scanner;
    }

    public int showMenu() {
        System.out.println("\n---------- 库存管理 ----------");
        System.out.println("1. 查询库存");
        System.out.println("2. 更新库存");
        System.out.println("3. 设置库存预警");
        System.out.println("4. 检查库存预警");
        System.out.println("5. 库存统计");
        System.out.println("0. 返回主菜单");
        System.out.println("------------------------------");
        System.out.print("请选择功能：");
        int choice = -1;
        try { choice = Integer.parseInt(scanner.nextLine()); } catch (NumberFormatException e) {}
        return choice;
    }

    public String getPetId() {
        System.out.print("请输入宠物ID：");
        return scanner.nextLine();
    }

    public int getQuantity() {
        System.out.print("请输入数量：");
        try { return Integer.parseInt(scanner.nextLine()); } catch (NumberFormatException e) { return 0; }
    }

    public int getWarningThreshold() {
        System.out.print("请输入预警阈值：");
        try { return Integer.parseInt(scanner.nextLine()); } catch (NumberFormatException e) { return 0; }
    }

    public void showInventoryList(List<Inventory> inventories) {
        if (inventories.isEmpty()) { System.out.println("没有库存数据"); return; }
        String[] headers = {"宠物ID", "库存数量", "预警阈值", "状态"};
        String[][] data = new String[inventories.size()][4];
        for (int i = 0; i < inventories.size(); i++) {
            Inventory inv = inventories.get(i);
            data[i][0] = inv.getPetId(); data[i][1] = String.valueOf(inv.getQuantity());
            data[i][2] = String.valueOf(inv.getWarningThreshold());
            data[i][3] = inv.needWarning() ? "预警" : "正常";
        }
        TableUtil.printTable(headers, data);
    }

    public void showInventoryStatistics(int totalQuantity, double totalValue) {
        System.out.println("\n---------- 库存统计 ----------");
        System.out.println("总库存数量：" + totalQuantity);
        System.out.println("总库存价值：" + String.format("%.2f", totalValue));
        System.out.println("------------------------------");
    }

    public void showMessage(String message) { System.out.println(message); }
}
