package com.petshop.view;

import com.petshop.model.SalesRecord;
import com.petshop.util.TableUtil;
import java.util.List;
import java.util.Scanner;

public class SalesManagementView {
    private Scanner scanner;

    public SalesManagementView(Scanner scanner) {
        this.scanner = scanner;
    }

    public int showMenu() {
        System.out.println("\n---------- 销售管理 ----------");
        System.out.println("1. 添加销售记录");
        System.out.println("2. 查询销售记录");
        System.out.println("3. 列出所有销售记录");
        System.out.println("4. 按时间范围查询");
        System.out.println("0. 返回主菜单");
        System.out.println("------------------------------");
        System.out.print("请选择功能：");
        int choice = -1;
        try { choice = Integer.parseInt(scanner.nextLine()); } catch (NumberFormatException e) {}
        return choice;
    }

    public String[] getSaleInfo() {
        String[] info = new String[2];
        System.out.print("请输入宠物ID："); info[0] = scanner.nextLine();
        System.out.print("请输入销售数量："); info[1] = scanner.nextLine();
        return info;
    }

    public String getRecordId() {
        System.out.print("请输入记录ID：");
        return scanner.nextLine();
    }

    public void showSalesList(List<SalesRecord> records) {
        if (records.isEmpty()) { System.out.println("没有销售记录"); return; }
        String[] headers = {"记录ID", "宠物ID", "宠物名称", "种类", "总价", "销售时间"};
        String[][] data = new String[records.size()][6];
        for (int i = 0; i < records.size(); i++) {
            SalesRecord record = records.get(i);
            data[i][0] = record.getRecordId(); data[i][1] = record.getPetId();
            data[i][2] = record.getPetName(); data[i][3] = record.getSpecies();
            data[i][4] = String.format("%.2f", record.getTotalPrice());
            data[i][5] = record.getSaleTime().toString().substring(0, 19);
        }
        TableUtil.printTable(headers, data);
    }

    public void showSalesStatistics(double totalAmount, int totalQuantity) {
        System.out.println("\n---------- 销售统计 ----------");
        System.out.println("总销售额：" + String.format("%.2f", totalAmount));
        System.out.println("总销售数量：" + totalQuantity);
        System.out.println("------------------------------");
    }

    public void showMessage(String message) { System.out.println(message); }
}
