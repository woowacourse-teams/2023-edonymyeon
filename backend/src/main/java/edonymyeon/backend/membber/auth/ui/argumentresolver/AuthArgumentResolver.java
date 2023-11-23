package edonymyeon.backend.membber.auth.ui.argumentresolver;

import static edonymyeon.backend.membber.auth.ui.SessionConst.USER;
import static edonymyeon.backend.global.exception.ExceptionInformation.AUTHORIZATION_EMPTY;

import edonymyeon.backend.membber.auth.annotation.AuthPrincipal;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.membber.member.application.dto.ActiveMemberId;
import edonymyeon.backend.membber.member.application.dto.AnonymousMemberId;
import edonymyeon.backend.membber.member.application.dto.MemberId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().equals(MemberId.class)
                && parameter.hasParameterAnnotation(AuthPrincipal.class);
    }

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) {
        final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        final HttpSession session = request.getSession();
        final Long userId = getUserId(session);

        if (Objects.isNull(userId)) {
            return getAnonymousMemberId(parameter);
        }

        session.setMaxInactiveInterval(USER.getValidatedTime());
        return new ActiveMemberId(userId);
    }

    @NotNull
    private AnonymousMemberId getAnonymousMemberId(final MethodParameter parameter) {
        if (authPrincipalFrom(parameter).required()) {
            throw new EdonymyeonException(AUTHORIZATION_EMPTY);
        }
        return new AnonymousMemberId();
    }

    private AuthPrincipal authPrincipalFrom(final MethodParameter parameter) {
        return Objects.requireNonNull(parameter.getParameterAnnotation(AuthPrincipal.class));
    }

    private Long getUserId(final HttpSession session) {
        try {
            return (Long) session.getAttribute(USER.getSessionId());
        } catch (IllegalStateException e) {
            throw new EdonymyeonException(AUTHORIZATION_EMPTY);
        }
    }
}
