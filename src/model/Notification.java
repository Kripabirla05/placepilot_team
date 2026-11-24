package model;

public class Notification implements Comparable<Notification> {

    private int id;
    private int studentId;
    private String message;
    private boolean read;
    private int priority;
    private String createdAt;

    public Notification(int id, int studentId, String message, boolean read, int priority, String createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.message = message;
        this.read = read;
        this.priority = priority;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getStudentId() { return studentId; }
    public String getMessage() { return message; }
    public boolean isRead() { return read; }
    public int getPriority() { return priority; }
    public String getCreatedAt() { return createdAt; }

    public String getPriorityText() {
        switch (priority) {
            case 1:  return "HIGH";
            case 2:  return "MEDIUM";
            default: return "LOW";
        }
    }

    @Override
    public int compareTo(Notification other) {
        return Integer.compare(this.priority, other.priority);
    }
}
