## Why

当前宠物商店管理系统基于控制台界面，操作效率低且不够直观。管理员需要频繁切换菜单、手动输入命令，对于数据查看和操作不够便捷。随着业务数据量增长，需要更高效的管理界面来提升管理员的工作效率。

## What Changes

- 将现有控制台界面升级为基于Java Swing的图形用户界面（GUI）
- 创建主窗口界面，集成所有管理功能模块
- 为宠物管理、库存管理、销售管理、数据统计分别创建独立的GUI界面
- 保持现有业务逻辑层（Service、DAO、Model）不变，仅替换视图层
- 添加数据可视化功能，包括表格展示、图表统计等

## Capabilities

### New Capabilities
- `gui-main-window`: 主窗口界面，包含菜单栏、状态栏和功能导航
- `gui-pet-management`: 宠物管理GUI界面，提供宠物信息的增删改查功能
- `gui-inventory-management`: 库存管理GUI界面，提供库存查询、更新、预警设置等功能
- `gui-sales-management`: 销售管理GUI界面，提供销售记录的添加、查询、统计等功能
- `gui-statistics`: 数据统计GUI界面，提供库存统计、销售统计等可视化图表

### Modified Capabilities
<!-- 无需修改现有规范，因为业务逻辑层保持不变 -->

## Impact

- 需要添加Java Swing相关依赖（已包含在JDK中）
- 需要修改Main.java以启动GUI应用
- 需要创建新的GUI视图类（gui包）
- 需要调整Controller层以支持GUI事件处理
- 现有数据文件格式保持不变，确保数据兼容性