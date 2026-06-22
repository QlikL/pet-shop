package com.petshop.view;

import java.util.Scanner;

public class MainMenuView {
    private Scanner scanner;

    public MainMenuView(Scanner scanner) {
        this.scanner = scanner;
    }

    public int showMenu() {
        System.out.println("\n========================================");
        System.out.println("          宠物商店管理系统");
        System.out.println("========================================");
        System.out.println("1. 宠物管理");
        System.out.println("2. 库存管理");
        System.out.println("3. 销售管理");
        System.out.println("4. 数据统计");
        System.out.println("0. 退出系统");
        System.out.println("========================================");
        System.out.print("请选择功能：");
        int choice = -1;
        try { choice = Integer.parseInt(scanner.nextLine()); } catch (NumberFormatException e) {}
        return choice;
    }

    public boolean confirmExit() {
        System.out.print("确定要退出系统吗？(y/n): ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }

    public void showMessage(String message) { System.out.println(message); }

    public void waitForEnter() {
        System.out.print("\n按回车键继续...");
        scanner.nextLine();
    }
}
