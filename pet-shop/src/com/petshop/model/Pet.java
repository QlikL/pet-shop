package com.petshop.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Pet implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String breed; // 品种（原来是name）
    private String species; // 种类（狗、猫、其他）
    private int birthYear; // 出生年份
    private int birthMonth; // 出生月份
    private double age; // 年龄（自动计算）
    private double price;
    private String status;

    public Pet() {
    }

    /**
     * 创建宠物对象
     * @param id 宠物ID
     * @param breed 品种
     * @param species 种类
     * @param birthYear 出生年份
     * @param birthMonth 出生月份
     * @param price 价格
     * @param status 状态
     */
    public Pet(String id, String breed, String species, int birthYear, int birthMonth, double price, String status) {
        this.id = id;
        this.breed = breed;
        this.species = species;
        this.birthYear = birthYear;
        this.birthMonth = birthMonth;
        this.price = price;
        this.status = status;
        // 计算年龄
        this.age = calculateAge();
    }

    /**
     * 根据出生年月计算年龄
     * @return 年龄（以年为单位，支持小数）
     */
    public double calculateAge() {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();
        
        // 计算年份差
        int yearDiff = currentYear - birthYear;
        // 计算月份差
        int monthDiff = currentMonth - birthMonth;
        
        // 如果当前月份小于出生月份，年龄减1
        if (monthDiff < 0) {
            yearDiff--;
            monthDiff += 12;
        }
        
        // 返回年龄，支持小数（月份/12）
        return yearDiff + (monthDiff / 12.0);
    }

    /**
     * 更新年龄（登录时调用）
     */
    public void updateAge() {
        this.age = calculateAge();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public int getBirthYear() { return birthYear; }
    public void setBirthYear(int birthYear) { this.birthYear = birthYear; }
    public int getBirthMonth() { return birthMonth; }
    public void setBirthMonth(int birthMonth) { this.birthMonth = birthMonth; }
    public double getAge() { return age; }
    public void setAge(double age) { this.age = age; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Pet{id='" + id + "', breed='" + breed + "', species='" + species +
               "', birthYear=" + birthYear + ", birthMonth=" + birthMonth +
               "', age=" + age + ", price=" + price + ", status='" + status + "'}";
    }
}
