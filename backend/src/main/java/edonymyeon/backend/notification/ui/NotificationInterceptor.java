package edonymyeon.backend.notification.ui;

import edonymyeon.backend.notification.application.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class NotificationInterceptor implements HandlerInterceptor {

    private static final String NOTIFICATION_IDENTIFIER_KEY = "notificated";

    private final NotificationService notificationService;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
                             final Object handler) {

        final String notificationIdentifier = request.getParameter(NOTIFICATION_IDENTIFIER_KEY);
        if (Objects.nonNull(notificationIdentifier)) {
            notificationService.markNotificationAsRead(Long.valueOf(notificationIdentifier));
        }

        return true;
    }
}
