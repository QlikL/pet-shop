package com.petshop.gui;

import com.petshop.controller.PetController;
import com.petshop.model.Pet;
import com.petshop.service.PetService;
import com.petshop.dao.PetDao;
import com.petshop.dao.InventoryDao;
import com.petshop.util.Theme;

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
        setLayout(new BorderLayout(12, 12));
        setBackground(Theme.BG_SECONDARY);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
    }
    
    private void createComponents() {
        // 创建标题面板（包含标题和图标按钮）
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        
        JLabel titleLabel = Theme.createTitleLabel("宠物管理");
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // 创建右上角按钮面板
        JPanel iconButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        iconButtonPanel.setOpaque(false);
        
        // 添加宠物按钮
        JButton addButton = Theme.createPrimaryButton("添加宠物");
        addButton.addActionListener(e -> addPet());
        iconButtonPanel.add(addButton);
        
        // 刷新列表按钮
        JButton refreshButton = Theme.createSecondaryButton("刷新");
        refreshButton.addActionListener(e -> loadPetData());
        iconButtonPanel.add(refreshButton);
        
        titlePanel.add(iconButtonPanel, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);
        
        // 创建表格
        createPetTable();
    }
    
    private void createPetTable() {
        String[] columnNames = {"宠物ID", "品种", "种类", "年龄", "价格", "状态"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 禁止编辑表格
            }
        };
        
        petTable = new JTable(tableModel);
        petTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        petTable.getTableHeader().setReorderingAllowed(false);
        
        // 使用Theme美化表格
        Theme.styleTable(petTable);
        
        // 创建行排序器
        sorter = new TableRowSorter<>(tableModel);
        
        // 设置每列的排序规则
        sorter.setComparator(0, String.CASE_INSENSITIVE_ORDER); // 宠物ID - 字符串排序
        sorter.setComparator(1, String.CASE_INSENSITIVE_ORDER); // 品种 - 字符串排序
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
                    setBackground(row % 2 == 0 ? Theme.BG_PRIMARY : Theme.BG_TERTIARY);
                    setForeground(Theme.TEXT_PRIMARY);
                } else {
                    setBackground(Theme.TABLE_ROW_SELECTED);
                    setBackground(Theme.PRIMARY);
                    setForeground(Color.WHITE);
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(petTable);
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
        
        JMenuItem updateItem = new JMenuItem("修改");
        updateItem.setFont(Theme.FONT_BODY);
        updateItem.addActionListener(e -> updatePet());
        popupMenu.add(updateItem);
        
        JMenuItem deleteItem = new JMenuItem("删除");
        deleteItem.setFont(Theme.FONT_BODY);
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
    

    
    private void loadPetData() {
        tableModel.setRowCount(0); // 清空表格
        allPets = petService.getAllPets();
        
        // 限制显示数量，避免大数据量时界面卡顿
        int maxDisplay = 1000;
        if (allPets.size() > maxDisplay) {
            com.petshop.util.DialogUtil.showInfo(this, "数据量较大（" + allPets.size() + "条），仅显示前" + maxDisplay + "条记录");
            allPets = allPets.subList(0, maxDisplay);
        }
        
        for (Pet pet : allPets) {
            Object[] row = {
                pet.getId(),
                pet.getBreed(),
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
        dialog.setSize(440, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Theme.BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 创建输入字段
        JTextField idField = Theme.createTextField(20);
        JTextField breedField = Theme.createTextField(20);
        JComboBox<String> speciesCombo = Theme.createComboBox(new String[]{"狗", "猫", "其他"});
        JTextField ageField = Theme.createTextField(20);
        JTextField priceField = Theme.createTextField(20);
        JComboBox<String> statusCombo = Theme.createComboBox(new String[]{"可售", "已售", "预留"});
        
        // 添加标签和输入字段
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(Theme.createBodyLabel("宠物ID:"), gbc);
        gbc.gridx = 1;
        panel.add(idField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(Theme.createBodyLabel("品种:"), gbc);
        gbc.gridx = 1;
        panel.add(breedField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(Theme.createBodyLabel("种类:"), gbc);
        gbc.gridx = 1;
        panel.add(speciesCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(Theme.createBodyLabel("年龄:"), gbc);
        gbc.gridx = 1;
        panel.add(ageField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(Theme.createBodyLabel("价格:"), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(Theme.createBodyLabel("状态:"), gbc);
        gbc.gridx = 1;
        panel.add(statusCombo, gbc);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        buttonPanel.setOpaque(false);
        JButton saveButton = Theme.createPrimaryButton("保存");
        JButton cancelButton = Theme.createSecondaryButton("取消");
        
        saveButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String breed = breedField.getText().trim();
                String species = (String) speciesCombo.getSelectedItem();
                String ageStr = ageField.getText().trim();
                String priceStr = priceField.getText().trim();
                String status = (String) statusCombo.getSelectedItem();
                
                if (id.isEmpty() || breed.isEmpty() || ageStr.isEmpty() || priceStr.isEmpty()) {
                    com.petshop.util.DialogUtil.showError(dialog, "请填写所有必填字段");
                    return;
                }
                
                double age = Double.parseDouble(ageStr);
                double price = Double.parseDouble(priceStr);
                
                petService.addPet(id, breed, species, age, price, status);
                loadPetData();
                dialog.dispose();
                com.petshop.util.DialogUtil.showSuccess(this, "添加成功");
            } catch (NumberFormatException ex) {
                com.petshop.util.DialogUtil.showError(dialog, "年龄和价格必须是数字");
            } catch (Exception ex) {
                com.petshop.util.DialogUtil.showError(dialog, "添加失败：" + ex.getMessage());
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
            com.petshop.util.DialogUtil.showWarning(this, "请先选择要删除的宠物");
            return;
        }
        
        String petId = (String) tableModel.getValueAt(selectedRow, 0);
        String petBreed = (String) tableModel.getValueAt(selectedRow, 1);
        boolean confirm = com.petshop.util.DialogUtil.showConfirm(this, "确认删除", "确定要删除宠物 " + petBreed + " (ID: " + petId + ") 吗？\n删除后无法恢复！");
        
        if (confirm) {
            try {
                petService.deletePet(petId);
                loadPetData();
                com.petshop.util.DialogUtil.showSuccess(this, "宠物 " + petBreed + " 删除成功");
            } catch (Exception e) {
                String errorMsg = "删除失败：";
                if (e.getMessage().contains("not found")) {
                    errorMsg += "宠物不存在";
                } else if (e.getMessage().contains("foreign key")) {
                    errorMsg += "该宠物有关联的库存或销售记录，无法删除";
                } else {
                    errorMsg += e.getMessage();
                }
                com.petshop.util.DialogUtil.showError(this, errorMsg);
            }
        }
    }
    
    private void updatePet() {
        int selectedRow = petTable.getSelectedRow();
        if (selectedRow == -1) {
            com.petshop.util.DialogUtil.showWarning(this, "请先选择要修改的宠物");
            return;
        }
        
        String petId = (String) tableModel.getValueAt(selectedRow, 0);
        String currentBreed = (String) tableModel.getValueAt(selectedRow, 1); // 原来是currentName
        String currentSpecies = (String) tableModel.getValueAt(selectedRow, 2);
        double currentAge = (double) tableModel.getValueAt(selectedRow, 3);
        double currentPrice = (double) tableModel.getValueAt(selectedRow, 4);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 5);
        
        // 创建修改宠物对话框
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "修改宠物", true);
        dialog.setSize(440, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = Theme.createDialogPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 创建输入字段
        JTextField breedField = Theme.createTextField(20);
        breedField.setText(currentBreed);
        JComboBox<String> speciesCombo = Theme.createComboBox(new String[]{"狗", "猫", "其他"});
        speciesCombo.setSelectedItem(currentSpecies);
        JTextField ageField = Theme.createTextField(20);
        ageField.setText(String.valueOf(currentAge));
        JTextField priceField = Theme.createTextField(20);
        priceField.setText(String.valueOf(currentPrice));
        JComboBox<String> statusCombo = Theme.createComboBox(new String[]{"可售", "已售", "预留"});
        statusCombo.setSelectedItem(currentStatus);
        
        // 添加标签和输入字段
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(Theme.createBodyLabel("品种:"), gbc);
        gbc.gridx = 1;
        panel.add(breedField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(Theme.createBodyLabel("种类:"), gbc);
        gbc.gridx = 1;
        panel.add(speciesCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(Theme.createBodyLabel("年龄:"), gbc);
        gbc.gridx = 1;
        panel.add(ageField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(Theme.createBodyLabel("价格:"), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(Theme.createBodyLabel("状态:"), gbc);
        gbc.gridx = 1;
        panel.add(statusCombo, gbc);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        buttonPanel.setOpaque(false);
        JButton saveButton = Theme.createPrimaryButton("保存");
        JButton cancelButton = Theme.createSecondaryButton("取消");
        
        saveButton.addActionListener(e -> {
            try {
                String breed = breedField.getText().trim(); // 原来是name
                String species = (String) speciesCombo.getSelectedItem();
                String ageStr = ageField.getText().trim();
                String priceStr = priceField.getText().trim();
                String status = (String) statusCombo.getSelectedItem();
                
                if (breed.isEmpty() || ageStr.isEmpty() || priceStr.isEmpty()) {
                    com.petshop.util.DialogUtil.showError(dialog, "请填写所有必填字段");
                    return;
                }
                
                double age = Double.parseDouble(ageStr);
                double price = Double.parseDouble(priceStr);
                
                petService.updatePet(petId, breed, species, age, price, status);
                loadPetData();
                dialog.dispose();
                com.petshop.util.DialogUtil.showSuccess(this, "修改成功");
            } catch (NumberFormatException ex) {
                com.petshop.util.DialogUtil.showError(dialog, "年龄和价格必须是数字");
            } catch (Exception ex) {
                com.petshop.util.DialogUtil.showError(dialog, "修改失败：" + ex.getMessage());
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
        com.petshop.util.DialogUtil.showInfo(this, "查询宠物功能待实现");
    }
}