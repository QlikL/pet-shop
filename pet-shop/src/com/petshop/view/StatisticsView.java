package com.petshop.view;

import java.util.Scanner;

public class StatisticsView {
    private Scanner scanner;

    public StatisticsView(Scanner scanner) {
        this.scanner = scanner;
    }

    public int showMenu() {
        System.out.println("\n---------- 数据统计 ----------");
        System.out.println("1. 库存统计");
        System.out.println("2. 销售统计");
        System.out.println("0. 返回主菜单");
        System.out.println("------------------------------");
        System.out.print("请选择功能：");
        int choice = -1;
        try { choice = Integer.parseInt(scanner.nextLine()); } catch (NumberFormatException e) {}
        return choice;
    }

    public void showMessage(String message) { System.out.println(message); }
}
