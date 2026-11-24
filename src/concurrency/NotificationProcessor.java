package concurrency;

import java.sql.SQLException;
import model.Notification;
import notification.NotificationService;

public class NotificationProcessor implements Runnable {

    private final NotificationService service = NotificationService.getInstance();

    @Override
    public void run() {
        System.out.println("[Notification thread started: " + Thread.currentThread().getName() + "]");
        try {
            while (true) {
                Notification n = service.take();
                if (n == null) {
                    break;
                }
                try {
                    service.save(n);
                } catch (SQLException e) {
                    System.out.println("[Notification save failed: " + e.getMessage() + "]");
                } finally {
                    service.done();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("[Notification thread interrupted]");
        }
        System.out.println("[Notification thread finished]");
    }
}
