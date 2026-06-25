# 宠物商店管理系统 - 面向对象程序设计课程设计

## 项目简介

本项目是一个基于Java的宠物商店管理系统，采用MVC架构设计，实现了宠物信息管理、库存管理、销售管理和数据统计等功能。系统采用Swing图形用户界面，提供现代化的用户体验。

## 技术栈

| 技术 | 版本 | 说明 |
|-----|------|------|
| Java | JDK 26 | 开发语言 |
| Swing | 内置 | GUI图形界面 |
| JFreeChart | 1.5+ | 数据统计图表 |
| iText | 2.1.7 | PDF导出功能 |

## 开发环境配置

### 1. 安装JDK 26

1. 访问 [Oracle官网](https://www.oracle.com/java/technologies/downloads/) 下载JDK 26
2. 安装JDK并配置环境变量：
   ```
   JAVA_HOME = C:\Program Files\Java\jdk-26
   Path 中添加 %JAVA_HOME%\bin
   ```
3. 验证安装：
   ```bash
   java -version
   ```

### 2. IntelliJ IDEA配置

#### 2.1 导入项目

1. 打开IntelliJ IDEA
2. 选择 `File` -> `Open`
3. 选择项目根目录 `pet-shop`
4. 点击 `OK` 导入项目

#### 2.2 配置JDK

1. 选择 `File` -> `Project Structure` -> `Project`
2. 在 `SDK` 下拉菜单中选择JDK 26
3. 在 `Language level` 中选择 `26`

#### 2.3 配置模块依赖

1. 选择 `File` -> `Project Structure` -> `Modules`
2. 选择项目模块，点击 `Dependencies` 标签
3. 点击 `+` -> `JARs or directories`
4. 添加以下库文件：
   - `jfreechart-1.5.jar`
   - `jcommon-1.0.jar`
   - `itext-2.1.7.jar`

#### 2.4 配置源代码目录

1. 在 `Project Structure` -> `Modules` -> `Sources`
2. 确认 `src` 目录被标记为 `Sources`（蓝色文件夹）

### 3. 运行项目

1. 在项目中找到 `src/com/petshop/Main.java`
2. 右键点击 `Main.java`
3. 选择 `Run 'Main.main()'`
4. 系统将显示登录对话框，输入用户名和密码后进入主界面

## 项目结构

```
pet-shop/
├── src/
│   └── com/
│       └── petshop/
│           ├── controller/      # 控制器层
│           │   ├── MainController.java
│           │   ├── PetController.java
│           │   ├── InventoryController.java
│           │   └── SalesController.java
│           ├── dao/             # 数据访问层
│           │   ├── FileDao.java
│           │   ├── PetDao.java
│           │   ├── InventoryDao.java
│           │   └── SalesRecordDao.java
│           ├── exception/       # 自定义异常
│           │   └── BusinessException.java
│           ├── gui/             # GUI图形界面
│           │   ├── MainFrame.java
│           │   ├── LoginDialog.java
│           │   ├── PetManagementPanel.java
│           │   ├── InventoryManagementPanel.java
│           │   ├── SalesManagementPanel.java
│           │   ├── StatisticsPanel.java
│           │   ├── BreadcrumbPanel.java
│           │   └── Toast.java
│           ├── model/           # 数据模型
│           │   ├── Pet.java
│           │   ├── Dog.java
│           │   ├── Cat.java
│           │   ├── Inventory.java
│           │   └── SalesRecord.java
│           ├── service/         # 业务逻辑层
│           │   ├── PetService.java
│           │   ├── InventoryService.java
│           │   └── SalesService.java
│           ├── util/            # 工具类
│           │   ├── TableUtil.java
│           │   ├── DialogUtil.java
│           │   └── Theme.java
│           └── Main.java        # 程序入口
├── data/                        # 数据文件目录
│   ├── pets.dat
│   ├── inventory.dat
│   └── sales.dat
├── 课程设计说明书.md
└── README.md
```

## 默认登录信息

| 项目 | 值 |
|-----|-----|
| 用户名 | admin |
| 密码 | 123456 |

## 功能说明

| 功能 | 说明 |
|-----|------|
| 登录系统 | 管理员登录验证（用户名：admin，密码：123456） |
| 宠物管理 | 表格展示宠物信息，支持增删改查 |
| 库存管理 | 库存查询、预警提醒 |
| 销售管理 | 销售记录管理 |
| 数据统计 | JFreeChart图表展示、导出PNG/PDF |

## 常见问题

### Q1: 编译错误"找不到符号"

**原因**：JDK版本不匹配或依赖库未配置

**解决**：
1. 检查 `Project Structure` -> `Project` 中的JDK版本
2. 确保依赖库已正确添加

### Q2: 数据文件找不到

**原因**：`data` 目录不存在

**解决**：
1. 在项目根目录下创建 `data` 文件夹
2. 程序会自动创建数据文件

### Q3: GUI界面显示异常

**原因**：系统外观主题不兼容

**解决**：
程序已内置Nimbus外观主题，如仍有问题可尝试修改 `MainFrame.java` 中的主题设置

## 许可证

本项目为课程设计项目，仅供学习参考。
