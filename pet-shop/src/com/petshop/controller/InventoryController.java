package com.petshop.controller;

import com.petshop.exception.BusinessException;
import com.petshop.model.Inventory;
import com.petshop.service.InventoryService;
import com.petshop.view.InventoryManagementView;
import java.util.List;

public class InventoryController {
    private InventoryService inventoryService;
    private InventoryManagementView view;

    public InventoryController(InventoryManagementView view) {
        this.inventoryService = new InventoryService();
        this.view = view;
    }

    public InventoryController(InventoryManagementView view, InventoryService inventoryService) {
        this.inventoryService = inventoryService;
        this.view = view;
    }

    public void run() {
        boolean running = true;
        while (running) {
            int choice = view.showMenu();
            switch (choice) {
                case 1: queryInventory(); break;
                case 2: updateInventory(); break;
                case 3: setWarning(); break;
                case 4: checkWarning(); break;
                case 5: showStatistics(); break;
                case 0: running = false; break;
                default: view.showMessage("无效的选择，请重新输入");
            }
        }
    }

    private void queryInventory() { view.showInventoryList(inventoryService.getAllInventory()); }

    private void updateInventory() {
        try {
            String petId = view.getPetId();
            System.out.print("请输入增加的数量（负数表示减少）：");
            int amount = view.getQuantity();
            inventoryService.updateQuantity(petId, amount);
            view.showMessage("库存更新成功");
        } catch (BusinessException e) { view.showMessage("更新失败：" + e.getMessage()); }
    }

    private void setWarning() {
        try {
            inventoryService.setWarningThreshold(view.getPetId(), view.getWarningThreshold());
            view.showMessage("预警阈值设置成功");
        } catch (BusinessException e) { view.showMessage("设置失败：" + e.getMessage()); }
    }

    private void checkWarning() {
        List<Inventory> warnings = inventoryService.getWarningInventory();
        if (warnings.isEmpty()) view.showMessage("没有库存预警");
        else { view.showMessage("以下库存需要预警："); view.showInventoryList(warnings); }
    }

    private void showStatistics() {
        List<Inventory> inventories = inventoryService.getAllInventory();
        int totalQuantity = 0;
        for (Inventory inv : inventories) totalQuantity += inv.getQuantity();
        view.showInventoryStatistics(totalQuantity, inventoryService.getTotalValue());
    }
}
