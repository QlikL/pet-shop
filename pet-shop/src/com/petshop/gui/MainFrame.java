package com.petshop.gui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 主窗口框架类
 * 包含顶部通栏、侧边栏导航、内容区域和状态栏
 */
public class MainFrame extends JFrame {
    private static final int DEFAULT_WIDTH = 1280;
    private static final int DEFAULT_HEIGHT = 800;
    
    private JPanel contentPanel;
    private JLabel statusLabel;
    private JLabel timeLabel;
    private JLabel moduleLabel; // 当前模块名称
    private JLabel userLabel; // 当前用户
    private JPanel navigationPanel;
    private Map<String, JButton> navButtons = new HashMap<>(); // 导航按钮映射
    private String currentModule = "首页"; // 当前模块
    private String currentUser = "管理员"; // 当前用户
    
    public MainFrame() {
        initializeFrame();
        createTopBar(); // 创建顶部通栏
        createNavigationPanel(); // 创建优化后的侧边栏
        createContentPanel();
        createStatusBar();
    }
    
    private void initializeFrame() {
        setTitle("宠物商店管理系统");
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // 应用现代化外观主题
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 设置自定义颜色
        UIManager.put("nimbusBase", new Color(51, 153, 255));
        UIManager.put("nimbusBlueGrey", new Color(204, 204, 204));
        UIManager.put("control", new Color(240, 240, 240));
        
        // 添加窗口关闭确认
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (confirmExit()) {
                    System.exit(0);
                }
            }
        });
    }
    
    /**
     * 创建顶部通栏
     */
    private void createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(51, 153, 255));
        topBar.setPreferredSize(new Dimension(0, 60));
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        
        // 左侧：当前模块名称（垂直居中）
        moduleLabel = new JLabel("首页");
        moduleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        moduleLabel.setForeground(Color.WHITE);
        topBar.add(moduleLabel, BorderLayout.WEST);
        
        // 右侧：用户信息和退出按钮（垂直居中）
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 15); // 右边距15像素
        gbc.anchor = GridBagConstraints.CENTER; // 居中对齐
        
        userLabel = new JLabel(currentUser);
        userLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        rightPanel.add(userLabel, gbc);
        
        JButton logoutButton = new JButton("退出登录");
        logoutButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> logout());
        
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridx = 1;
        gbc.gridy = 0;
        rightPanel.add(logoutButton, gbc);
        
        topBar.add(rightPanel, BorderLayout.EAST);
        
        add(topBar, BorderLayout.NORTH);
    }
    
    private void createNavigationPanel() {
        navigationPanel = new JPanel();
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        navigationPanel.setBackground(new Color(240, 242, 245));
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        navigationPanel.setPreferredSize(new Dimension(200, 0));
        
        // 添加导航按钮 (移除"功能导航"标题)
        addButtonWithIcon("宠物管理", "");
        addButtonWithIcon("库存管理", "");
        addButtonWithIcon("销售管理", "");
        addButtonWithIcon("数据统计", "");
        
        JScrollPane scrollPane = new JScrollPane(navigationPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.WEST);
    }
    
    /**
     * 添加带图标的导航按钮
     */
    private void addButtonWithIcon(String text, String icon) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setPreferredSize(new Dimension(180, 50));
        button.setMaximumSize(new Dimension(180, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        // 移除 setContentAreaFilled(false) 以允许背景色显示
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 设置未选中状态样式 - 深灰色文字
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(51, 51, 51)); // 深黑色
        
        // 添加鼠标悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!button.getBackground().equals(new Color(51, 153, 255))) {
                    button.setBackground(new Color(240, 242, 245));
                    button.setForeground(new Color(51, 51, 51)); // 保持深黑色
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!button.getBackground().equals(new Color(51, 153, 255))) {
                    button.setBackground(Color.WHITE);
                    button.setForeground(new Color(51, 51, 51)); // 恢复深黑色
                }
            }
        });
        
        // 设置选中状态样式 - 蓝色背景配白色文字
        button.addActionListener(e -> {
            // 重置所有按钮样式
            for (JButton btn : navButtons.values()) {
                btn.setBackground(Color.WHITE);
                btn.setForeground(new Color(51, 51, 51)); // 深黑色
            }
            // 高亮当前选中的按钮 - 蓝色背景，白色文字
            button.setBackground(new Color(51, 153, 255));
            button.setForeground(Color.WHITE); // 白色文字在蓝色背景上更清晰
            
            // 切换面板
            switchPanel(text);
        });
        
        navButtons.put(text, button);
        navigationPanel.add(button);
        navigationPanel.add(Box.createVerticalStrut(10));
    }
    
    /**
     * 切换内容面板
     */
    private void switchPanel(String moduleName) {
        currentModule = moduleName;
        updateModuleLabel();
        
        switch (moduleName) {
            case "宠物管理":
                showPetManagement();
                break;
            case "库存管理":
                showInventoryManagement();
                break;
            case "销售管理":
                showSalesManagement();
                break;
            case "数据统计":
                showStatistics();
                break;
        }
    }
    
    /**
     * 更新顶部模块标签
     */
    private void updateModuleLabel() {
        moduleLabel.setText(currentModule);
    }
    
    /**
     * 退出登录
     */
    private void logout() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "确定要退出登录吗？",
            "确认退出",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            // 关闭主窗口
            dispose();
            
            // 重新显示登录窗口
            LoginDialog loginDialog = new LoginDialog(null);
            if (loginDialog.showLogin()) {
                // 登录成功，创建新的主窗口
                currentUser = loginDialog.getUsername();
                MainFrame newMainFrame = new MainFrame();
                newMainFrame.currentUser = currentUser;
                newMainFrame.setVisible(true);
            } else {
                // 取消登录，退出系统
                System.exit(0);
            }
        }
    }
    
    private void createContentPanel() {
        // 创建内容区域
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 添加欢迎信息
        JLabel welcomeLabel = new JLabel("欢迎使用宠物商店管理系统", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        contentPanel.add(welcomeLabel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void showPetManagement() {
        setStatus("正在加载宠物管理界面...");
        PetManagementPanel petPanel = new PetManagementPanel();
        setContentPanel(petPanel);
        setStatus("宠物管理 - 查看和管理宠物信息");
    }
    
    private void showInventoryManagement() {
        setStatus("正在加载库存管理界面...");
        InventoryManagementPanel inventoryPanel = new InventoryManagementPanel();
        setContentPanel(inventoryPanel);
        setStatus("库存管理 - 查看和更新库存信息");
    }
    
    private void showSalesManagement() {
        setStatus("正在加载销售管理界面...");
        SalesManagementPanel salesPanel = new SalesManagementPanel();
        setContentPanel(salesPanel);
        setStatus("销售管理 - 查看和添加销售记录");
    }
    
    private void showStatistics() {
        setStatus("正在加载数据统计界面...");
        StatisticsPanel statisticsPanel = new StatisticsPanel();
        setContentPanel(statisticsPanel);
        setStatus("数据统计 - 查看统计分析图表");
    }
    
    private void createStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        
        statusLabel = new JLabel("就绪");
        timeLabel = new JLabel();
        
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(timeLabel, BorderLayout.EAST);
        
        add(statusPanel, BorderLayout.SOUTH);
        
        // 更新时间显示
        updateTime();
        Timer timer = new Timer(1000, e -> updateTime());
        timer.start();
    }
    
    private void updateTime() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        timeLabel.setText(now.format(formatter));
    }
    
    public void setStatus(String status) {
        statusLabel.setText(status);
    }
    
    public void setContentPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private boolean confirmExit() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "确定要退出系统吗？",
            "确认退出",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
}