package edonymyeon.backend.notification.application;

import edonymyeon.backend.notification.application.dto.Receiver;

public interface NotificationSender {
    void sendNotification(Receiver receiver, String title);
}
