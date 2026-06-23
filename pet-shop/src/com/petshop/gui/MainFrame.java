package com.petshop.gui;

import javax.swing.*;
import java.awt.*;

/**
 * 主窗口框架类
 * 包含标题栏、菜单栏、内容区域和状态栏
 */
public class MainFrame extends JFrame {
    private static final int DEFAULT_WIDTH = 1024;
    private static final int DEFAULT_HEIGHT = 768;
    
    private JPanel contentPanel;
    private JLabel statusLabel;
    private JLabel timeLabel;
    private JPanel navigationPanel;
    
    public MainFrame() {
        initializeFrame();
        createMenuBar();
        createNavigationPanel();
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
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // 宠物管理菜单
        JMenu petMenu = new JMenu("宠物管理");
        petMenu.add(new JMenuItem("查看宠物列表"));
        petMenu.add(new JMenuItem("添加宠物"));
        petMenu.add(new JMenuItem("查询宠物"));
        petMenu.addSeparator();
        petMenu.add(new JMenuItem("按种类筛选"));
        petMenu.add(new JMenuItem("按状态筛选"));
        
        // 库存管理菜单
        JMenu inventoryMenu = new JMenu("库存管理");
        inventoryMenu.add(new JMenuItem("查看库存列表"));
        inventoryMenu.add(new JMenuItem("查询库存"));
        inventoryMenu.add(new JMenuItem("更新库存"));
        inventoryMenu.add(new JMenuItem("设置预警"));
        inventoryMenu.add(new JMenuItem("检查预警"));
        inventoryMenu.add(new JMenuItem("库存统计"));
        
        // 销售管理菜单
        JMenu salesMenu = new JMenu("销售管理");
        salesMenu.add(new JMenuItem("查看销售记录"));
        salesMenu.add(new JMenuItem("添加销售记录"));
        salesMenu.add(new JMenuItem("查询销售记录"));
        salesMenu.add(new JMenuItem("按时间范围查询"));
        salesMenu.add(new JMenuItem("销售统计"));
        
        // 数据统计菜单
        JMenu statisticsMenu = new JMenu("数据统计");
        statisticsMenu.add(new JMenuItem("库存统计"));
        statisticsMenu.add(new JMenuItem("销售统计"));
        
        // 系统菜单
        JMenu systemMenu = new JMenu("系统");
        systemMenu.add(new JMenuItem("关于"));
        systemMenu.addSeparator();
        systemMenu.add(new JMenuItem("退出"));
        
        menuBar.add(petMenu);
        menuBar.add(inventoryMenu);
        menuBar.add(salesMenu);
        menuBar.add(statisticsMenu);
        menuBar.add(systemMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void createNavigationPanel() {
        navigationPanel = new JPanel();
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        navigationPanel.setPreferredSize(new Dimension(150, 0));
        
        JLabel titleLabel = new JLabel("功能导航");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton petButton = new JButton("宠物管理");
        JButton inventoryButton = new JButton("库存管理");
        JButton salesButton = new JButton("销售管理");
        JButton statisticsButton = new JButton("数据统计");
        
        // 设置按钮属性
        Dimension buttonSize = new Dimension(130, 35);
        petButton.setPreferredSize(buttonSize);
        inventoryButton.setPreferredSize(buttonSize);
        salesButton.setPreferredSize(buttonSize);
        statisticsButton.setPreferredSize(buttonSize);
        
        petButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        inventoryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        salesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        statisticsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 添加按钮事件监听器
        petButton.addActionListener(e -> showPetManagement());
        inventoryButton.addActionListener(e -> showInventoryManagement());
        salesButton.addActionListener(e -> showSalesManagement());
        statisticsButton.addActionListener(e -> showStatistics());
        
        navigationPanel.add(titleLabel);
        navigationPanel.add(Box.createVerticalStrut(20));
        navigationPanel.add(petButton);
        navigationPanel.add(Box.createVerticalStrut(10));
        navigationPanel.add(inventoryButton);
        navigationPanel.add(Box.createVerticalStrut(10));
        navigationPanel.add(salesButton);
        navigationPanel.add(Box.createVerticalStrut(10));
        navigationPanel.add(statisticsButton);
        
        add(navigationPanel, BorderLayout.WEST);
    }
    
    private void createContentPanel() {
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
        setStatus("宠物管理");
    }
    
    private void showInventoryManagement() {
        setStatus("正在加载库存管理界面...");
        InventoryManagementPanel inventoryPanel = new InventoryManagementPanel();
        setContentPanel(inventoryPanel);
        setStatus("库存管理");
    }
    
    private void showSalesManagement() {
        setStatus("正在加载销售管理界面...");
        SalesManagementPanel salesPanel = new SalesManagementPanel();
        setContentPanel(salesPanel);
        setStatus("销售管理");
    }
    
    private void showStatistics() {
        setStatus("正在加载数据统计界面...");
        StatisticsPanel statisticsPanel = new StatisticsPanel();
        setContentPanel(statisticsPanel);
        setStatus("数据统计");
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
        timeLabel.setText("管理员 | " + now.format(formatter));
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