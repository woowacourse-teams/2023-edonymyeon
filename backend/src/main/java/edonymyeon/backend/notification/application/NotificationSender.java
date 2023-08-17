package edonymyeon.backend.notification.application;

public interface NotificationSender {
    void sendNotification(Receiver receiver, String title);
}
