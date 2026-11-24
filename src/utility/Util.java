package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Util {

    public static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidName(String name) {
        if (isEmpty(name) || name.length() < 2) {
            return false;
        }
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!Character.isLetter(c) && c != ' ' && c != '.') {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidEmail(String email) {
        if (isEmpty(email) || email.contains(" ")) {
            return false;
        }
        int at = email.indexOf('@');
        int dot = email.lastIndexOf('.');
        return at > 0 && dot > at + 1 && dot < email.length() - 1;
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidCgpa(double cgpa) {
        return cgpa >= 0.0 && cgpa <= 10.0;
    }

    public static boolean isValidBacklogs(int backlogs) {
        return backlogs >= 0 && backlogs <= 20;
    }

    public static Set<String> toSkillSet(String skills) {
        Set<String> set = new LinkedHashSet<>();
        if (isEmpty(skills)) {
            return set;
        }
        for (String s : skills.split(",")) {
            String skill = s.trim().toLowerCase();
            if (!skill.isEmpty()) {
                set.add(skill);
            }
        }
        return set;
    }

    public static String cleanSkills(String skills) {
        StringBuilder sb = new StringBuilder();
        for (String s : toSkillSet(skills)) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1));
        }
        return sb.toString();
    }

    private static final Scanner sc = new Scanner(System.in);

    public static String readString(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    public static int readInt(String prompt) {
        while (true) {
            String text = readString(prompt);
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a whole number.");
            }
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            String text = readString(prompt);
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a number (example 7.5).");
            }
        }
    }

    public static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int n = readInt(prompt);
            if (n >= min && n <= max) {
                return n;
            }
            System.out.println("  Enter a number from " + min + " to " + max + ".");
        }
    }

    public static boolean readYesNo(String prompt) {
        String s = readString(prompt + " (y/n): ");
        return s.equalsIgnoreCase("y") || s.equalsIgnoreCase("yes");
    }

    public static void pressEnter() {
        readString("\nPress Enter to continue...");
    }

    public static void print(String[] headers, List<String[]> rows) {
        int[] width = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            width[i] = headers[i].length();
        }
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                width[i] = Math.max(width[i], row[i].length());
            }
        }

        String line = makeLine(width);
        System.out.println(line);
        System.out.println(makeRow(headers, width));
        System.out.println(line);
        for (String[] row : rows) {
            System.out.println(makeRow(row, width));
        }
        System.out.println(line);
    }

    private static String makeRow(String[] cells, int[] width) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < cells.length; i++) {
            sb.append(" ").append(String.format("%-" + width[i] + "s", cells[i])).append(" |");
        }
        return sb.toString();
    }

    private static String makeLine(int[] width) {
        StringBuilder sb = new StringBuilder("+");
        for (int w : width) {
            for (int i = 0; i < w + 2; i++) {
                sb.append('-');
            }
            sb.append('+');
        }
        return sb.toString();
    }

    public static void title(String text) {
        System.out.println("\n===== " + text + " =====");
    }

    public static List<String> readLines(String path) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    public static List<String[]> readCsv(String path) throws IOException {
        List<String[]> rows = new ArrayList<>();
        for (String line : readLines(path)) {
            if (!line.trim().isEmpty()) {
                rows.add(line.split(",", -1));
            }
        }
        return rows;
    }

    public static void writeLines(String path, List<String> lines) throws IOException {
        File parent = new File(path).getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            for (String line : lines) {
                pw.println(line);
            }
        }
    }

    public static <T> void exportList(String path, String header, List<T> items) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(header);
        for (T item : items) {
            lines.add(item.toString());
        }
        writeLines(path, lines);
    }

    public static void copyFile(String from, String to) throws IOException {
        File parent = new File(to).getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (FileInputStream in = new FileInputStream(from);
             FileOutputStream out = new FileOutputStream(to)) {
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
        }
    }
}
