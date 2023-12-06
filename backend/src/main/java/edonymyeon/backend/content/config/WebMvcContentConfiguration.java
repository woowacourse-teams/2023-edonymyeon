package edonymyeon.backend.content.config;

import edonymyeon.backend.content.post.ui.argumentresolver.HotPostSizingArgumentResolver;
import edonymyeon.backend.content.post.ui.argumentresolver.PostPagingArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebMvcContentConfiguration implements WebMvcConfigurer {

    private final PostPagingArgumentResolver postPagingArgumentResolver;
    private final HotPostSizingArgumentResolver hotPostSizingArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(postPagingArgumentResolver);
        resolvers.add(hotPostSizingArgumentResolver);
    }
}
