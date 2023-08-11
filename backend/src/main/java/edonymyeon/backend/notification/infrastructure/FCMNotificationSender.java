package edonymyeon.backend.notification.infrastructure;

import static edonymyeon.backend.global.exception.ExceptionInformation.NOTIFICATION_REQUEST_FAILED;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.notification.application.NotificationSender;
import edonymyeon.backend.notification.application.Receiver;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FCMNotificationSender implements NotificationSender {

    public static final String FIREBASE_ADMIN_KEY_PATH = "edonymyeon-firebase.json";
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/edonymyeon-5c344/messages:send";

    private final ObjectMapper objectMapper;

    @Override
    public boolean sendNotification(final Receiver receiver, final String title, final String content) {
        try {
            final String requestBody = makeFCMNotificationRequestBody(receiver.getToken(), title, content);
            final OkHttpClient client = new OkHttpClient();
            final Request request = makeFCMNotificationRequest(requestBody);
            final Response response = sendFCMNotificationRequest(client, request);
            return response.isSuccessful();
        } catch (IOException ioException) {
            throw new BusinessLogicException(NOTIFICATION_REQUEST_FAILED);
        }
    }

    @NotNull
    private static Response sendFCMNotificationRequest(final OkHttpClient client, final Request request) throws IOException {
        return client.newCall(request).execute();
    }

    @NotNull
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

    private String makeFCMNotificationRequestBody(String targetToken, String title, String body) throws JsonProcessingException {
        final FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build()
                        ).build())
                .validateOnly(false).build();
        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getFCMAccessToken() throws IOException {
        final GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(FIREBASE_ADMIN_KEY_PATH).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
