package edonymyeon.backend.global.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

        MDC.put("uri", request.getRequestURI());
        MDC.put("request-identifier", UUID.randomUUID().toString());
        return true;
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response,
                                final Object handler, final Exception ex)
            throws Exception {
        MDC.put("statuscode", String.valueOf(response.getStatus()));

        ContentCachingResponseWrapper responseWrapper = getResponseWrapper(response);
        String responseBody = getResponseBody(responseWrapper);
        log.info("body {} ", responseBody);
        // 클라이언트로 전달 전 실제 response 객체에 copy
        responseWrapper.copyBodyToResponse();

        MDC.clear();
    }

    private String getResponseBody(ContentCachingResponseWrapper responseWrapper) {
        return new String(responseWrapper.getContentAsByteArray());
    }

    private ContentCachingResponseWrapper getResponseWrapper(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper)response;
        }
        return new ContentCachingResponseWrapper(response);
    }
}
