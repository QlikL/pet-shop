package com.petshop.gui;

import com.petshop.model.SalesRecord;
import com.petshop.service.SalesService;
import com.petshop.dao.PetDao;
import com.petshop.dao.InventoryDao;
import com.petshop.dao.SalesRecordDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.Comparator;

/**
 * 销售管理界面面板
 */
public class SalesManagementPanel extends JPanel {
    private SalesService salesService;
    private JTable salesTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter; // 排序器
    private JPanel summaryPanel; // 汇总面板
    private JLabel totalQuantityLabel; // 总数量标签
    private JLabel totalAmountLabel; // 总金额标签
    
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
        // 创建标题面板（包含标题和图标按钮）
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("销售管理", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 51, 51));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 创建右上角按钮面板
        JPanel iconButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        iconButtonPanel.setOpaque(false);
        
        // 添加销售记录按钮
        JButton addButton = createTextButton("添加", "添加销售记录", Color.WHITE);
        addButton.addActionListener(e -> addSalesRecord());
        iconButtonPanel.add(addButton);
        
        // 查询销售记录按钮
        JButton queryButton = createTextButton("查询", "查询销售记录", Color.WHITE);
        queryButton.addActionListener(e -> querySalesRecord());
        iconButtonPanel.add(queryButton);
        
        // 刷新列表按钮
        JButton refreshButton = createTextButton("刷新", "刷新列表", Color.WHITE);
        refreshButton.addActionListener(e -> loadSalesData());
        iconButtonPanel.add(refreshButton);
        
        titlePanel.add(iconButtonPanel, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);
        
        // 创建表格
        createSalesTable();
        
        // 移除底部按钮面板，不再需要
    }
    
    private void createSalesTable() {
        String[] columnNames = {"记录ID", "宠物ID", "宠物品种", "销售数量", "销售时间", "销售金额"};
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
        
        // 创建行排序器
        sorter = new TableRowSorter<>(tableModel);
        
        // 设置每列的排序规则
        sorter.setComparator(0, String.CASE_INSENSITIVE_ORDER); // 记录ID - 字符串排序
        sorter.setComparator(1, String.CASE_INSENSITIVE_ORDER); // 宠物ID - 字符串排序
        sorter.setComparator(2, String.CASE_INSENSITIVE_ORDER); // 宠物品种 - 字符串排序
        sorter.setComparator(3, (Comparator<Object>) (o1, o2) -> Integer.compare((Integer)o1, (Integer)o2)); // 销售数量 - 整数排序
        sorter.setComparator(4, (Comparator<Object>) (o1, o2) -> {
            if (o1 instanceof java.time.LocalDateTime && o2 instanceof java.time.LocalDateTime) {
                return ((java.time.LocalDateTime)o1).compareTo((java.time.LocalDateTime)o2);
            }
            return String.CASE_INSENSITIVE_ORDER.compare(o1.toString(), o2.toString());
        }); // 销售时间 - LocalDateTime排序
        sorter.setComparator(5, (Comparator<Object>) (o1, o2) -> {
            try {
                // 尝试将值转换为Double
                double val1 = (o1 instanceof Number) ? ((Number) o1).doubleValue() : Double.parseDouble(o1.toString());
                double val2 = (o2 instanceof Number) ? ((Number) o2).doubleValue() : Double.parseDouble(o2.toString());
                return Double.compare(val1, val2);
            } catch (NumberFormatException e) {
                // 如果无法解析（如空字符串），按字符串比较
                return o1.toString().compareTo(o2.toString());
            }
        }); // 销售金额 - 双精度排序
        
        salesTable.setRowSorter(sorter);
        
        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        add(scrollPane, BorderLayout.CENTER);
        
        // 创建底部汇总面板
        createSummaryPanel();
    }
    

    
    /**
     * 创建底部汇总面板
     */
    private void createSummaryPanel() {
        summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        summaryPanel.setBackground(new Color(248, 249, 250));
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        Font labelFont = new Font("微软雅黑", Font.PLAIN, 14);
        Font valueFont = new Font("微软雅黑", Font.BOLD, 15);
        
        JLabel totalQtyLabel = new JLabel("总销售数量:");
        totalQtyLabel.setFont(labelFont);
        totalQtyLabel.setForeground(new Color(100, 100, 100));
        
        totalQuantityLabel = new JLabel("0");
        totalQuantityLabel.setFont(valueFont);
        totalQuantityLabel.setForeground(new Color(76, 175, 80));
        
        JLabel totalAmtLabel = new JLabel("总销售金额:");
        totalAmtLabel.setFont(labelFont);
        totalAmtLabel.setForeground(new Color(100, 100, 100));
        
        totalAmountLabel = new JLabel("¥0.00");
        totalAmountLabel.setFont(valueFont);
        totalAmountLabel.setForeground(new Color(33, 150, 243));
        
        summaryPanel.add(totalQtyLabel);
        summaryPanel.add(totalQuantityLabel);
        summaryPanel.add(Box.createHorizontalStrut(20));
        summaryPanel.add(totalAmtLabel);
        summaryPanel.add(totalAmountLabel);
        
        add(summaryPanel, BorderLayout.SOUTH);
    }
    
    /**
     * 创建文字按钮
     */
    private JButton createTextButton(String text, String tooltip, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(70, 35));
        return button;
    }
    
    private void loadSalesData() {
        tableModel.setRowCount(0); // 清空表格
        List<SalesRecord> salesRecords = salesService.getAllSales();
        
        int totalQuantity = 0;
        double totalAmount = 0.0;
        
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
            
            // 累加汇总数据
            totalQuantity += record.getQuantity();
            totalAmount += record.getTotalPrice();
        }
        
        // 更新汇总面板（不受排序影响，始终显示）
        totalQuantityLabel.setText(String.valueOf(totalQuantity));
        totalAmountLabel.setText(String.format("¥%.2f", totalAmount));
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