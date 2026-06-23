package com.petshop.gui;

import com.petshop.dao.PetDao;
import com.petshop.dao.InventoryDao;
import com.petshop.dao.SalesRecordDao;
import com.petshop.model.Pet;
import com.petshop.model.Inventory;
import com.petshop.model.SalesRecord;
import com.petshop.service.InventoryService;
import com.petshop.service.SalesService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 * 数据统计界面面板
 */
public class StatisticsPanel extends JPanel {
    private JTabbedPane tabbedPane;
    private PetDao petDao;
    private InventoryDao inventoryDao;
    private SalesRecordDao salesRecordDao;
    private InventoryService inventoryService;
    private SalesService salesService;
    
    public StatisticsPanel() {
        // 初始化DAO和服务
        this.petDao = new PetDao();
        this.inventoryDao = new InventoryDao();
        this.salesRecordDao = new SalesRecordDao();
        this.inventoryService = new InventoryService(petDao, inventoryDao);
        this.salesService = new SalesService(petDao, inventoryDao, salesRecordDao);
        
        initializePanel();
        createComponents();
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
        JLabel titleLabel = new JLabel("数据统计", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 51, 51));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);
        
        // 创建选项卡面板
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        
        // 添加库存统计选项卡
        JPanel inventoryStatsPanel = createInventoryStatsPanel();
        tabbedPane.addTab("库存统计", inventoryStatsPanel);
        
        // 添加销售统计选项卡
        JPanel salesStatsPanel = createSalesStatsPanel();
        tabbedPane.addTab("销售统计", salesStatsPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createInventoryStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 创建库存统计图表
        JFreeChart inventoryChart = createInventoryChart();
        ChartPanel chartPanel = new ChartPanel(inventoryChart);
        panel.add(chartPanel, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton refreshButton = new JButton("刷新统计");
        JButton exportImageButton = new JButton("导出图片");
        JButton exportPdfButton = new JButton("导出PDF");
        
        refreshButton.addActionListener(e -> refreshInventoryStats());
        exportImageButton.addActionListener(e -> exportInventoryImage());
        exportPdfButton.addActionListener(e -> exportInventoryPdf());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportImageButton);
        buttonPanel.add(exportPdfButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JFreeChart createInventoryChart() {
        // 创建数据集 - 按种类统计库存
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // 从实际数据获取库存统计
        List<Inventory> inventories = inventoryService.getAllInventory();
        Map<String, Integer> speciesCount = new HashMap<>();
        speciesCount.put("狗", 0);
        speciesCount.put("猫", 0);
        speciesCount.put("其他", 0);
        
        for (Inventory inv : inventories) {
            // 直接使用Inventory中的species字段
            String species = inv.getSpecies();
            // 将品种归类为3种类型
            if ("狗".equals(species)) {
                speciesCount.put("狗", speciesCount.get("狗") + inv.getQuantity());
            } else if ("猫".equals(species)) {
                speciesCount.put("猫", speciesCount.get("猫") + inv.getQuantity());
            } else {
                speciesCount.put("其他", speciesCount.get("其他") + inv.getQuantity());
            }
        }
        
        dataset.addValue(speciesCount.get("狗"), "库存数量", "狗");
        dataset.addValue(speciesCount.get("猫"), "库存数量", "猫");
        dataset.addValue(speciesCount.get("其他"), "库存数量", "其他");
        
        // 创建柱状图
        JFreeChart chart = ChartFactory.createBarChart(
            "库存统计",           // 图表标题
            "宠物种类",           // X轴标签
            "库存数量",           // Y轴标签
            dataset,              // 数据集
            PlotOrientation.VERTICAL, // 图表方向
            true,                 // 显示图例
            true,                 // 显示工具提示
            false                 // 不生成URL
        );
        
        // 设置中文字体
        setChineseFont(chart);
        
        return chart;
    }
    
    private JPanel createSalesStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 创建销售统计图表
        JFreeChart salesChart = createSalesChart();
        ChartPanel chartPanel = new ChartPanel(salesChart);
        panel.add(chartPanel, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton refreshButton = new JButton("刷新统计");
        JButton exportImageButton = new JButton("导出图片");
        JButton exportPdfButton = new JButton("导出PDF");
        
        refreshButton.addActionListener(e -> refreshSalesStats());
        exportImageButton.addActionListener(e -> exportSalesImage());
        exportPdfButton.addActionListener(e -> exportSalesPdf());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportImageButton);
        buttonPanel.add(exportPdfButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JFreeChart createSalesChart() {
        // 创建数据集 - 按月份统计销售额
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // 从实际数据获取销售统计
        List<SalesRecord> records = salesService.getAllSales();
        Map<String, Double> monthlySales = new HashMap<>();
        
        // 初始化最近6个月的数据
        java.time.LocalDate now = java.time.LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            java.time.LocalDate month = now.minusMonths(i);
            String monthKey = month.getMonthValue() + "月";
            monthlySales.put(monthKey, 0.0);
        }
        
        // 统计每个月的销售额
        for (SalesRecord record : records) {
            java.time.LocalDateTime saleTime = record.getSaleTime();
            int month = saleTime.getMonthValue();
            String monthKey = month + "月";
            if (monthlySales.containsKey(monthKey)) {
                monthlySales.put(monthKey, monthlySales.get(monthKey) + record.getTotalPrice());
            }
        }
        
        // 添加数据到数据集
        for (Map.Entry<String, Double> entry : monthlySales.entrySet()) {
            dataset.addValue(entry.getValue(), "销售额", entry.getKey());
        }
        
        // 创建折线图
        JFreeChart chart = ChartFactory.createLineChart(
            "销售趋势",           // 图表标题
            "月份",               // X轴标签
            "销售额",             // Y轴标签
            dataset,              // 数据集
            PlotOrientation.VERTICAL, // 图表方向
            true,                 // 显示图例
            true,                 // 显示工具提示
            false                 // 不生成URL
        );
        
        // 设置中文字体
        setChineseFont(chart);
        
        return chart;
    }
    
    /**
     * 设置图表的中文字体
     */
    private void setChineseFont(JFreeChart chart) {
        Font font = new Font("微软雅黑", Font.PLAIN, 12);
        
        // 设置标题字体
        if (chart.getTitle() != null) {
            chart.getTitle().setFont(new Font("微软雅黑", Font.BOLD, 16));
        }
        
        // 设置图例字体
        if (chart.getLegend() != null) {
            chart.getLegend().setItemFont(font);
        }
        
        // 设置XY轴字体
        if (chart.getPlot() instanceof org.jfree.chart.plot.CategoryPlot) {
            org.jfree.chart.plot.CategoryPlot plot = (org.jfree.chart.plot.CategoryPlot) chart.getPlot();
            if (plot.getDomainAxis() != null) {
                plot.getDomainAxis().setLabelFont(font);
                plot.getDomainAxis().setTickLabelFont(font);
            }
            if (plot.getRangeAxis() != null) {
                plot.getRangeAxis().setLabelFont(font);
                plot.getRangeAxis().setTickLabelFont(font);
            }
        }
    }
    
    private void refreshInventoryStats() {
        // 刷新库存统计图表
        try {
            JFreeChart inventoryChart = createInventoryChart();
            ChartPanel chartPanel = (ChartPanel) ((JPanel) ((JScrollPane) ((JPanel) getParent().getComponent(1)).getComponent(0)).getViewport().getView()).getComponent(0);
            chartPanel.setChart(inventoryChart);
            JOptionPane.showMessageDialog(this, "库存统计已刷新", "提示", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "刷新失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportInventoryImage() {
        // 实现库存统计图片导出
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存库存统计图片");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG图片", "png"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".png")) {
                    file = new java.io.File(file.getAbsolutePath() + ".png");
                }
                
                // 获取库存统计图表
                JFreeChart inventoryChart = createInventoryChart();
                
                // 保存为PNG图片
                org.jfree.chart.ChartUtils.saveChartAsPNG(file, inventoryChart, 800, 600);
                
                JOptionPane.showMessageDialog(this, "图片导出成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "图片导出失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportInventoryPdf() {
        // 实现库存统计PDF导出
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存库存统计PDF");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF文件", "pdf"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".pdf")) {
                    file = new java.io.File(file.getAbsolutePath() + ".pdf");
                }
                
                // 获取库存统计图表
                JFreeChart inventoryChart = createInventoryChart();
                
                // 创建PDF文档
                com.lowagie.text.Document document = new com.lowagie.text.Document();
                com.lowagie.text.pdf.PdfWriter writer = com.lowagie.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(file));
                
                document.open();
                
                // 将图表添加到PDF
                java.awt.image.BufferedImage image = inventoryChart.createBufferedImage(800, 600);
                com.lowagie.text.Image pdfImage = com.lowagie.text.Image.getInstance(writer, image, 1.0f);
                document.add(pdfImage);
                
                document.close();
                
                JOptionPane.showMessageDialog(this, "PDF导出成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "PDF导出失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshSalesStats() {
        // 刷新销售统计图表
        try {
            JFreeChart salesChart = createSalesChart();
            ChartPanel chartPanel = (ChartPanel) ((JPanel) ((JScrollPane) ((JPanel) getParent().getComponent(1)).getComponent(0)).getViewport().getView()).getComponent(0);
            chartPanel.setChart(salesChart);
            JOptionPane.showMessageDialog(this, "销售统计已刷新", "提示", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "刷新失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportSalesImage() {
        // 实现销售统计图片导出
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存销售统计图片");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG图片", "png"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".png")) {
                    file = new java.io.File(file.getAbsolutePath() + ".png");
                }
                
                // 获取销售统计图表
                JFreeChart salesChart = createSalesChart();
                
                // 保存为PNG图片
                org.jfree.chart.ChartUtils.saveChartAsPNG(file, salesChart, 800, 600);
                
                JOptionPane.showMessageDialog(this, "图片导出成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "图片导出失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportSalesPdf() {
        // 实现销售统计PDF导出
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存销售统计PDF");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF文件", "pdf"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".pdf")) {
                    file = new java.io.File(file.getAbsolutePath() + ".pdf");
                }
                
                // 获取销售统计图表
                JFreeChart salesChart = createSalesChart();
                
                // 创建PDF文档
                com.lowagie.text.Document document = new com.lowagie.text.Document();
                com.lowagie.text.pdf.PdfWriter writer = com.lowagie.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(file));
                
                document.open();
                
                // 将图表添加到PDF
                java.awt.image.BufferedImage image = salesChart.createBufferedImage(800, 600);
                com.lowagie.text.Image pdfImage = com.lowagie.text.Image.getInstance(writer, image, 1.0f);
                document.add(pdfImage);
                
                document.close();
                
                JOptionPane.showMessageDialog(this, "PDF导出成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "PDF导出失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}