package com.petshop.dao;

import com.petshop.model.SalesRecord;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class SalesRecordDao {
    private static final String DATA_FILE = "data/sales.dat";
    private List<SalesRecord> records;

    public SalesRecordDao() {
        this.records = new ArrayList<>();
        loadRecords();
    }

    private void loadRecords() {
        try {
            Object obj = FileDao.loadObject(DATA_FILE);
            if (obj != null) {
                this.records = (List<SalesRecord>) obj;
            }
        } catch (Exception e) {
            this.records = new ArrayList<>();
        }
    }

    private void saveRecords() {
        try {
            new File("data").mkdirs();
            FileDao.saveObject(DATA_FILE, records);
        } catch (IOException e) {
            System.out.println("保存销售记录失败：" + e.getMessage());
        }
    }

    public void add(SalesRecord record) {
        records.add(record);
        saveRecords();
    }

    public SalesRecord findById(String recordId) {
        for (SalesRecord record : records) {
            if (record.getRecordId().equals(recordId)) return record;
        }
        return null;
    }

    public List<SalesRecord> findAll() { return new ArrayList<>(records); }

    public List<SalesRecord> findByTimeRange(LocalDateTime start, LocalDateTime end) {
        List<SalesRecord> result = new ArrayList<>();
        for (SalesRecord record : records) {
            if (!record.getSaleTime().isBefore(start) && !record.getSaleTime().isAfter(end)) {
                result.add(record);
            }
        }
        return result;
    }

    /**
     * 根据宠物ID查找并删除最新的销售记录（用于状态回退）
     */
    public boolean deleteByPetId(String petId) {
        // 找到该宠物的最新销售记录
        SalesRecord latestRecord = null;
        int index = -1;
        
        for (int i = 0; i < records.size(); i++) {
            SalesRecord record = records.get(i);
            if (record.getPetId().equals(petId)) {
                if (latestRecord == null || record.getSaleTime().isAfter(latestRecord.getSaleTime())) {
                    latestRecord = record;
                    index = i;
                }
            }
        }
        
        if (index != -1) {
            records.remove(index);
            saveRecords();
            return true;
        }
        return false;
    }
}
