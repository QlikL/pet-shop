package com.petshop.util;

public class TableUtil {

    public static void printSeparator(int length) {
        for (int i = 0; i < length; i++) System.out.print("-");
        System.out.println();
    }

    public static void printTitle(String title, int width) {
        printSeparator(width);
        int padding = (width - title.length()) / 2;
        for (int i = 0; i < padding; i++) System.out.print(" ");
        System.out.println(title);
        printSeparator(width);
    }

    public static String formatRow(String... columns) {
        StringBuilder sb = new StringBuilder();
        for (String col : columns) sb.append(String.format("%-15s", col));
        return sb.toString();
    }

    public static void printTable(String[] headers, String[][] data) {
        System.out.println(formatRow(headers));
        printSeparator(headers.length * 15);
        for (String[] row : data) System.out.println(formatRow(row));
        printSeparator(headers.length * 15);
    }
}
