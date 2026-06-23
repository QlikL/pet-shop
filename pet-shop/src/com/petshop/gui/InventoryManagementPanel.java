package com.petshop.gui;

import com.petshop.model.Inventory;
import com.petshop.service.InventoryService;
import com.petshop.dao.PetDao;
import com.petshop.dao.InventoryDao;
import com.petshop.util.DialogUtil;
import com.petshop.util.Theme;

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
        setLayout(new BorderLayout(12, 12));
        setBackground(Theme.BG_SECONDARY);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
    }
    
    private void createComponents() {
        // 创建标题面板（包含标题和图标按钮）
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        
        JLabel titleLabel = Theme.createTitleLabel("库存管理");
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // 创建右上角图标按钮面板
        JPanel iconButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        iconButtonPanel.setOpaque(false);
        
        // 检查预警按钮
        JButton checkWarningButton = Theme.createIconButton("⚠", "检查预警", Theme.WARNING);
        checkWarningButton.addActionListener(e -> checkWarning());
        iconButtonPanel.add(checkWarningButton);
        
        // 库存统计按钮
        JButton statisticsButton = Theme.createIconButton("📊", "库存统计", Theme.PRIMARY);
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
        searchPanel.setBackground(Theme.BG_PRIMARY);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_LIGHT),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        // 搜索框
        JLabel searchLabel = Theme.createBodyLabel("搜索:");
        JTextField searchField = Theme.createTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "输入宠物ID或名称");
        
        // 种类筛选
        JLabel typeLabel = Theme.createBodyLabel("种类:");
        JComboBox<String> typeCombo = Theme.createComboBox(new String[]{"全部", "狗", "猫", "其他"});
        
        // 状态筛选
        JLabel statusLabel = Theme.createBodyLabel("状态:");
        JComboBox<String> statusCombo = Theme.createComboBox(new String[]{"全部", "充足", "预警", "缺货"});
        
        // 查询按钮
        JButton searchButton = Theme.createPrimaryButton("查询");
        searchButton.addActionListener(e -> performSearch(searchField.getText(), 
                                                            (String) typeCombo.getSelectedItem(),
                                                            (String) statusCombo.getSelectedItem()));
        
        // 刷新按钮
        JButton refreshButton = Theme.createSecondaryButton("刷新");
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            typeCombo.setSelectedIndex(0);
            statusCombo.setSelectedIndex(0);
            loadInventoryData();
        });
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(8));
        searchPanel.add(typeLabel);
        searchPanel.add(typeCombo);
        searchPanel.add(Box.createHorizontalStrut(8));
        searchPanel.add(statusLabel);
        searchPanel.add(statusCombo);
        searchPanel.add(Box.createHorizontalStrut(8));
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);
        
        add(searchPanel, BorderLayout.NORTH);
    }
    
    private void createInventoryTable() {
        String[] columnNames = {"种类", "品种", "库存数量", "预警阈值", "状态"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 禁止编辑表格
            }
        };
        
        inventoryTable = new JTable(tableModel);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.getTableHeader().setReorderingAllowed(false);
        
        // 使用Theme美化表格
        Theme.styleTable(inventoryTable);
        
        // 设置状态列的自定义渲染器
        inventoryTable.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());
        
        // 创建行排序器
        sorter = new TableRowSorter<>(tableModel);
        
        // 设置每列的排序规则
        sorter.setComparator(0, String.CASE_INSENSITIVE_ORDER); // 种类 - 字符串排序
        sorter.setComparator(1, String.CASE_INSENSITIVE_ORDER); // 品种 - 字符串排序
        sorter.setComparator(2, (Comparator<Object>) (o1, o2) -> Integer.compare((Integer)o1, (Integer)o2)); // 库存数量 - 整数排序
        sorter.setComparator(3, (Comparator<Object>) (o1, o2) -> Integer.compare((Integer)o1, (Integer)o2)); // 预警阈值 - 整数排序
        sorter.setComparator(4, String.CASE_INSENSITIVE_ORDER); // 状态 - 字符串排序
        
        inventoryTable.setRowSorter(sorter);
        
        // 交替行颜色
        inventoryTable.setDefaultRenderer(Object.class, Theme.createAlternatingRowRenderer());
        
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER_LIGHTER, 1));
        scrollPane.getViewport().setBackground(Theme.BG_PRIMARY);
        add(scrollPane, BorderLayout.CENTER);
        
        // 添加右键菜单
        setupContextMenu();
    }
    
    /**
     * 设置右键菜单
     */
    private void setupContextMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBackground(Theme.BG_PRIMARY);
        popupMenu.setBorder(BorderFactory.createLineBorder(Theme.BORDER_LIGHTER));
        
        // 移除“更新库存”菜单项，因为库存数量是自动统计的
        // 只保留“设置预警”菜单项
        JMenuItem warningItem = new JMenuItem("设置预警");
        warningItem.setFont(Theme.FONT_BODY);
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
                    setBackground(new Color(254, 240, 240)); // 浅红色
                    setForeground(Theme.DANGER);
                } else if (status.contains("预警")) {
                    setBackground(new Color(255, 251, 230)); // 浅黄色
                    setForeground(Theme.WARNING);
                } else {
                    setBackground(new Color(240, 253, 244)); // 浅绿色
                    setForeground(Theme.SUCCESS);
                }
            } else {
                setBackground(Theme.PRIMARY);
                setForeground(Color.WHITE);
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
            // 应用筛选条件
            boolean matchesKeyword = keyword.isEmpty() || 
                                     inventory.getSpecies().contains(keyword) ||
                                     inventory.getBreed().contains(keyword);
            boolean matchesType = "全部".equals(type) || type.equals(inventory.getSpecies());
            
            String currentStatus = getStatus(inventory.getQuantity(), inventory.getWarningThreshold());
            boolean matchesStatus = "全部".equals(status) || currentStatus.contains(status);
            
            if (matchesKeyword && matchesType && matchesStatus) {
                Object[] row = {
                    inventory.getSpecies(),
                    inventory.getBreed(),
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
            // 计算状态
            String status = getStatus(inventory.getQuantity(), inventory.getWarningThreshold());
            
            Object[] row = {
                inventory.getSpecies(),
                inventory.getBreed(),
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
    
    // updateInventory方法已移除，因为库存数量是自动统计的，不可手动修改
    
    private void setWarning() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            Toast.showWarning(this, "请先选择要设置预警的库存");
            return;
        }
        
        String species = (String) tableModel.getValueAt(selectedRow, 0);
        String breed = (String) tableModel.getValueAt(selectedRow, 1);
        // 安全地获取当前预警阈值
        Object thresholdObj = tableModel.getValueAt(selectedRow, 3); // 预警阈值在第4列（索引3）
        int currentThreshold;
        if (thresholdObj instanceof Number) {
            currentThreshold = ((Number) thresholdObj).intValue();
        } else {
            currentThreshold = Integer.parseInt(thresholdObj.toString());
        }
        
        // 创建设置预警对话框
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "设置库存预警", true);
        dialog.setSize(380, 220);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = Theme.createDialogPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel currentLabel = Theme.createBodyLabel("当前预警阈值: " + currentThreshold);
        JTextField thresholdField = Theme.createTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(currentLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(Theme.createBodyLabel("新的预警阈值:"), gbc);
        gbc.gridx = 1;
        panel.add(thresholdField, gbc);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        buttonPanel.setOpaque(false);
        JButton saveButton = Theme.createPrimaryButton("保存");
        JButton cancelButton = Theme.createSecondaryButton("取消");
        
        saveButton.addActionListener(e -> {
            try {
                String thresholdStr = thresholdField.getText().trim();
                if (thresholdStr.isEmpty()) {
                    DialogUtil.showError(dialog, "请输入预警阈值");
                    return;
                }
                
                int threshold = Integer.parseInt(thresholdStr);
                inventoryService.setWarningThreshold(species, breed, threshold); // 原来是petId
                loadInventoryData();
                dialog.dispose();
                Toast.showSuccess(this, "预警阈值设置成功");
            } catch (NumberFormatException ex) {
                DialogUtil.showError(dialog, "请输入有效的数字");
            } catch (Exception ex) {
                DialogUtil.showError(dialog, "设置失败：" + ex.getMessage());
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
            String[] columnNames = {"种类", "品种", "当前库存", "预警阈值"};
            DefaultTableModel warningTableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            JTable warningTable = new JTable(warningTableModel);
            warningTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            Theme.styleTable(warningTable);
            warningTable.setDefaultRenderer(Object.class, Theme.createAlternatingRowRenderer());
            
            for (Inventory inventory : warnings) {
                Object[] row = {
                    inventory.getSpecies(),
                    inventory.getBreed(),
                    inventory.getQuantity(),
                    inventory.getWarningThreshold()
                };
                warningTableModel.addRow(row);
            }
            
            JScrollPane scrollPane = new JScrollPane(warningTable);
            scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER_LIGHTER, 1));
            scrollPane.getViewport().setBackground(Theme.BG_PRIMARY);
            panel.add(scrollPane, BorderLayout.CENTER);
            
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
    
    private void showStatistics() {
        // 创建库存统计对话框
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "库存统计", true);
        dialog.setSize(450, 320);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
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
        statsPanel.setBackground(Theme.BG_TERTIARY);
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_LIGHTER),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        addStatRow(statsPanel, "总库存数量:", String.valueOf(totalQuantity));
        addStatRow(statsPanel, "库存种类数:", String.valueOf(totalItems));
        addStatRow(statsPanel, "库存总价值:", String.format("¥%.2f", totalValue));
        addStatRow(statsPanel, "预警项目数:", String.valueOf(warningCount));
        
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
    
    /**
     * 添加统计行
     */
    private void addStatRow(JPanel panel, String label, String value) {
        JLabel lbl = Theme.createBodyLabel(label);
        lbl.setForeground(Theme.TEXT_SECONDARY);
        
        JLabel val = Theme.createSubtitleLabel(value);
        val.setForeground(Theme.PRIMARY);
        
        panel.add(lbl);
        panel.add(val);
    }
}