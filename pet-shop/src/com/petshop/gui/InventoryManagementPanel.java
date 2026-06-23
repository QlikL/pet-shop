package com.petshop.gui;

import com.petshop.model.Inventory;
import com.petshop.service.InventoryService;
import com.petshop.dao.PetDao;
import com.petshop.dao.InventoryDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 库存管理界面面板
 */
public class InventoryManagementPanel extends JPanel {
    private InventoryService inventoryService;
    private PetDao petDao;
    private InventoryDao inventoryDao;
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    
    public InventoryManagementPanel() {
        // 创建DAO实例
        this.petDao = new PetDao();
        this.inventoryDao = new InventoryDao();
        this.inventoryService = new InventoryService(petDao, inventoryDao);
        
        initializePanel();
        createComponents();
        loadInventoryData();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    private void createComponents() {
        // 创建标题
        JLabel titleLabel = new JLabel("库存管理", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);
        
        // 创建表格
        createInventoryTable();
        
        // 创建按钮面板
        createButtonPanel();
    }
    
    private void createInventoryTable() {
        String[] columnNames = {"宠物ID", "宠物名称", "库存数量", "预警阈值"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 禁止编辑表格
            }
        };
        
        inventoryTable = new JTable(tableModel);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton queryButton = new JButton("查询库存");
        JButton updateButton = new JButton("更新库存");
        JButton warningButton = new JButton("设置预警");
        JButton checkWarningButton = new JButton("检查预警");
        JButton statisticsButton = new JButton("库存统计");
        JButton refreshButton = new JButton("刷新列表");
        
        queryButton.addActionListener(e -> queryInventory());
        updateButton.addActionListener(e -> updateInventory());
        warningButton.addActionListener(e -> setWarning());
        checkWarningButton.addActionListener(e -> checkWarning());
        statisticsButton.addActionListener(e -> showStatistics());
        refreshButton.addActionListener(e -> loadInventoryData());
        
        buttonPanel.add(queryButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(warningButton);
        buttonPanel.add(checkWarningButton);
        buttonPanel.add(statisticsButton);
        buttonPanel.add(refreshButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadInventoryData() {
        tableModel.setRowCount(0); // 清空表格
        List<Inventory> inventories = inventoryService.getAllInventory();
        for (Inventory inventory : inventories) {
            String petName = "未知";
            com.petshop.model.Pet pet = petDao.findById(inventory.getPetId());
            if (pet != null) {
                petName = pet.getName();
            }
            Object[] row = {
                inventory.getPetId(),
                petName,
                inventory.getQuantity(),
                inventory.getWarningThreshold()
            };
            tableModel.addRow(row);
        }
    }
    
    private void queryInventory() {
        // 创建查询库存对话框
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "查询库存", true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField petIdField = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("宠物ID:"), gbc);
        gbc.gridx = 1;
        panel.add(petIdField, gbc);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton queryButton = new JButton("查询");
        JButton cancelButton = new JButton("取消");
        
        queryButton.addActionListener(e -> {
            String petId = petIdField.getText().trim();
            if (petId.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "请输入宠物ID", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Inventory inventory = inventoryDao.findByPetId(petId);
            if (inventory == null) {
                JOptionPane.showMessageDialog(dialog, "未找到该宠物的库存信息", "查询结果", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // 更新表格显示查询结果
                tableModel.setRowCount(0);
                String petName = "未知";
                com.petshop.model.Pet pet = petDao.findById(inventory.getPetId());
                if (pet != null) {
                    petName = pet.getName();
                }
                Object[] row = {
                    inventory.getPetId(),
                    petName,
                    inventory.getQuantity(),
                    inventory.getWarningThreshold()
                };
                tableModel.addRow(row);
                dialog.dispose();
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
    
    private void updateInventory() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要更新的库存", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String petId = (String) tableModel.getValueAt(selectedRow, 0);
        int currentQuantity = (int) tableModel.getValueAt(selectedRow, 2);
        
        // 创建更新库存对话框
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "更新库存", true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel currentLabel = new JLabel("当前库存: " + currentQuantity);
        JTextField amountField = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(currentLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("数量变化 (正数增加，负数减少):"), gbc);
        gbc.gridx = 1;
        panel.add(amountField, gbc);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");
        
        saveButton.addActionListener(e -> {
            try {
                String amountStr = amountField.getText().trim();
                if (amountStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请输入数量变化", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int amount = Integer.parseInt(amountStr);
                inventoryService.updateQuantity(petId, amount);
                loadInventoryData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "库存更新成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "请输入有效的数字", "错误", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "更新失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
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
    
    private void setWarning() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要设置预警的库存", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String petId = (String) tableModel.getValueAt(selectedRow, 0);
        int currentThreshold = (int) tableModel.getValueAt(selectedRow, 3);
        
        // 创建设置预警对话框
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "设置库存预警", true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel currentLabel = new JLabel("当前预警阈值: " + currentThreshold);
        JTextField thresholdField = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(currentLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("新的预警阈值:"), gbc);
        gbc.gridx = 1;
        panel.add(thresholdField, gbc);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");
        
        saveButton.addActionListener(e -> {
            try {
                String thresholdStr = thresholdField.getText().trim();
                if (thresholdStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请输入预警阈值", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int threshold = Integer.parseInt(thresholdStr);
                inventoryService.setWarningThreshold(petId, threshold);
                loadInventoryData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "预警阈值设置成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "请输入有效的数字", "错误", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "设置失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
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
    
    private void checkWarning() {
        List<Inventory> warnings = inventoryService.getWarningInventory();
        if (warnings.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有库存预警", "提示", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // 创建预警列表对话框
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "库存预警列表", true);
            dialog.setSize(500, 300);
            dialog.setLocationRelativeTo(this);
            
            JPanel panel = new JPanel(new BorderLayout());
            
            // 创建表格
            String[] columnNames = {"宠物ID", "宠物名称", "当前库存", "预警阈值"};
            DefaultTableModel warningTableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            JTable warningTable = new JTable(warningTableModel);
            warningTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            
            for (Inventory inventory : warnings) {
                String petName = "未知";
                com.petshop.model.Pet pet = petDao.findById(inventory.getPetId());
                if (pet != null) {
                    petName = pet.getName();
                }
                Object[] row = {
                    inventory.getPetId(),
                    petName,
                    inventory.getQuantity(),
                    inventory.getWarningThreshold()
                };
                warningTableModel.addRow(row);
            }
            
            JScrollPane scrollPane = new JScrollPane(warningTable);
            panel.add(scrollPane, BorderLayout.CENTER);
            
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
    
    private void showStatistics() {
        // 创建库存统计对话框
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "库存统计", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        
        // 计算统计数据
        List<Inventory> inventories = inventoryService.getAllInventory();
        int totalQuantity = 0;
        for (Inventory inventory : inventories) {
            totalQuantity += inventory.getQuantity();
        }
        
        double totalValue = inventoryService.getTotalValue();
        int totalItems = inventories.size();
        int warningCount = inventoryService.getWarningInventory().size();
        
        // 创建统计信息面板
        JPanel statsPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        statsPanel.add(new JLabel("总库存数量:"));
        statsPanel.add(new JLabel(String.valueOf(totalQuantity)));
        
        statsPanel.add(new JLabel("库存种类数:"));
        statsPanel.add(new JLabel(String.valueOf(totalItems)));
        
        statsPanel.add(new JLabel("库存总价值:"));
        statsPanel.add(new JLabel(String.format("%.2f", totalValue)));
        
        statsPanel.add(new JLabel("预警项目数:"));
        statsPanel.add(new JLabel(String.valueOf(warningCount)));
        
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