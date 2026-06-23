package com.petshop.gui;

import com.petshop.controller.PetController;
import com.petshop.model.Pet;
import com.petshop.service.PetService;
import com.petshop.dao.PetDao;
import com.petshop.dao.InventoryDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * 宠物管理界面面板
 */
public class PetManagementPanel extends JPanel {
    private PetService petService;
    private JTable petTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private List<Pet> allPets; // 保存所有宠物数据
    
    public PetManagementPanel() {
        // 创建DAO实例
        PetDao petDao = new PetDao();
        InventoryDao inventoryDao = new InventoryDao();
        this.petService = new PetService(petDao, inventoryDao);
        
        initializePanel();
        createComponents();
        loadPetData();
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
        
        JLabel titleLabel = new JLabel("宠物管理", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 51, 51));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 创建右上角图标按钮面板
        JPanel iconButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        iconButtonPanel.setOpaque(false);
        
        // 添加宠物按钮（+号）
        JButton addButton = createIconButton("", "添加宠物", new Color(76, 175, 80));
        addButton.addActionListener(e -> addPet());
        iconButtonPanel.add(addButton);
        
        // 刷新列表按钮（刷新符号）
        JButton refreshButton = createIconButton("🔄", "刷新列表", new Color(96, 125, 139));
        refreshButton.addActionListener(e -> loadPetData());
        iconButtonPanel.add(refreshButton);
        
        titlePanel.add(iconButtonPanel, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);
        
        // 创建表格
        createPetTable();
        
        // 移除底部按钮面板，不再需要
    }
    
    private void createPetTable() {
        String[] columnNames = {"宠物ID", "名称", "种类", "年龄", "价格", "状态"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 禁止编辑表格
            }
        };
        
        petTable = new JTable(tableModel);
        petTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        petTable.getTableHeader().setReorderingAllowed(false);
        petTable.setRowHeight(30);
        petTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        petTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        petTable.getTableHeader().setBackground(new Color(240, 242, 245)); // 浅灰蓝背景
        petTable.getTableHeader().setForeground(new Color(51, 51, 51)); // 深黑色文字
        petTable.setGridColor(new Color(230, 230, 230));
        petTable.setShowGrid(true);
        petTable.setIntercellSpacing(new Dimension(1, 1));
        
        // 创建行排序器
        sorter = new TableRowSorter<>(tableModel);
        
        // 设置每列的排序规则
        sorter.setComparator(0, String.CASE_INSENSITIVE_ORDER); // 宠物ID - 字符串排序
        sorter.setComparator(1, String.CASE_INSENSITIVE_ORDER); // 名称 - 字符串排序
        sorter.setComparator(2, String.CASE_INSENSITIVE_ORDER); // 种类 - 字符串排序
        sorter.setComparator(3, (Comparator<Object>) (o1, o2) -> Integer.compare((Integer)o1, (Integer)o2)); // 年龄 - 整数排序
        sorter.setComparator(4, (Comparator<Object>) (o1, o2) -> Double.compare((Double)o1, (Double)o2)); // 价格 - 双精度排序
        sorter.setComparator(5, String.CASE_INSENSITIVE_ORDER); // 状态 - 字符串排序
        
        petTable.setRowSorter(sorter);
        
        // 交替行颜色
        petTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(petTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        add(scrollPane, BorderLayout.CENTER);
        
        // 添加右键菜单
        setupContextMenu();
    }
    
    /**
     * 设置右键菜单
     */
    private void setupContextMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        
        JMenuItem updateItem = new JMenuItem("修改");
        updateItem.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        updateItem.addActionListener(e -> updatePet());
        popupMenu.add(updateItem);
        
        JMenuItem deleteItem = new JMenuItem("删除");
        deleteItem.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        deleteItem.addActionListener(e -> deletePet());
        popupMenu.add(deleteItem);
        
        petTable.addMouseListener(new java.awt.event.MouseAdapter() {
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
                int row = petTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < petTable.getRowCount()) {
                    petTable.setRowSelectionInterval(row, row);
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
    

    
    private void loadPetData() {
        tableModel.setRowCount(0); // 清空表格
        allPets = petService.getAllPets();
        
        // 限制显示数量，避免大数据量时界面卡顿
        int maxDisplay = 1000;
        if (allPets.size() > maxDisplay) {
            JOptionPane.showMessageDialog(this, 
                "数据量较大（" + allPets.size() + "条），仅显示前" + maxDisplay + "条记录",
                "提示", JOptionPane.INFORMATION_MESSAGE);
            allPets = allPets.subList(0, maxDisplay);
        }
        
        for (Pet pet : allPets) {
            Object[] row = {
                pet.getId(),
                pet.getName(),
                pet.getSpecies(),
                pet.getAge(),
                pet.getPrice(),
                pet.getStatus()
            };
            tableModel.addRow(row);
        }
    }
    
    private void addPet() {
        // 创建添加宠物对话框
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "添加宠物", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 创建输入字段
        JTextField idField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JComboBox<String> speciesCombo = new JComboBox<>(new String[]{"狗", "猫", "其他"});
        JTextField ageField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"可售", "已售", "预留"});
        
        // 添加标签和输入字段
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("宠物ID:"), gbc);
        gbc.gridx = 1;
        panel.add(idField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("名称:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("种类:"), gbc);
        gbc.gridx = 1;
        panel.add(speciesCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("年龄:"), gbc);
        gbc.gridx = 1;
        panel.add(ageField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("价格:"), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("状态:"), gbc);
        gbc.gridx = 1;
        panel.add(statusCombo, gbc);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");
        
        saveButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();
                String species = (String) speciesCombo.getSelectedItem();
                String ageStr = ageField.getText().trim();
                String priceStr = priceField.getText().trim();
                String status = (String) statusCombo.getSelectedItem();
                
                if (id.isEmpty() || name.isEmpty() || ageStr.isEmpty() || priceStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请填写所有必填字段", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int age = Integer.parseInt(ageStr);
                double price = Double.parseDouble(priceStr);
                
                petService.addPet(id, name, species, age, price, status);
                loadPetData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "添加成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "年龄和价格必须是数字", "错误", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "添加失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void deletePet() {
        int selectedRow = petTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的宠物", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String petId = (String) tableModel.getValueAt(selectedRow, 0);
        String petName = (String) tableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "确定要删除宠物 " + petName + " (ID: " + petId + ") 吗？\n删除后无法恢复！", 
            "确认删除", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                petService.deletePet(petId);
                loadPetData();
                JOptionPane.showMessageDialog(this, "宠物 " + petName + " 删除成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                String errorMsg = "删除失败：";
                if (e.getMessage().contains("not found")) {
                    errorMsg += "宠物不存在";
                } else if (e.getMessage().contains("foreign key")) {
                    errorMsg += "该宠物有关联的库存或销售记录，无法删除";
                } else {
                    errorMsg += e.getMessage();
                }
                JOptionPane.showMessageDialog(this, errorMsg, "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updatePet() {
        int selectedRow = petTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要修改的宠物", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String petId = (String) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentSpecies = (String) tableModel.getValueAt(selectedRow, 2);
        int currentAge = (int) tableModel.getValueAt(selectedRow, 3);
        double currentPrice = (double) tableModel.getValueAt(selectedRow, 4);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 5);
        
        // 创建修改宠物对话框
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "修改宠物", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 创建输入字段
        JTextField nameField = new JTextField(currentName, 20);
        JComboBox<String> speciesCombo = new JComboBox<>(new String[]{"狗", "猫", "其他"});
        speciesCombo.setSelectedItem(currentSpecies);
        JTextField ageField = new JTextField(String.valueOf(currentAge), 20);
        JTextField priceField = new JTextField(String.valueOf(currentPrice), 20);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"可售", "已售", "预留"});
        statusCombo.setSelectedItem(currentStatus);
        
        // 添加标签和输入字段
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("名称:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("种类:"), gbc);
        gbc.gridx = 1;
        panel.add(speciesCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("年龄:"), gbc);
        gbc.gridx = 1;
        panel.add(ageField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("价格:"), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("状态:"), gbc);
        gbc.gridx = 1;
        panel.add(statusCombo, gbc);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");
        
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String species = (String) speciesCombo.getSelectedItem();
                String ageStr = ageField.getText().trim();
                String priceStr = priceField.getText().trim();
                String status = (String) statusCombo.getSelectedItem();
                
                if (name.isEmpty() || ageStr.isEmpty() || priceStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请填写所有必填字段", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int age = Integer.parseInt(ageStr);
                double price = Double.parseDouble(priceStr);
                
                petService.updatePet(petId, name, species, age, price, status);
                loadPetData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "修改成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "年龄和价格必须是数字", "错误", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "修改失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void queryPet() {
        // TODO: 实现查询宠物对话框
        JOptionPane.showMessageDialog(this, "查询宠物功能待实现", "提示", JOptionPane.INFORMATION_MESSAGE);
    }
}