package edonymyeon.backend.global.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
        logResponseBody(response);
        MDC.clear();
    }

    private void logResponseBody(final HttpServletResponse response) throws IOException {
        ContentCachingResponseWrapper responseWrapper = getResponseWrapper(response);
        log.info("body {} ", getResponseBody(responseWrapper));
        responseWrapper.copyBodyToResponse();
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
            return (ContentCachingResponseWrapper)response;
        }
        return new ContentCachingResponseWrapper(response);
    }
}
