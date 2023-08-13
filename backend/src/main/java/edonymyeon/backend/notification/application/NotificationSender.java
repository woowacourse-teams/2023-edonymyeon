package edonymyeon.backend.notification.application;

public interface NotificationSender {
    boolean sendNotification(Receiver receiver, String title);
}
