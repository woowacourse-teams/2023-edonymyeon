package edonymyeon.backend.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
            throws Exception {

        String requestURI = getRequestURI(request);

        MDC.put("uri", requestURI);
        MDC.put("request-identifier", UUID.randomUUID().toString());
        return true;
    }

    private static String getRequestURI(final HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        final String queryString = request.getQueryString();

        if (Objects.nonNull(queryString)) {
            requestURI = requestURI + "?" + queryString;
        }

        return requestURI;
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response,
                                final Object handler, final Exception ex)
            throws Exception {
        MDC.put("statuscode", String.valueOf(response.getStatus()));
        MDC.put("request-contentType", request.getContentType());
        MDC.put("response-contentType", response.getContentType());
        if (isContentTypeNotLoggingResponseBody(response)) {
            MDC.clear();
            return;
        }
        logResponseBody(response);
    }

    private static boolean isContentTypeNotLoggingResponseBody(final HttpServletResponse response) {
        return Objects.nonNull(response.getContentType()) &&
                (response.getContentType().contains(MediaType.TEXT_HTML_VALUE) ||
                response.getContentType().contains(MediaType.IMAGE_GIF_VALUE) ||
                response.getContentType().contains(MediaType.IMAGE_JPEG_VALUE) ||
                response.getContentType().contains(MediaType.IMAGE_PNG_VALUE));
    }

    private void logResponseBody(final HttpServletResponse response) throws IOException {
        ContentCachingResponseWrapper responseWrapper = getResponseWrapper(response);
        log.info("body {} ", getResponseBody(responseWrapper));
    }

    private String getResponseBody(ContentCachingResponseWrapper responseWrapper) {
        return new String(responseWrapper.getContentAsByteArray());
    }

    /***
     * HttpServletResponse를 ContentCachingResponseWrapper로 형변환하는 메소드입니다.
     * ContentCachingResponseWrapper로부터 ResponseBody 내용을 추출할 수 있습니다.
     */
    private ContentCachingResponseWrapper getResponseWrapper(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        }
        return new ContentCachingResponseWrapper(response);
    }
}
