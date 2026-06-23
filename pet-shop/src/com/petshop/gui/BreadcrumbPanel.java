package com.petshop.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 面包屑导航面板
 */
public class BreadcrumbPanel extends JPanel {
    private List<String> breadcrumbs = new ArrayList<>();
    
    public BreadcrumbPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
    }
    
    /**
     * 设置面包屑路径
     * @param items 面包屑项列表
     */
    public void setBreadcrumbs(String... items) {
        breadcrumbs.clear();
        for (String item : items) {
            breadcrumbs.add(item);
        }
        renderBreadcrumbs();
    }
    
    /**
     * 渲染面包屑
     */
    private void renderBreadcrumbs() {
        removeAll();
        
        for (int i = 0; i < breadcrumbs.size(); i++) {
            // 添加链接文本
            JLabel label = new JLabel(breadcrumbs.get(i));
            label.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            if (i == breadcrumbs.size() - 1) {
                // 最后一项（当前页）使用深色
                label.setForeground(new Color(51, 51, 51));
                label.setFont(new Font("微软雅黑", Font.BOLD, 14));
            } else {
                // 前面的项使用蓝色链接样式
                label.setForeground(new Color(51, 153, 255));
                
                // 添加点击事件
                int index = i;
                label.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        // TODO: 实现返回到指定层级的功能
                        Toast.showInfo(BreadcrumbPanel.this, "返回到: " + breadcrumbs.get(index));
                    }
                    
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        label.setText("<html><u>" + breadcrumbs.get(index) + "</u></html>");
                    }
                    
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        label.setText(breadcrumbs.get(index));
                    }
                });
            }
            
            add(label);
            
            // 如果不是最后一项，添加分隔符
            if (i < breadcrumbs.size() - 1) {
                JLabel separator = new JLabel(">");
                separator.setForeground(new Color(153, 153, 153));
                separator.setFont(new Font("微软雅黑", Font.PLAIN, 14));
                add(separator);
            }
        }
        
        revalidate();
        repaint();
    }
}
