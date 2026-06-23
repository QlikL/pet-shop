package com.petshop.gui;

import javax.swing.*;
import java.awt.*;
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
    
    public StatisticsPanel() {
        initializePanel();
        createComponents();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    private void createComponents() {
        // 创建标题
        JLabel titleLabel = new JLabel("数据统计", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);
        
        // 创建选项卡面板
        tabbedPane = new JTabbedPane();
        
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
        // 创建数据集
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // 这里应该从服务获取实际数据，现在使用示例数据
        dataset.addValue(100, "库存数量", "狗");
        dataset.addValue(80, "库存数量", "猫");
        dataset.addValue(50, "库存数量", "鸟");
        dataset.addValue(30, "库存数量", "鱼");
        
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
        // 创建数据集
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // 这里应该从服务获取实际数据，现在使用示例数据
        dataset.addValue(15000, "销售额", "1月");
        dataset.addValue(18000, "销售额", "2月");
        dataset.addValue(12000, "销售额", "3月");
        dataset.addValue(20000, "销售额", "4月");
        dataset.addValue(22000, "销售额", "5月");
        dataset.addValue(19000, "销售额", "6月");
        
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
        
        return chart;
    }
    
    private void refreshInventoryStats() {
        // TODO: 实现库存统计刷新
        JOptionPane.showMessageDialog(this, "库存统计刷新功能待实现", "提示", JOptionPane.INFORMATION_MESSAGE);
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
        // TODO: 实现销售统计刷新
        JOptionPane.showMessageDialog(this, "销售统计刷新功能待实现", "提示", JOptionPane.INFORMATION_MESSAGE);
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