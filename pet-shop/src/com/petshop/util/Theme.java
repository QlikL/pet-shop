package com.petshop.util;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * 主题工具类 - 统一管理UI样式
 */
public class Theme {
    // 主色调 - 现代蓝色系
    public static final Color PRIMARY = new Color(64, 158, 255);
    public static final Color PRIMARY_LIGHT = new Color(121, 187, 255);
    public static final Color PRIMARY_DARK = new Color(51, 132, 219);
    
    // 辅助色
    public static final Color SUCCESS = new Color(103, 194, 58);
    public static final Color WARNING = new Color(230, 162, 60);
    public static final Color DANGER = new Color(245, 108, 108);
    public static final Color INFO = new Color(144, 147, 153);
    
    // 背景色
    public static final Color BG_PRIMARY = new Color(255, 255, 255);
    public static final Color BG_SECONDARY = new Color(250, 252, 255);
    public static final Color BG_TERTIARY = new Color(245, 247, 252);
    
    // 文字颜色
    public static final Color TEXT_PRIMARY = new Color(48, 49, 51);
    public static final Color TEXT_REGULAR = new Color(96, 98, 102);
    public static final Color TEXT_SECONDARY = new Color(144, 147, 153);
    
    // 边框颜色
    public static final Color BORDER_LIGHT = new Color(220, 223, 230);
    public static final Color BORDER_LIGHTER = new Color(235, 238, 245);
    
    // 表格颜色
    public static final Color TABLE_HEADER_BG = new Color(250, 251, 253);
    public static final Color TABLE_HEADER_FG = new Color(96, 98, 102);
    public static final Color TABLE_ROW_HOVER = new Color(245, 249, 255);
    public static final Color TABLE_ROW_SELECTED = new Color(218, 235, 255);
    public static final Color TABLE_GRID = new Color(235, 238, 245);
    
    // 侧边栏颜色
    public static final Color SIDEBAR_BG = new Color(54, 63, 78);
    public static final Color SIDEBAR_HOVER = new Color(67, 78, 96);
    public static final Color SIDEBAR_ACTIVE = PRIMARY;
    
    // 字体
    public static final Font FONT_TITLE_LARGE = new Font("微软雅黑", Font.BOLD, 22);
    public static final Font FONT_TITLE = new Font("微软雅黑", Font.BOLD, 18);
    public static final Font FONT_SUBTITLE = new Font("微软雅黑", Font.BOLD, 15);
    public static final Font FONT_BODY = new Font("微软雅黑", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("微软雅黑", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("微软雅黑", Font.PLAIN, 14);
    
    /**
     * 创建现代风格按钮
     */
    public static JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 36));
        
        // 添加圆角效果
        button.setBorder(BorderFactory.createCompoundBorder(
            createRoundedBorder(bgColor, 8),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));
        
        // 添加鼠标悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    /**
     * 创建主要按钮
     */
    public static JButton createPrimaryButton(String text) {
        return createButton(text, PRIMARY);
    }
    
    /**
     * 创建成功按钮
     */
    public static JButton createSuccessButton(String text) {
        return createButton(text, SUCCESS);
    }
    
    /**
     * 创建危险按钮
     */
    public static JButton createDangerButton(String text) {
        return createButton(text, DANGER);
    }
    
    /**
     * 创建次要按钮（边框样式）
     */
    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setBackground(BG_PRIMARY);
        button.setForeground(TEXT_PRIMARY);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 36));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));
        
        // 添加鼠标悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BG_TERTIARY);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BG_PRIMARY);
            }
        });
        
        return button;
    }
    
    /**
     * 创建圆角边框
     */
    public static Border createRoundedBorder(Color color, int radius) {
        return new Border() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
                g2d.dispose();
            }
            
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
            }
            
            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        };
    }
    
    /**
     * 创建现代化的输入框
     */
    public static JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(BG_PRIMARY);
        
        // 添加焦点效果
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        return field;
    }
    
    /**
     * 创建现代化的密码输入框
     */
    public static JPasswordField createPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(BG_PRIMARY);
        
        // 添加焦点效果
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        return field;
    }
    
    /**
     * 创建现代化的下拉框
     */
    public static JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(FONT_BODY);
        comboBox.setBackground(BG_PRIMARY);
        comboBox.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1));
        return comboBox;
    }
    
    /**
     * 创建表格样式
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(36);
        table.setGridColor(TABLE_GRID);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionBackground(TABLE_ROW_SELECTED);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setBackground(BG_PRIMARY);
        
        // 设置表头样式
        table.getTableHeader().setFont(FONT_SUBTITLE);
        table.getTableHeader().setBackground(TABLE_HEADER_BG);
        table.getTableHeader().setForeground(TABLE_HEADER_FG);
        table.getTableHeader().setPreferredSize(new Dimension(0, 42));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY));
    }
    
    /**
     * 创建标签样式
     */
    public static JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }
    
    /**
     * 创建标题标签
     */
    public static JLabel createTitleLabel(String text) {
        return createLabel(text, FONT_TITLE, TEXT_PRIMARY);
    }
    
    /**
     * 创建副标题标签
     */
    public static JLabel createSubtitleLabel(String text) {
        return createLabel(text, FONT_SUBTITLE, TEXT_REGULAR);
    }
    
    /**
     * 创建正文标签
     */
    public static JLabel createBodyLabel(String text) {
        return createLabel(text, FONT_BODY, TEXT_REGULAR);
    }
    
    /**
     * 创建卡片面板
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_PRIMARY);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHTER, 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }
    
    /**
     * 创建信息按钮
     */
    public static JButton createInfoButton(String text) {
        return createButton(text, INFO);
    }
    
    /**
     * 创建警告按钮
     */
    public static JButton createWarningButton(String text) {
        return createButton(text, WARNING);
    }
    
    /**
     * 创建图标风格的圆形按钮
     */
    public static JButton createIconButton(String text, String tooltip, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(40, 40));
        button.setBorder(BorderFactory.createCompoundBorder(
            createRoundedBorder(bgColor, 8),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    /**
     * 创建样式化的表格行渲染器（交替行颜色 + 选中高亮）
     */
    public static javax.swing.table.DefaultTableCellRenderer createAlternatingRowRenderer() {
        return new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? BG_PRIMARY : BG_TERTIARY);
                    setForeground(TEXT_PRIMARY);
                } else {
                    setBackground(PRIMARY);
                    setForeground(Color.WHITE);
                }
                return c;
            }
        };
    }
    
    /**
     * 创建现代化的弹窗面板（用于对话框内部）
     */
    public static JPanel createDialogPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        return panel;
    }
    
    /**
     * 创建带阴影的面板
     */
    public static JPanel createShadowPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 绘制阴影
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 12, 12);
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);
                
                // 绘制背景
                g2d.setColor(BG_PRIMARY);
                g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 12, 12);
                
                g2d.dispose();
            }
        };
    }
}
