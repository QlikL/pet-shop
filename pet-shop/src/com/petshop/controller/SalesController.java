package com.petshop.controller;

import com.petshop.exception.BusinessException;
import com.petshop.model.SalesRecord;
import com.petshop.service.SalesService;
import com.petshop.view.SalesManagementView;
import java.util.List;

public class SalesController {
    private SalesService salesService;
    private SalesManagementView view;

    public SalesController(SalesManagementView view) {
        this.salesService = new SalesService();
        this.view = view;
    }

    public SalesController(SalesManagementView view, SalesService salesService) {
        this.salesService = salesService;
        this.view = view;
    }

    public void run() {
        boolean running = true;
        while (running) {
            int choice = view.showMenu();
            switch (choice) {
                case 1: addSale(); break;
                case 2: querySale(); break;
                case 3: listAllSales(); break;
                case 4: listByTimeRange(); break;
                case 0: running = false; break;
                default: view.showMessage("无效的选择，请重新输入");
            }
        }
    }

    private void addSale() {
        view.showMessage("销售记录已改为自动生成！\n当宠物状态变为'已售'时自动创建销售记录。");
    }

    private void querySale() {
        try {
            SalesRecord record = salesService.getSaleRecord(view.getRecordId());
            view.showMessage("记录ID：" + record.getRecordId());
            view.showMessage("宠物名称：" + record.getPetName());
            view.showMessage("种类：" + record.getSpecies());
            view.showMessage("总价：" + record.getTotalPrice());
            view.showMessage("时间：" + record.getSaleTime());
        } catch (BusinessException e) { view.showMessage("查询失败：" + e.getMessage()); }
    }

    private void listAllSales() { view.showSalesList(salesService.getAllSales()); }

    private void listByTimeRange() { view.showMessage("此功能暂未实现"); }
}
