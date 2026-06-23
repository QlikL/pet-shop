package com.petshop.gui;

import com.petshop.util.Theme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Toast提示框 - 用于显示操作成功/失败的轻量级提示
 */
public class Toast {
    
    /**
     * 显示成功提示
     */
    public static void showSuccess(Component parent, String message) {
        showToast(parent, message, Theme.SUCCESS);
    }
    
    /**
     * 显示错误提示
     */
    public static void showError(Component parent, String message) {
        showToast(parent, message, Theme.DANGER);
    }
    
    /**
     * 显示警告提示
     */
    public static void showWarning(Component parent, String message) {
        showToast(parent, message, Theme.WARNING);
    }
    
    /**
     * 显示信息提示
     */
    public static void showInfo(Component parent, String message) {
        showToast(parent, message, Theme.PRIMARY);
    }
    
    /**
     * 显示自定义Toast
     */
    private static void showToast(Component parent, String message, Color bgColor) {
        // 创建无边框窗口
        JWindow toastWindow = new JWindow(SwingUtilities.getWindowAncestor(parent));
        toastWindow.setAlwaysOnTop(true);
        
        // 创建内容面板
        JPanel contentPanel = new JPanel(new BorderLayout(10, 0));
        contentPanel.setBackground(bgColor);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        // 消息标签
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(Theme.FONT_BODY);
        messageLabel.setForeground(Color.WHITE);
        contentPanel.add(messageLabel, BorderLayout.CENTER);
        
        toastWindow.add(contentPanel);
        
        // 计算位置 (右上角)
        Window parentWindow = SwingUtilities.getWindowAncestor(parent);
        if (parentWindow != null) {
            int x = parentWindow.getX() + parentWindow.getWidth() - 350;
            int y = parentWindow.getY() + 80;
            toastWindow.setLocation(x, y);
        } else {
            toastWindow.setLocationRelativeTo(null);
        }
        
        // 设置大小并显示
        toastWindow.pack();
        toastWindow.setSize(320, toastWindow.getHeight());
        toastWindow.setVisible(true);
        
        // 3秒后自动关闭
        Timer timer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 淡出效果
                fadeOut(toastWindow);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * 淡出动画
     */
    private static void fadeOut(JWindow window) {
        Timer fadeTimer = new Timer(50, new ActionListener() {
            private float opacity = 1.0f;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity -= 0.1f;
                if (opacity <= 0) {
                    ((Timer) e.getSource()).stop();
                    window.dispose();
                } else {
                    window.setOpacity(opacity);
                }
            }
        });
        fadeTimer.start();
    }
}
