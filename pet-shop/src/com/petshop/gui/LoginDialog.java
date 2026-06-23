package com.petshop.gui;

import com.petshop.util.Theme;
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
        setSize(420, 340);
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
        titlePanel.setBackground(Theme.PRIMARY);
        titlePanel.setPreferredSize(new Dimension(420, 90));
        titlePanel.setLayout(new GridBagLayout());
        
        JLabel titleLabel = new JLabel("宠物商店管理系统");
        titleLabel.setFont(Theme.FONT_TITLE_LARGE);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        add(titlePanel, BorderLayout.NORTH);
        
        // 表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Theme.BG_PRIMARY);
        formPanel.setBorder(BorderFactory.createEmptyBorder(32, 48, 32, 48));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 用户名标签和输入框
        JLabel userLabel = Theme.createBodyLabel("用户名:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(userLabel, gbc);
        
        usernameField = Theme.createTextField(16);
        // 不设置默认用户名，保持为空
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        formPanel.add(usernameField, gbc);
        
        // 密码标签和输入框
        JLabel passLabel = Theme.createBodyLabel("密  码:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(passLabel, gbc);
        
        passwordField = Theme.createPasswordField(16);
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
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 16));
        buttonPanel.setBackground(Theme.BG_PRIMARY);
        
        JButton loginButton = Theme.createPrimaryButton("登录");
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.addActionListener(e -> performLogin());
        
        JButton cancelButton = Theme.createSecondaryButton("取消");
        cancelButton.setPreferredSize(new Dimension(120, 40));
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
            com.petshop.util.DialogUtil.showWarning(this, "用户名和密码不能为空！");
            return;
        }
        
        // 验证用户名密码 (实际项目中应该查询数据库)
        if (DEFAULT_USERNAME.equals(username) && DEFAULT_PASSWORD.equals(password)) {
            loginSuccess = true;
            dispose();
        } else {
            com.petshop.util.DialogUtil.showError(this, "登录失败", "用户名或密码错误！");
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
