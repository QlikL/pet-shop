package com.petshop.controller;

import com.petshop.dao.InventoryDao;
import com.petshop.dao.PetDao;
import com.petshop.dao.SalesRecordDao;
import com.petshop.service.InventoryService;
import com.petshop.service.PetService;
import com.petshop.service.SalesService;
import com.petshop.view.*;

import java.util.Scanner;

public class MainController {
    private MainMenuView mainMenuView;
    private PetController petController;
    private InventoryController inventoryController;
    private SalesController salesController;
    private StatisticsView statisticsView;

    public MainController() {
        Scanner scanner = new Scanner(System.in);

        // 创建共享的DAO实例
        PetDao petDao = new PetDao();
        InventoryDao inventoryDao = new InventoryDao();
        SalesRecordDao salesRecordDao = new SalesRecordDao();

        // 创建共享的Service实例
        PetService petService = new PetService(petDao, inventoryDao);
        InventoryService inventoryService = new InventoryService(petDao, inventoryDao);
        SalesService salesService = new SalesService(petDao, inventoryDao, salesRecordDao);

        this.mainMenuView = new MainMenuView(scanner);
        this.petController = new PetController(new PetManagementView(scanner), petService);
        this.inventoryController = new InventoryController(new InventoryManagementView(scanner), inventoryService);
        this.salesController = new SalesController(new SalesManagementView(scanner), salesService);
        this.statisticsView = new StatisticsView(scanner);
    }

    public void run() {
        mainMenuView.showMessage("欢迎使用宠物商店管理系统！");
        boolean running = true;
        while (running) {
            int choice = mainMenuView.showMenu();
            switch (choice) {
                case 1: petController.run(); break;
                case 2: inventoryController.run(); break;
                case 3: salesController.run(); break;
                case 4: showStatistics(); break;
                case 0:
                    if (mainMenuView.confirmExit()) {
                        running = false;
                        mainMenuView.showMessage("感谢使用，再见！");
                    }
                    break;
                default: mainMenuView.showMessage("无效的选择，请重新输入");
            }
        }
    }

    private void showStatistics() {
        boolean running = true;
        while (running) {
            int choice = statisticsView.showMenu();
            switch (choice) {
                case 1: inventoryController.run(); break;
                case 2: salesController.run(); break;
                case 0: running = false; break;
                default: statisticsView.showMessage("无效的选择");
            }
        }
    }
}
