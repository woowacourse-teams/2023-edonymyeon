package edonymyeon.backend.notification.infrastructure;

import static edonymyeon.backend.global.exception.ExceptionInformation.NOTIFICATION_REQUEST_FAILED;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.notification.application.NotificationSender;
import edonymyeon.backend.notification.application.dto.Receiver;
import edonymyeon.backend.notification.application.dto.Data;
import edonymyeon.backend.notification.infrastructure.FcmMessage.Message;
import edonymyeon.backend.notification.infrastructure.FcmMessage.Notification;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FCMNotificationSender implements NotificationSender {

    public static final String FIREBASE_ADMIN_KEY_PATH = "/firebase/edonymyeon-firebase.json";
    private static final String API_URL = "https://fcm.googleapis.com/v1/projects/edonymyeon-5c344/messages:send";

    private final ObjectMapper objectMapper;

    private boolean isSentSuccessfully(final Response response) throws IOException {
        final boolean isSuccessful = Objects.equals(response.code(), HttpStatus.OK.value());
        if (!isSuccessful) {
            log.error("FCM 알림 발송 실패 - {}", response.body().string());
        }
        return isSuccessful;
    }

    @Override
    public void sendNotification(final Receiver receiver, final String title) {
        try {
            final String requestBody = makeFCMNotificationRequestBody(receiver.getToken(), title, receiver.getData());
            log.info("FCM 요청 발송 시작 - {}", requestBody);
            final OkHttpClient client = new OkHttpClient();
            final Request request = makeFCMNotificationRequest(requestBody);
            log.info("FCM 요청 발송 시작 - {}", request.body().toString());
            final Response response = sendFCMNotificationRequest(client, request);
            if (!isSentSuccessfully(response)) {
                throw new BusinessLogicException(NOTIFICATION_REQUEST_FAILED);
            }
        } catch (IOException ioException) {
            log.error("FCM 알림 발송 도중 IOException 발생", ioException);
            throw new BusinessLogicException(NOTIFICATION_REQUEST_FAILED);
        }
    }

    private String makeFCMNotificationRequestBody(String targetToken, String title, Data data)
            throws JsonProcessingException {
        final FcmMessage fcmMessage = buildFcmMessage(targetToken, title, data);
        return objectMapper.writeValueAsString(fcmMessage);
    }

    private FcmMessage buildFcmMessage(final String targetToken, final String title, final Data data) {
        return FcmMessage.builder()
                .validateOnly(false)
                .message(buildMessage(targetToken, title, data)
                ).build();
    }

    private Message buildMessage(final String targetToken, final String title, final Data data) {
        return Message.builder()
                .notification(Notification.builder()
                        .title(title)
                        .build())
                .data(data)
                .token(targetToken)
                .build();
    }

    private Request makeFCMNotificationRequest(final String message) throws IOException {
        final RequestBody requestBody = RequestBody.create(message,
                MediaType.get("application/json; charset=utf-8"));
        return new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getFCMAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();
    }

    private String getFCMAccessToken() throws IOException {
        final GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(FIREBASE_ADMIN_KEY_PATH).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    private Response sendFCMNotificationRequest(final OkHttpClient client, final Request request)
            throws IOException {
        return client.newCall(request).execute();
    }
}
