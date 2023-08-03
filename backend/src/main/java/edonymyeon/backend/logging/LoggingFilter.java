package edonymyeon.backend.logging;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * 서블릿 필터 사이에 개입하여,
 * ServletResponse를 ContentCachingResponseWrapper로 바꾸어 체이닝에 등록하는 필터입니다.
 * ContentCachingResponseWrapper를 사용하면
 * ServletResponse와 달리 responseBody 내용을 꺼내 쓸 수 있기 때문에
 * 로깅에 활용할 수 있습니다.
**/
@Component
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        ContentCachingResponseWrapper httpServletResponse = new ContentCachingResponseWrapper(
                (HttpServletResponse) response);

        chain.doFilter(request, httpServletResponse);
        httpServletResponse.copyBodyToResponse();
    }
}
