package com.petshop.util;

import javax.swing.*;
import java.awt.*;

/**
 * 自定义对话框工具类 - 提供美观的对话框
 */
public class DialogUtil {
    
    /**
     * 显示提示信息对话框
     */
    public static void showInfo(Component parent, String message) {
        showInfo(parent, "提示", message);
    }
    
    /**
     * 显示提示信息对话框（带标题）
     */
    public static void showInfo(Component parent, String title, String message) {
        CustomDialog dialog = new CustomDialog(parent, title, message, DialogType.INFO);
        dialog.setVisible(true);
    }
    
    /**
     * 显示成功信息对话框
     */
    public static void showSuccess(Component parent, String message) {
        showSuccess(parent, "成功", message);
    }
    
    /**
     * 显示成功信息对话框（带标题）
     */
    public static void showSuccess(Component parent, String title, String message) {
        CustomDialog dialog = new CustomDialog(parent, title, message, DialogType.SUCCESS);
        dialog.setVisible(true);
    }
    
    /**
     * 显示警告信息对话框
     */
    public static void showWarning(Component parent, String message) {
        showWarning(parent, "警告", message);
    }
    
    /**
     * 显示警告信息对话框（带标题）
     */
    public static void showWarning(Component parent, String title, String message) {
        CustomDialog dialog = new CustomDialog(parent, title, message, DialogType.WARNING);
        dialog.setVisible(true);
    }
    
    /**
     * 显示错误信息对话框
     */
    public static void showError(Component parent, String message) {
        showError(parent, "错误", message);
    }
    
    /**
     * 显示错误信息对话框（带标题）
     */
    public static void showError(Component parent, String title, String message) {
        CustomDialog dialog = new CustomDialog(parent, title, message, DialogType.ERROR);
        dialog.setVisible(true);
    }
    
    /**
     * 显示确认对话框
     * @return true 如果用户点击了确认按钮
     */
    public static boolean showConfirm(Component parent, String message) {
        return showConfirm(parent, "确认", message);
    }
    
    /**
     * 显示确认对话框（带标题）
     * @return true 如果用户点击了确认按钮
     */
    public static boolean showConfirm(Component parent, String title, String message) {
        CustomDialog dialog = new CustomDialog(parent, title, message, DialogType.CONFIRM);
        dialog.setVisible(true);
        return dialog.isConfirmed();
    }
    
    /**
     * 对话框类型枚举
     */
    public enum DialogType {
        INFO, SUCCESS, WARNING, ERROR, CONFIRM
    }
    
    /**
     * 自定义对话框类
     */
    private static class CustomDialog extends JDialog {
        private boolean confirmed = false;
        
        public CustomDialog(Component parent, String title, String message, DialogType type) {
            super(getFrame(parent), title, true);
            initializeDialog();
            createComponents(title, message, type);
        }
        
        private static Frame getFrame(Component parent) {
            if (parent instanceof Frame) {
                return (Frame) parent;
            }
            return (Frame) SwingUtilities.getWindowAncestor(parent);
        }
        
        private void initializeDialog() {
            setSize(380, 200);
            setLocationRelativeTo(getOwner());
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setUndecorated(false);
        }
        
        private void createComponents(String title, String message, DialogType type) {
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(Theme.BG_PRIMARY);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
            
            // 消息面板
            JPanel messagePanel = new JPanel(new BorderLayout());
            messagePanel.setOpaque(false);
            messagePanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 24, 0));
            
            // 消息文本
            JLabel messageLabel = new JLabel("<html><div style='width:280px;'>" + message + "</div></html>");
            messageLabel.setFont(Theme.FONT_BODY);
            messageLabel.setForeground(Theme.TEXT_PRIMARY);
            messagePanel.add(messageLabel, BorderLayout.CENTER);
            
            mainPanel.add(messagePanel, BorderLayout.CENTER);
            
            // 按钮面板
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
            buttonPanel.setOpaque(false);
            
            switch (type) {
                case CONFIRM:
                    JButton confirmButton = Theme.createPrimaryButton("确认");
                    JButton cancelButton = Theme.createSecondaryButton("取消");
                    
                    confirmButton.addActionListener(e -> {
                        confirmed = true;
                        dispose();
                    });
                    
                    cancelButton.addActionListener(e -> {
                        confirmed = false;
                        dispose();
                    });
                    
                    buttonPanel.add(confirmButton);
                    buttonPanel.add(cancelButton);
                    break;
                    
                case ERROR:
                    JButton errorButton = Theme.createDangerButton("关闭");
                    errorButton.addActionListener(e -> dispose());
                    buttonPanel.add(errorButton);
                    break;
                    
                case WARNING:
                    JButton warningButton = Theme.createButton("我知道了", Theme.WARNING);
                    warningButton.addActionListener(e -> dispose());
                    buttonPanel.add(warningButton);
                    break;
                    
                case SUCCESS:
                    JButton successButton = Theme.createSuccessButton("确定");
                    successButton.addActionListener(e -> dispose());
                    buttonPanel.add(successButton);
                    break;
                    
                default: // INFO
                    JButton infoButton = Theme.createPrimaryButton("确定");
                    infoButton.addActionListener(e -> dispose());
                    buttonPanel.add(infoButton);
                    break;
            }
            
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            setContentPane(mainPanel);
        }
        
        public boolean isConfirmed() {
            return confirmed;
        }
    }
}
