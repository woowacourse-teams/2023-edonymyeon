package edonymyeon.backend.global.config;

import edonymyeon.backend.auth.ui.argumentresolver.AuthArgumentResolver;
import edonymyeon.backend.logging.LoggingInterceptor;
import edonymyeon.backend.post.ui.argumentresolver.HotPostSizingArgumentResolver;
import edonymyeon.backend.post.ui.argumentresolver.PostPagingArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final AuthArgumentResolver authArgumentResolver;
    private final PostPagingArgumentResolver postPagingArgumentResolver;
    private final HotPostSizingArgumentResolver hotPostSizingArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authArgumentResolver);
        resolvers.add(postPagingArgumentResolver);
        resolvers.add(hotPostSizingArgumentResolver);
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor())
                .addPathPatterns("/**");
    }
}
