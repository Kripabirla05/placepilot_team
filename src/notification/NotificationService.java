package notification;

import dao.NotificationDAO;
import dao.UserDAO;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import model.Notification;
import model.Student;

public class NotificationService {

    private static final NotificationService INSTANCE = new NotificationService();

    public static NotificationService getInstance() {
        return INSTANCE;
    }

    private final Queue<Notification> queue = new PriorityQueue<>();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private boolean busy = false;
    private boolean stopped = false;
    private int processedCount = 0;

    private NotificationService() { }

    public synchronized void send(int studentId, String message, int priority) {
        queue.add(new Notification(0, studentId, message, false, priority, null));
        notifyAll();
    }

    public void sendToAll(String message, int priority) {
        try {
            List<Student> students = new UserDAO().findAll();
            for (Student s : students) {
                send(s.getId(), message, priority);
            }
        } catch (SQLException e) {
            System.out.println("Could not send notifications: " + e.getMessage());
        }
    }

    public synchronized Notification take() throws InterruptedException {
        while (queue.isEmpty() && !stopped) {
            wait();
        }
        if (queue.isEmpty()) {
            return null;
        }
        busy = true;
        return queue.poll();
    }

    public synchronized void done() {
        busy = false;
        processedCount++;
        notifyAll();
    }

    public synchronized void waitUntilEmpty() {
        try {
            while (!queue.isEmpty() || busy) {
                wait(2000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public synchronized void stop() {
        stopped = true;
        notifyAll();
    }

    public synchronized int getProcessedCount() {
        return processedCount;
    }

    public synchronized int getPendingCount() {
        return queue.size();
    }

    public void save(Notification n) throws SQLException {
        notificationDAO.insert(n);
    }

    public List<Notification> getInbox(int studentId) throws SQLException {
        waitUntilEmpty();
        return notificationDAO.findByStudent(studentId);
    }

    public void showInbox(int studentId) throws SQLException {
        List<Notification> list = getInbox(studentId);
        if (list.isEmpty()) {
            System.out.println("Inbox is empty.");
            return;
        }

        Iterator<Notification> it = list.iterator();
        int number = 1;
        while (it.hasNext()) {
            Notification n = it.next();
            System.out.printf("%d. [%s] %s %s  (%s)%n", number++, n.getPriorityText(),
                    n.isRead() ? " " : "NEW", n.getMessage(), n.getCreatedAt());
        }
        notificationDAO.markAllRead(studentId);
    }

    public int unreadCount(int studentId) throws SQLException {
        waitUntilEmpty();
        return notificationDAO.countUnread(studentId);
    }

    public void clearInbox(int studentId) throws SQLException {
        notificationDAO.deleteAll(studentId);
    }
}
