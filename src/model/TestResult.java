package model;

public class TestResult implements Displayable {

    private int id;
    private int studentId;
    private String testType;
    private int score;
    private int total;
    private String testDate;

    public TestResult(int id, int studentId, String testType, int score, int total, String testDate) {
        this.id = id;
        this.studentId = studentId;
        this.testType = testType;
        this.score = score;
        this.total = total;
        this.testDate = testDate;
    }

    public int getId() { return id; }
    public int getStudentId() { return studentId; }
    public String getTestType() { return testType; }
    public int getScore() { return score; }
    public int getTotal() { return total; }
    public String getTestDate() { return testDate; }

    public double getPercentage() {
        return total == 0 ? 0 : (score * 100.0) / total;
    }

    @Override
    public void display() {
        System.out.printf("%-12s %d/%d (%.0f%%)  on %s%n", testType, score, total, getPercentage(), testDate);
    }
}
