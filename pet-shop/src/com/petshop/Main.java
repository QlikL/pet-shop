package com.petshop;

import com.petshop.gui.LoginDialog;
import com.petshop.gui.MainFrame;
import com.petshop.service.PetService;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // 设置外观主题
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 在事件调度线程中启动GUI
        SwingUtilities.invokeLater(() -> {
            // 创建主窗口（隐藏）
            MainFrame frame = new MainFrame();
            
            // 显示登录对话框
            LoginDialog loginDialog = new LoginDialog(frame);
            boolean loginSuccess = loginDialog.showLogin();
            
            if (loginSuccess) {
                // 登录成功，更新所有宠物的年龄
                try {
                    PetService petService = new PetService();
                    petService.updateAllPetsAge();
                } catch (Exception e) {
                    System.out.println("更新宠物年龄失败：" + e.getMessage());
                }
                
                // 显示主窗口
                frame.setVisible(true);
                // 更新用户信息
                java.lang.reflect.Field userLabelField;
                try {
                    userLabelField = MainFrame.class.getDeclaredField("userLabel");
                    userLabelField.setAccessible(true);
                    JLabel userLabel = (JLabel) userLabelField.get(frame);
                    userLabel.setText(" " + loginDialog.getUsername());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // 登录失败或取消，退出程序
                System.exit(0);
            }
        });
    }
}
