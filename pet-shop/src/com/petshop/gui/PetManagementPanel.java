package com.petshop.gui;

import com.petshop.controller.PetController;
import com.petshop.model.Pet;
import com.petshop.service.PetService;
import com.petshop.dao.PetDao;
import com.petshop.dao.InventoryDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 宠物管理界面面板
 */
public class PetManagementPanel extends JPanel {
    private PetService petService;
    private JTable petTable;
    private DefaultTableModel tableModel;
    
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
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    private void createComponents() {
        // 创建标题
        JLabel titleLabel = new JLabel("宠物管理", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);
        
        // 创建表格
        createPetTable();
        
        // 创建按钮面板
        createButtonPanel();
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
        
        JScrollPane scrollPane = new JScrollPane(petTable);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton addButton = new JButton("添加宠物");
        JButton deleteButton = new JButton("删除宠物");
        JButton updateButton = new JButton("修改宠物");
        JButton queryButton = new JButton("查询宠物");
        JButton filterSpeciesButton = new JButton("按种类筛选");
        JButton filterStatusButton = new JButton("按状态筛选");
        JButton refreshButton = new JButton("刷新列表");
        
        addButton.addActionListener(e -> addPet());
        deleteButton.addActionListener(e -> deletePet());
        updateButton.addActionListener(e -> updatePet());
        queryButton.addActionListener(e -> queryPet());
        filterSpeciesButton.addActionListener(e -> filterBySpecies());
        filterStatusButton.addActionListener(e -> filterByStatus());
        refreshButton.addActionListener(e -> loadPetData());
        
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(queryButton);
        buttonPanel.add(filterSpeciesButton);
        buttonPanel.add(filterStatusButton);
        buttonPanel.add(refreshButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadPetData() {
        tableModel.setRowCount(0); // 清空表格
        List<Pet> pets = petService.getAllPets();
        
        // 限制显示数量，避免大数据量时界面卡顿
        int maxDisplay = 1000;
        if (pets.size() > maxDisplay) {
            JOptionPane.showMessageDialog(this, 
                "数据量较大（" + pets.size() + "条），仅显示前" + maxDisplay + "条记录",
                "提示", JOptionPane.INFORMATION_MESSAGE);
            pets = pets.subList(0, maxDisplay);
        }
        
        for (Pet pet : pets) {
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
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 创建输入字段
        JTextField nameField = new JTextField(20);
        JTextField speciesField = new JTextField(20);
        JTextField ageField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JTextField statusField = new JTextField(20);
        
        // 添加标签和输入字段
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("名称:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("种类:"), gbc);
        gbc.gridx = 1;
        panel.add(speciesField, gbc);
        
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
        panel.add(statusField, gbc);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");
        
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String species = speciesField.getText().trim();
                String ageStr = ageField.getText().trim();
                String priceStr = priceField.getText().trim();
                String status = statusField.getText().trim();
                
                if (name.isEmpty() || species.isEmpty() || ageStr.isEmpty() || priceStr.isEmpty() || status.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请填写所有字段", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int age = Integer.parseInt(ageStr);
                double price = Double.parseDouble(priceStr);
                
                petService.addPet("", name, species, age, price, status);
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
        
        gbc.gridx = 0; gbc.gridy = 5;
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
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 创建输入字段
        JTextField nameField = new JTextField(currentName, 20);
        JTextField speciesField = new JTextField(currentSpecies, 20);
        JTextField ageField = new JTextField(String.valueOf(currentAge), 20);
        JTextField priceField = new JTextField(String.valueOf(currentPrice), 20);
        JTextField statusField = new JTextField(currentStatus, 20);
        
        // 添加标签和输入字段
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("名称:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("种类:"), gbc);
        gbc.gridx = 1;
        panel.add(speciesField, gbc);
        
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
        panel.add(statusField, gbc);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");
        
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String species = speciesField.getText().trim();
                String ageStr = ageField.getText().trim();
                String priceStr = priceField.getText().trim();
                String status = statusField.getText().trim();
                
                if (name.isEmpty() || species.isEmpty() || ageStr.isEmpty() || priceStr.isEmpty() || status.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请填写所有字段", "错误", JOptionPane.ERROR_MESSAGE);
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
    
    private void filterBySpecies() {
        String species = JOptionPane.showInputDialog(this, "请输入要筛选的种类:");
        if (species != null && !species.trim().isEmpty()) {
            List<Pet> filteredPets = petService.getPetsBySpecies(species.trim());
            updateTableWithFilteredData(filteredPets);
        }
    }
    
    private void filterByStatus() {
        String status = JOptionPane.showInputDialog(this, "请输入要筛选的状态:");
        if (status != null && !status.trim().isEmpty()) {
            List<Pet> filteredPets = petService.getPetsByStatus(status.trim());
            updateTableWithFilteredData(filteredPets);
        }
    }
    
    private void updateTableWithFilteredData(List<Pet> pets) {
        tableModel.setRowCount(0);
        for (Pet pet : pets) {
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
}