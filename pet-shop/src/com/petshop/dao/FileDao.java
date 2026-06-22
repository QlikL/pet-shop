package com.petshop.dao;

import java.io.*;

public class FileDao {

    public static void saveObject(String filePath, Object obj) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(obj);
        }
    }

    public static Object loadObject(String filePath) throws IOException, ClassNotFoundException {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return ois.readObject();
        }
    }

    public static void saveText(String filePath, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        }
    }

    public static String loadText(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) return "";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    public static void backupFile(String sourcePath, String backupPath) throws IOException {
        File source = new File(sourcePath);
        if (!source.exists()) return;
        File backupDir = new File(backupPath).getParentFile();
        if (!backupDir.exists()) backupDir.mkdirs();
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(backupPath)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }
}
