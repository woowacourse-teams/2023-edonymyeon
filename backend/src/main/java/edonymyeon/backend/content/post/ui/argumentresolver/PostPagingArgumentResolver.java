package edonymyeon.backend.content.post.ui.argumentresolver;

import edonymyeon.backend.content.post.application.GeneralFindingCondition;
import edonymyeon.backend.content.post.ui.annotation.PostPaging;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import java.util.Objects;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class PostPagingArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().equals(GeneralFindingCondition.class)
                && parameter.hasParameterAnnotation(PostPaging.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) {
        final String page = webRequest.getParameter("page");
        final String size = webRequest.getParameter("size");
        final String sortBy = webRequest.getParameter("sort-by");
        final String sortDirection = webRequest.getParameter("sort-direction");

        return GeneralFindingCondition.of(
                convertToNumber(page),
                convertToNumber(size),
                sortBy,
                sortDirection
        );
    }

    private static Integer convertToNumber(final String target) {
        if (Objects.nonNull(target)) {
            try {
                return Integer.parseInt(target);
            } catch (NumberFormatException e) {
                throw new EdonymyeonException(ExceptionInformation.POST_INVALID_PAGINATION_CONDITION);
            }
        }
        return null;
    }
}
