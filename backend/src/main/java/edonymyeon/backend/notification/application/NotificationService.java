package edonymyeon.backend.notification.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.NOTIFICATION_REQUEST_FAILED;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.post.domain.Post;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    public static final String THUMBS_NOTIFICATION_TITLE = "당신의 글에 누군가 반응을 남겼습니다.";

    public static final String THUMBS_NOTIFICATION_CONTENT = "클릭하여 확인해보세요!";

    private final String API_URL = "https://fcm.googleapis.com/v1/projects/edonymyeon-5c344/messages:send";

    private final ObjectMapper objectMapper;

    public void sendThumbsNotificationToWriter(final Post post) {
        try {
            String message = makeMessage(post.getMember().getDeviceToken(),
                    THUMBS_NOTIFICATION_TITLE,
                    THUMBS_NOTIFICATION_CONTENT);

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(message,
                    MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(requestBody)
                    .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                    .build();

            final Response response = client.newCall(request).execute();

            log.info("Alert info = {}", response.body().string());
        } catch (IOException ioException) {
            throw new BusinessLogicException(NOTIFICATION_REQUEST_FAILED);
        }
    }

    private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build()
                        ).build()).validateOnly(false).build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "edonymyeon-firebase.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
