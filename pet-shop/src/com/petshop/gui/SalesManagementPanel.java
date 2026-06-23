package com.petshop.gui;

import com.petshop.model.SalesRecord;
import com.petshop.service.SalesService;
import com.petshop.dao.PetDao;
import com.petshop.dao.InventoryDao;
import com.petshop.dao.SalesRecordDao;
import com.petshop.util.Theme;

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
        setLayout(new BorderLayout(12, 12));
        setBackground(Theme.BG_SECONDARY);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
    }
    
    private void createComponents() {
        // 创建标题面板（包含标题和图标按钮）
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        
        JLabel titleLabel = Theme.createTitleLabel("销售管理");
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // 创建右上角按钮面板
        JPanel iconButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        iconButtonPanel.setOpaque(false);
        
        // 添加销售记录按钮
        JButton addButton = Theme.createPrimaryButton("添加记录");
        addButton.addActionListener(e -> addSalesRecord());
        iconButtonPanel.add(addButton);
        
        // 查询销售记录按钮
        JButton queryButton = Theme.createSecondaryButton("查询");
        queryButton.addActionListener(e -> querySalesRecord());
        iconButtonPanel.add(queryButton);
        
        // 刷新列表按钮
        JButton refreshButton = Theme.createSecondaryButton("刷新");
        refreshButton.addActionListener(e -> loadSalesData());
        iconButtonPanel.add(refreshButton);
        
        titlePanel.add(iconButtonPanel, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);
        
        // 创建表格
        createSalesTable();
    }
    
    private void createSalesTable() {
        String[] columnNames = {"记录ID", "宠物ID", "宠物品种", "种类", "销售时间", "销售金额"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 禁止编辑表格
            }
        };
        
        salesTable = new JTable(tableModel);
        salesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        salesTable.getTableHeader().setReorderingAllowed(false);
        
        // 使用Theme美化表格
        Theme.styleTable(salesTable);
        
        // 创建行排序器
        sorter = new TableRowSorter<>(tableModel);
        
        // 设置每列的排序规则
        sorter.setComparator(0, String.CASE_INSENSITIVE_ORDER);
        sorter.setComparator(1, String.CASE_INSENSITIVE_ORDER);
        sorter.setComparator(2, String.CASE_INSENSITIVE_ORDER);
        sorter.setComparator(3, String.CASE_INSENSITIVE_ORDER);
        sorter.setComparator(4, (Comparator<Object>) (o1, o2) -> {
            if (o1 instanceof java.time.LocalDateTime && o2 instanceof java.time.LocalDateTime) {
                return ((java.time.LocalDateTime)o1).compareTo((java.time.LocalDateTime)o2);
            }
            return String.CASE_INSENSITIVE_ORDER.compare(o1.toString(), o2.toString());
        });
        sorter.setComparator(5, (Comparator<Object>) (o1, o2) -> {
            try {
                double val1 = (o1 instanceof Number) ? ((Number) o1).doubleValue() : Double.parseDouble(o1.toString());
                double val2 = (o2 instanceof Number) ? ((Number) o2).doubleValue() : Double.parseDouble(o2.toString());
                return Double.compare(val1, val2);
            } catch (NumberFormatException e) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        
        salesTable.setRowSorter(sorter);
        
        // 交替行颜色
        salesTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Theme.BG_PRIMARY : Theme.BG_TERTIARY);
                    setForeground(Theme.TEXT_PRIMARY);
                } else {
                    setBackground(Theme.PRIMARY);
                    setForeground(Color.WHITE);
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER_LIGHTER, 1));
        scrollPane.getViewport().setBackground(Theme.BG_PRIMARY);
        add(scrollPane, BorderLayout.CENTER);
        
        // 创建底部汇总面板
        createSummaryPanel();
    }
    

    
    /**
     * 创建底部汇总面板
     */
    private void createSummaryPanel() {
        summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 12));
        summaryPanel.setBackground(Theme.BG_PRIMARY);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER_LIGHT),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        
        JLabel totalQtyLabel = Theme.createBodyLabel("总销售数量:");
        totalQuantityLabel = Theme.createSubtitleLabel("0");
        totalQuantityLabel.setForeground(Theme.SUCCESS);
        
        JLabel totalAmtLabel = Theme.createBodyLabel("总销售金额:");
        totalAmountLabel = Theme.createSubtitleLabel("¥0.00");
        totalAmountLabel.setForeground(Theme.PRIMARY);
        
        summaryPanel.add(totalQtyLabel);
        summaryPanel.add(totalQuantityLabel);
        summaryPanel.add(Box.createHorizontalStrut(24));
        summaryPanel.add(totalAmtLabel);
        summaryPanel.add(totalAmountLabel);
        
        add(summaryPanel, BorderLayout.SOUTH);
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
                record.getSpecies(),
                record.getSaleTime(),
                record.getTotalPrice()
            };
            tableModel.addRow(row);
            
            // 累加汇总数据（根据销售金额正负判断是增加还是减少）
            if (record.getTotalPrice() > 0) {
                totalQuantity += 1; // 每条正数记录代表一只宠物
            } else {
                totalQuantity -= 1; // 每条负数记录代表回退了一只宠物
            }
            totalAmount += record.getTotalPrice();
        }
        
        // 更新汇总面板（不受排序影响，始终显示）
        totalQuantityLabel.setText(String.valueOf(totalQuantity));
        totalAmountLabel.setText(String.format("¥%.2f", totalAmount));
    }
    
    private void addSalesRecord() {
        // 提示用户：销售记录由宠物状态变更自动生成
        com.petshop.util.DialogUtil.showInfo(this, "销售记录已改为自动生成！\n\n当您在宠物管理中将宠物状态改为'已售'时，\n会自动创建销售记录。\n当将'已售'宠物改为其他状态时，\n会自动删除对应的销售记录。");
    }
    
    private void querySalesRecord() {
        // 创建查询销售记录对话框
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "查询销售记录", true);
        dialog.setSize(380, 220);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = Theme.createDialogPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField recordIdField = Theme.createTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(Theme.createBodyLabel("记录ID:"), gbc);
        gbc.gridx = 1;
        panel.add(recordIdField, gbc);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        buttonPanel.setOpaque(false);
        JButton queryButton = Theme.createPrimaryButton("查询");
        JButton cancelButton = Theme.createSecondaryButton("取消");
        
        queryButton.addActionListener(e -> {
            try {
                String recordId = recordIdField.getText().trim();
                if (recordId.isEmpty()) {
                    com.petshop.util.DialogUtil.showError(dialog, "请输入记录ID");
                    return;
                }
                
                SalesRecord record = salesService.getSaleRecord(recordId);
                if (record == null) {
                    com.petshop.util.DialogUtil.showInfo(dialog, "查询结果", "未找到该销售记录");
                } else {
                    // 更新表格显示查询结果
                    tableModel.setRowCount(0);
                    Object[] row = {
                        record.getRecordId(),
                        record.getPetId(),
                        record.getPetName(),
                        record.getSpecies(),
                        record.getSaleTime(),
                        record.getTotalPrice()
                    };
                    tableModel.addRow(row);
                    dialog.dispose();
                }
            } catch (Exception ex) {
                com.petshop.util.DialogUtil.showError(dialog, "查询失败：" + ex.getMessage());
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
        dialog.setSize(440, 280);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = Theme.createDialogPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField startDateField = Theme.createTextField(20);
        JTextField endDateField = Theme.createTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(Theme.createBodyLabel("开始时间 (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        panel.add(startDateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(Theme.createBodyLabel("结束时间 (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        panel.add(endDateField, gbc);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        buttonPanel.setOpaque(false);
        JButton queryButton = Theme.createPrimaryButton("查询");
        JButton cancelButton = Theme.createSecondaryButton("取消");
        
        queryButton.addActionListener(e -> {
            String startDate = startDateField.getText().trim();
            String endDate = endDateField.getText().trim();
            
            if (startDate.isEmpty() || endDate.isEmpty()) {
                com.petshop.util.DialogUtil.showError(dialog, "请输入开始和结束时间");
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
                        record.getSpecies(),
                        record.getSaleTime(),
                        record.getTotalPrice()
                    };
                    tableModel.addRow(row);
                }
                dialog.dispose();
            } catch (java.time.format.DateTimeParseException ex) {
                com.petshop.util.DialogUtil.showError(dialog, "时间格式错误，请使用yyyy-MM-dd格式");
            } catch (Exception ex) {
                com.petshop.util.DialogUtil.showError(dialog, "查询失败：" + ex.getMessage());
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
        panel.setBackground(Theme.BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 计算统计数据
        List<SalesRecord> records = salesService.getAllSales();
        double totalAmount = 0;
        int totalQuantity = 0;
        
        for (SalesRecord record : records) {
            totalAmount += record.getTotalPrice();
            if (record.getTotalPrice() > 0) {
                totalQuantity += 1; // 每条正数记录代表一只宠物
            } else {
                totalQuantity -= 1; // 每条负数记录代表回退了一只宠物
            }
        }

        int totalRecords = records.size();
        
        // 创建统计信息面板
        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 12, 12));
        statsPanel.setBackground(Theme.BG_TERTIARY);
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_LIGHTER),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel recordLabel = Theme.createBodyLabel("销售记录数:");
        recordLabel.setForeground(Theme.TEXT_SECONDARY);
        JLabel recordVal = Theme.createSubtitleLabel(String.valueOf(totalRecords));
        recordVal.setForeground(Theme.PRIMARY);
        statsPanel.add(recordLabel);
        statsPanel.add(recordVal);
        
        JLabel qtyLabel = Theme.createBodyLabel("总销售数量:");
        qtyLabel.setForeground(Theme.TEXT_SECONDARY);
        JLabel qtyVal = Theme.createSubtitleLabel(String.valueOf(totalQuantity));
        qtyVal.setForeground(Theme.SUCCESS);
        statsPanel.add(qtyLabel);
        statsPanel.add(qtyVal);
        
        JLabel amtLabel = Theme.createBodyLabel("总销售额:");
        amtLabel.setForeground(Theme.TEXT_SECONDARY);
        JLabel amtVal = Theme.createSubtitleLabel(String.format("¥%.2f", totalAmount));
        amtVal.setForeground(Theme.PRIMARY);
        statsPanel.add(amtLabel);
        statsPanel.add(amtVal);
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        buttonPanel.setOpaque(false);
        JButton closeButton = Theme.createSecondaryButton("关闭");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}