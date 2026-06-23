package com.petshop.gui;

import com.petshop.model.SalesRecord;
import com.petshop.service.SalesService;
import com.petshop.dao.PetDao;
import com.petshop.dao.InventoryDao;
import com.petshop.dao.SalesRecordDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 销售管理界面面板
 */
public class SalesManagementPanel extends JPanel {
    private SalesService salesService;
    private JTable salesTable;
    private DefaultTableModel tableModel;
    
    public SalesManagementPanel() {
        // 创建DAO实例
        PetDao petDao = new PetDao();
        InventoryDao inventoryDao = new InventoryDao();
        SalesRecordDao salesRecordDao = new SalesRecordDao();
        this.salesService = new SalesService(petDao, inventoryDao, salesRecordDao);
        
        initializePanel();
        createComponents();
        loadSalesData();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }
    
    private void createComponents() {
        // 创建标题面板
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("销售管理", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 51, 51));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);
        
        // 创建表格
        createSalesTable();
        
        // 创建按钮面板
        createButtonPanel();
    }
    
    private void createSalesTable() {
        String[] columnNames = {"记录ID", "宠物ID", "宠物名称", "销售数量", "销售时间", "销售金额"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 禁止编辑表格
            }
        };
        
        salesTable = new JTable(tableModel);
        salesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        salesTable.getTableHeader().setReorderingAllowed(false);
        salesTable.setRowHeight(30);
        salesTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        salesTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        salesTable.getTableHeader().setBackground(new Color(240, 242, 245)); // 浅灰蓝背景
        salesTable.getTableHeader().setForeground(new Color(51, 51, 51)); // 深黑色文字
        salesTable.setGridColor(new Color(230, 230, 230));
        salesTable.setShowGrid(true);
        salesTable.setIntercellSpacing(new Dimension(1, 1));
        
        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 5, 5, 5)
        ));
        
        JButton addButton = new JButton("添加销售记录");
        JButton queryButton = new JButton("查询销售记录");
        JButton timeQueryButton = new JButton("按时间范围查询");
        JButton statisticsButton = new JButton("销售统计");
        JButton refreshButton = new JButton("刷新列表");
        
        // 设置按钮样式
        styleButton(addButton, new Color(76, 175, 80));
        styleButton(queryButton, new Color(33, 150, 243));
        styleButton(timeQueryButton, new Color(156, 39, 176));
        styleButton(statisticsButton, new Color(255, 152, 0));
        styleButton(refreshButton, new Color(96, 125, 139));
        
        addButton.addActionListener(e -> addSalesRecord());
        queryButton.addActionListener(e -> querySalesRecord());
        timeQueryButton.addActionListener(e -> queryByTimeRange());
        statisticsButton.addActionListener(e -> showStatistics());
        refreshButton.addActionListener(e -> loadSalesData());
        
        buttonPanel.add(addButton);
        buttonPanel.add(queryButton);
        buttonPanel.add(timeQueryButton);
        buttonPanel.add(statisticsButton);
        buttonPanel.add(refreshButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * 设置按钮样式
     */
    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(130, 40));
    }
    
    private void loadSalesData() {
        tableModel.setRowCount(0); // 清空表格
        List<SalesRecord> salesRecords = salesService.getAllSales();
        for (SalesRecord record : salesRecords) {
            Object[] row = {
                record.getRecordId(),
                record.getPetId(),
                record.getPetName(),
                record.getQuantity(),
                record.getSaleTime(),
                record.getTotalPrice()
            };
            tableModel.addRow(row);
        }
    }
    
    private void addSalesRecord() {
        // 创建添加销售记录对话框
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "添加销售记录", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField petIdField = new JTextField(20);
        JTextField quantityField = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("宠物ID:"), gbc);
        gbc.gridx = 1;
        panel.add(petIdField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("销售数量:"), gbc);
        gbc.gridx = 1;
        panel.add(quantityField, gbc);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");
        
        saveButton.addActionListener(e -> {
            try {
                String petId = petIdField.getText().trim();
                String quantityStr = quantityField.getText().trim();
                
                if (petId.isEmpty() || quantityStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请填写所有字段", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int quantity = Integer.parseInt(quantityStr);
                salesService.addSale(petId, quantity);
                loadSalesData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "销售记录添加成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "销售数量必须是数字", "错误", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "添加失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void querySalesRecord() {
        // 创建查询销售记录对话框
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "查询销售记录", true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField recordIdField = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("记录ID:"), gbc);
        gbc.gridx = 1;
        panel.add(recordIdField, gbc);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton queryButton = new JButton("查询");
        JButton cancelButton = new JButton("取消");
        
        queryButton.addActionListener(e -> {
            try {
                String recordId = recordIdField.getText().trim();
                if (recordId.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请输入记录ID", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                SalesRecord record = salesService.getSaleRecord(recordId);
                if (record == null) {
                    JOptionPane.showMessageDialog(dialog, "未找到该销售记录", "查询结果", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // 更新表格显示查询结果
                    tableModel.setRowCount(0);
                    Object[] row = {
                        record.getRecordId(),
                        record.getPetId(),
                        record.getPetName(),
                        record.getQuantity(),
                        record.getSaleTime(),
                        record.getTotalPrice()
                    };
                    tableModel.addRow(row);
                    dialog.dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "查询失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(queryButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void queryByTimeRange() {
        // 创建按时间范围查询对话框
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "按时间范围查询", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField startDateField = new JTextField(20);
        JTextField endDateField = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("开始时间 (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        panel.add(startDateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("结束时间 (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        panel.add(endDateField, gbc);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton queryButton = new JButton("查询");
        JButton cancelButton = new JButton("取消");
        
        queryButton.addActionListener(e -> {
            String startDate = startDateField.getText().trim();
            String endDate = endDateField.getText().trim();
            
            if (startDate.isEmpty() || endDate.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "请输入开始和结束时间", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                java.time.LocalDate start = java.time.LocalDate.parse(startDate);
                java.time.LocalDate end = java.time.LocalDate.parse(endDate);
                
                List<SalesRecord> records = salesService.getSalesByTimeRange(start.atStartOfDay(), end.plusDays(1).atStartOfDay());
                
                // 更新表格显示查询结果
                tableModel.setRowCount(0);
                for (SalesRecord record : records) {
                    Object[] row = {
                        record.getRecordId(),
                        record.getPetId(),
                        record.getPetName(),
                        record.getQuantity(),
                        record.getSaleTime(),
                        record.getTotalPrice()
                    };
                    tableModel.addRow(row);
                }
                dialog.dispose();
            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "时间格式错误，请使用yyyy-MM-dd格式", "错误", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "查询失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(queryButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showStatistics() {
        // 创建销售统计对话框
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "销售统计", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        
        // 计算统计数据
        List<SalesRecord> records = salesService.getAllSales();
        double totalAmount = 0;
        int totalQuantity = 0;
        
        for (SalesRecord record : records) {
            totalAmount += record.getTotalPrice();
            totalQuantity += record.getQuantity();
        }

        int totalRecords = records.size();
        
        // 创建统计信息面板
        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        statsPanel.add(new JLabel("销售记录数:"));
        statsPanel.add(new JLabel(String.valueOf(totalRecords)));
        
        statsPanel.add(new JLabel("总销售数量:"));
        statsPanel.add(new JLabel(String.valueOf(totalQuantity)));
        
        statsPanel.add(new JLabel("总销售额:"));
        statsPanel.add(new JLabel(String.format("%.2f", totalAmount)));
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}