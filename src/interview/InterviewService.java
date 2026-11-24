package interview;

import concurrency.TestTimer;
import dao.TestResultDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import model.Question;
import model.Student;
import utility.Util;

public class InterviewService {

    private static final int QUESTIONS = 5;
    private static final int SECONDS = 90;

    private final TestResultDAO testResultDAO = new TestResultDAO();

    public void startMcq(Student student) throws IOException, SQLException {
        List<Question> list = getRandom("MCQ", QUESTIONS);
        if (list.isEmpty()) {
            System.out.println("No MCQ questions found in data/questions.csv");
            return;
        }

        System.out.println("\n--- MCQ Test: " + list.size() + " questions, " + SECONDS + " seconds ---");
        System.out.println("Type A, B, C or D. Press Enter to start.");
        Util.readString("");

        TestTimer timer = new TestTimer(SECONDS);
        timer.start();

        String[][] sheet = new String[list.size()][2];
        int attempted = 0;

        for (int i = 0; i < list.size(); i++) {
            Question q = list.get(i);
            System.out.println("\nQ" + (i + 1) + ". " + q.getText());
            String[] letters = {"A", "B", "C", "D"};
            for (int k = 0; k < 4; k++) {
                System.out.println("   " + letters[k] + ") " + q.getOptions()[k]);
            }
            String ans = Util.readString("Answer: ").toUpperCase();

            if (timer.isTimeUp()) {
                System.out.println("Time was over. Test stopped.");
                sheet[i][0] = "-";
                sheet[i][1] = q.getAnswer();
                for (int j = i + 1; j < list.size(); j++) {
                    sheet[j][0] = "-";
                    sheet[j][1] = list.get(j).getAnswer();
                }
                break;
            }
            sheet[i][0] = ans;
            sheet[i][1] = q.getAnswer().toUpperCase();
            attempted++;
        }

        timer.interrupt();

        int score = 0;
        System.out.println("\n--- Result ---");
        for (int i = 0; i < sheet.length; i++) {
            boolean correct = sheet[i][0] != null && sheet[i][0].equals(sheet[i][1]);
            if (correct) {
                score++;
            }
            System.out.println("Q" + (i + 1) + ": your answer = " + sheet[i][0]
                    + ", correct = " + sheet[i][1] + (correct ? "  [OK]" : "  [WRONG]"));
        }
        System.out.println("Score: " + score + "/" + sheet.length + "  (attempted " + attempted + ")");
        testResultDAO.insert(student.getId(), "MCQ", score, sheet.length);
        System.out.println("Result saved.");
    }

    public void startMock(Student student, String type) throws IOException, SQLException {
        List<Question> list = getRandom(type, 3);
        if (list.isEmpty()) {
            System.out.println("No " + type + " questions found in data/questions.csv");
            return;
        }

        System.out.println("\n--- " + (type.equals("TECH") ? "Technical" : "HR") + " Mock Interview ---");
        System.out.println("Answer in 1-2 sentences. Use the important words in your answer.\n");

        int score = 0;
        int total = 0;
        int number = 1;
        for (Question q : list) {
            System.out.println("Q" + number++ + ". " + q.getText());
            String answer = Util.readString("Your answer: ").toLowerCase();

            String[] keywords = q.getKeywords();
            int found = 0;
            StringBuilder missing = new StringBuilder();
            for (String k : keywords) {
                if (answer.contains(k.trim())) {
                    found++;
                } else {
                    missing.append(k.trim()).append(", ");
                }
            }
            score += found;
            total += keywords.length;
            System.out.println("  Keywords matched: " + found + "/" + keywords.length);
            if (missing.length() > 0) {
                System.out.println("  You could also mention: " + missing.substring(0, missing.length() - 2));
            }
            System.out.println();
        }

        System.out.println("Interview score: " + score + "/" + total);
        testResultDAO.insert(student.getId(), "MOCK_" + type, score, total);
        System.out.println("Result saved.");
    }

    private static final String FILE_PATH = "data/questions.csv";

    private List<Question> loadAll() throws IOException {
        List<Question> questions = new ArrayList<>();
        List<String[]> rows = Util.readCsv(FILE_PATH);
        for (int i = 1; i < rows.size(); i++) {
            String[] r = rows.get(i);
            if (r.length < 7 || r[1].trim().isEmpty()) {
                continue;
            }
            String[] options = {r[2].trim(), r[3].trim(), r[4].trim(), r[5].trim()};
            questions.add(new Question(r[0].trim().toUpperCase(), r[1].trim(), options, r[6].trim()));
        }
        return questions;
    }

    private List<Question> getRandom(String type, int count) throws IOException {
        List<Question> list = loadAll().stream()
                .filter(q -> q.getType().equals(type))
                .collect(Collectors.toList());
        Collections.shuffle(list);
        return list.size() > count ? new ArrayList<>(list.subList(0, count)) : list;
    }
}