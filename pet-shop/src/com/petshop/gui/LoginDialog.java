package com.petshop.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * 登录对话框
 */
public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private boolean loginSuccess = false;
    
    // 默认用户名和密码 (实际项目中应该从数据库或配置文件读取)
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "123456";
    
    public LoginDialog(JFrame parent) {
        super(parent, "用户登录", true);
        initializeDialog();
        createComponents();
    }
    
    private void initializeDialog() {
        setSize(400, 300);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 设置现代化外观
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void createComponents() {
        // 标题面板
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(51, 153, 255));
        titlePanel.setPreferredSize(new Dimension(400, 80));
        titlePanel.setLayout(new GridBagLayout());
        
        JLabel titleLabel = new JLabel("宠物商店管理系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        add(titlePanel, BorderLayout.NORTH);
        
        // 表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 用户名标签和输入框
        JLabel userLabel = new JLabel("用户名:");
        userLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(userLabel, gbc);
        
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        // 不设置默认用户名，保持为空
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        formPanel.add(usernameField, gbc);
        
        // 密码标签和输入框
        JLabel passLabel = new JLabel("密  码:");
        passLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(passLabel, gbc);
        
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        // 不设置默认密码，保持为空
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        formPanel.add(passwordField, gbc);
        
        // 添加回车键监听
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
        
        add(formPanel, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton loginButton = new JButton("登录");
        loginButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        loginButton.setPreferredSize(new Dimension(100, 35));
        loginButton.addActionListener(e -> performLogin());
        
        JButton cancelButton = new JButton("取消");
        cancelButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.addActionListener(e -> System.exit(0));
        
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * 执行登录验证
     */
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        
        // 简单验证
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "用户名和密码不能为空！", 
                "提示", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 验证用户名密码 (实际项目中应该查询数据库)
        if (DEFAULT_USERNAME.equals(username) && DEFAULT_PASSWORD.equals(password)) {
            loginSuccess = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "用户名或密码错误！", 
                "登录失败", 
                JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }
    
    /**
     * 显示登录对话框并返回是否登录成功
     */
    public boolean showLogin() {
        setVisible(true);
        return loginSuccess;
    }
    
    /**
     * 获取当前登录的用户名
     */
    public String getUsername() {
        return usernameField.getText().trim();
    }
}
