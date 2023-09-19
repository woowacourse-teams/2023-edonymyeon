package edonymyeon.backend.global.version;

import java.lang.reflect.Method;
import java.util.Optional;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class ApiVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        return createCondition(AnnotationUtils.findAnnotation(handlerType, ApiVersion.class));
    }

    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        return createCondition(AnnotationUtils.findAnnotation(method, ApiVersion.class));
    }

    private RequestCondition<?> createCondition(ApiVersion apiVersion) {
        return Optional.ofNullable(apiVersion)
                .map(item -> new ApiVersionRequestCondition(item.value()))
                .orElse(null);
    }
}
