package com.petshop.gui;

import com.petshop.model.Inventory;
import com.petshop.service.InventoryService;
import com.petshop.dao.PetDao;
import com.petshop.dao.InventoryDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.Comparator;

/**
 * 库存管理界面面板
 */
public class InventoryManagementPanel extends JPanel {
    private InventoryService inventoryService;
    private PetDao petDao;
    private InventoryDao inventoryDao;
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter; // 排序器
    
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
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }
    
    private void createComponents() {
        // 创建标题面板（包含标题和图标按钮）
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("库存管理", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 51, 51));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 创建右上角图标按钮面板
        JPanel iconButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        iconButtonPanel.setOpaque(false);
        
        // 检查预警按钮
        JButton checkWarningButton = createIconButton("⚠️", "检查预警", new Color(255, 152, 0));
        checkWarningButton.addActionListener(e -> checkWarning());
        iconButtonPanel.add(checkWarningButton);
        
        // 库存统计按钮
        JButton statisticsButton = createIconButton("📊", "库存统计", new Color(33, 150, 243));
        statisticsButton.addActionListener(e -> showStatistics());
        iconButtonPanel.add(statisticsButton);
        
        titlePanel.add(iconButtonPanel, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);
        
        // 创建搜索和筛选面板
        createSearchFilterPanel();
        
        // 创建表格
        createInventoryTable();
        
        // 移除底部按钮面板，不再需要
    }
    
    /**
     * 创建搜索和筛选面板
     */
    private void createSearchFilterPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // 搜索框
        JLabel searchLabel = new JLabel("搜索:");
        searchLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        JTextField searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "输入宠物ID或名称");
        
        // 种类筛选
        JLabel typeLabel = new JLabel("种类:");
        typeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"全部", "狗", "猫", "其他"});
        typeCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        
        // 状态筛选
        JLabel statusLabel = new JLabel("状态:");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"全部", "充足", "预警", "缺货"});
        statusCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        
        // 查询按钮
        JButton searchButton = new JButton("查询");
        searchButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        searchButton.addActionListener(e -> performSearch(searchField.getText(), 
                                                            (String) typeCombo.getSelectedItem(),
                                                            (String) statusCombo.getSelectedItem()));
        
        // 刷新按钮
        JButton refreshButton = new JButton("刷新");
        refreshButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            typeCombo.setSelectedIndex(0);
            statusCombo.setSelectedIndex(0);
            loadInventoryData();
        });
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(typeLabel);
        searchPanel.add(typeCombo);
        searchPanel.add(statusLabel);
        searchPanel.add(statusCombo);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);
        
        add(searchPanel, BorderLayout.NORTH);
    }
    
    private void createInventoryTable() {
        String[] columnNames = {"宠物ID", "宠物名称", "种类", "库存数量", "预警阈值", "状态"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 禁止编辑表格
            }
        };
        
        inventoryTable = new JTable(tableModel);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.getTableHeader().setReorderingAllowed(false);
        inventoryTable.setRowHeight(28);
        inventoryTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        inventoryTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        
        // 设置状态列的自定义渲染器
        inventoryTable.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());
        
        // 创建行排序器
        sorter = new TableRowSorter<>(tableModel);
        
        // 设置每列的排序规则
        sorter.setComparator(0, String.CASE_INSENSITIVE_ORDER); // 宠物ID - 字符串排序
        sorter.setComparator(1, String.CASE_INSENSITIVE_ORDER); // 宠物名称 - 字符串排序
        sorter.setComparator(2, String.CASE_INSENSITIVE_ORDER); // 种类 - 字符串排序
        sorter.setComparator(3, (Comparator<Object>) (o1, o2) -> Integer.compare((Integer)o1, (Integer)o2)); // 库存数量 - 整数排序
        sorter.setComparator(4, (Comparator<Object>) (o1, o2) -> Integer.compare((Integer)o1, (Integer)o2)); // 预警阈值 - 整数排序
        sorter.setComparator(5, String.CASE_INSENSITIVE_ORDER); // 状态 - 字符串排序
        
        inventoryTable.setRowSorter(sorter);
        
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // 添加右键菜单
        setupContextMenu();
    }
    
    /**
     * 设置右键菜单
     */
    private void setupContextMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        
        JMenuItem updateItem = new JMenuItem("更新库存");
        updateItem.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        updateItem.addActionListener(e -> updateInventory());
        popupMenu.add(updateItem);
        
        JMenuItem warningItem = new JMenuItem("设置预警");
        warningItem.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        warningItem.addActionListener(e -> setWarning());
        popupMenu.add(warningItem);
        
        inventoryTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }
            
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }
            
            private void showPopup(java.awt.event.MouseEvent e) {
                int row = inventoryTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < inventoryTable.getRowCount()) {
                    inventoryTable.setRowSelectionInterval(row, row);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }
    
    /**
     * 创建图标按钮
     */
    private JButton createIconButton(String icon, String tooltip, Color bgColor) {
        JButton button = new JButton(icon);
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(40, 40));
        return button;
    }
    
    /**
     * 状态单元格渲染器 - 根据库存状态显示不同颜色
     */
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected) {
                String status = value.toString();
                if (status.contains("缺货")) {
                    setBackground(new Color(255, 204, 204)); // 浅红色
                    setForeground(Color.RED);
                } else if (status.contains("预警")) {
                    setBackground(new Color(255, 255, 204)); // 浅黄色
                    setForeground(new Color(255, 140, 0));
                } else {
                    setBackground(new Color(204, 255, 204)); // 浅绿色
                    setForeground(new Color(0, 128, 0));
                }
            } else {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }
            
            setHorizontalAlignment(CENTER);
            return c;
        }
    }
    

    
    /**
     * 执行搜索和筛选
     */
    private void performSearch(String keyword, String type, String status) {
        tableModel.setRowCount(0); // 清空表格
        List<Inventory> inventories = inventoryService.getAllInventory();
        
        for (Inventory inventory : inventories) {
            String petName = "未知";
            String petType = "-";
            com.petshop.model.Pet pet = petDao.findById(inventory.getPetId());
            if (pet != null) {
                petName = pet.getName();
                petType = pet.getSpecies();
            }
            
            // 应用筛选条件
            boolean matchesKeyword = keyword.isEmpty() || 
                                     inventory.getPetId().contains(keyword) ||
                                     petName.contains(keyword);
            boolean matchesType = "全部".equals(type) || type.equals(petType);
            
            String currentStatus = getStatus(inventory.getQuantity(), inventory.getWarningThreshold());
            boolean matchesStatus = "全部".equals(status) || currentStatus.contains(status);
            
            if (matchesKeyword && matchesType && matchesStatus) {
                Object[] row = {
                    inventory.getPetId(),
                    petName,
                    petType,
                    inventory.getQuantity(),
                    inventory.getWarningThreshold(),
                    currentStatus
                };
                tableModel.addRow(row);
            }
        }
        
        if (tableModel.getRowCount() == 0) {
            Toast.showInfo(this, "未找到符合条件的记录");
        } else {
            Toast.showSuccess(this, "查询到 " + tableModel.getRowCount() + " 条记录");
        }
    }
    
    private void loadInventoryData() {
        tableModel.setRowCount(0); // 清空表格
        List<Inventory> inventories = inventoryService.getAllInventory();
        for (Inventory inventory : inventories) {
            String petName = "未知";
            String petType = "-";
            com.petshop.model.Pet pet = petDao.findById(inventory.getPetId());
            if (pet != null) {
                petName = pet.getName();
                petType = pet.getSpecies();
            }
            
            // 计算状态
            String status = getStatus(inventory.getQuantity(), inventory.getWarningThreshold());
            
            Object[] row = {
                inventory.getPetId(),
                petName,
                petType,
                inventory.getQuantity(),
                inventory.getWarningThreshold(),
                status
            };
            tableModel.addRow(row);
        }
    }
    
    /**
     * 获取库存状态
     */
    private String getStatus(int quantity, int threshold) {
        if (quantity <= 0) {
            return "缺货";
        } else if (quantity <= threshold) {
            return "预警";
        } else {
            return "充足";
        }
    }
    
    private void updateInventory() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            Toast.showWarning(this, "请先选择要更新的库存");
            return;
        }
        
        String petId = (String) tableModel.getValueAt(selectedRow, 0);
        // 安全地获取当前库存数量
        Object quantityObj = tableModel.getValueAt(selectedRow, 3); // 库存数量在第4列（索引3）
        int currentQuantity;
        if (quantityObj instanceof Number) {
            currentQuantity = ((Number) quantityObj).intValue();
        } else {
            currentQuantity = Integer.parseInt(quantityObj.toString());
        }
        
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
                Toast.showSuccess(this, "库存更新成功");
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
            Toast.showWarning(this, "请先选择要设置预警的库存");
            return;
        }
        
        String petId = (String) tableModel.getValueAt(selectedRow, 0);
        // 安全地获取当前预警阈值
        Object thresholdObj = tableModel.getValueAt(selectedRow, 4); // 预警阈值在第5列（索引4）
        int currentThreshold;
        if (thresholdObj instanceof Number) {
            currentThreshold = ((Number) thresholdObj).intValue();
        } else {
            currentThreshold = Integer.parseInt(thresholdObj.toString());
        }
        
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
                Toast.showSuccess(this, "预警阈值设置成功");
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
            Toast.showInfo(this, "没有库存预警");
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
        dialog.setSize(450, 320);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
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
        JPanel statsPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        statsPanel.setBackground(new Color(245, 247, 250));
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        Font labelFont = new Font("微软雅黑", Font.PLAIN, 14);
        Font valueFont = new Font("微软雅黑", Font.BOLD, 16);
        
        addStatRow(statsPanel, "总库存数量:", String.valueOf(totalQuantity), labelFont, valueFont);
        addStatRow(statsPanel, "库存种类数:", String.valueOf(totalItems), labelFont, valueFont);
        addStatRow(statsPanel, "库存总价值:", String.format("¥%.2f", totalValue), labelFont, valueFont);
        addStatRow(statsPanel, "预警项目数:", String.valueOf(warningCount), labelFont, valueFont);
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("关闭");
        closeButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    /**
     * 添加统计行
     */
    private void addStatRow(JPanel panel, String label, String value, Font labelFont, Font valueFont) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(labelFont);
        lbl.setForeground(new Color(100, 100, 100));
        
        JLabel val = new JLabel(value);
        val.setFont(valueFont);
        val.setForeground(new Color(51, 153, 255));
        
        panel.add(lbl);
        panel.add(val);
    }
}