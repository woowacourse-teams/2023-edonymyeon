package edonymyeon.backend.auth.ui.argumentresolver;

import static edonymyeon.backend.global.exception.ExceptionInformation.AUTHORIZATION_EMPTY;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.auth.application.AuthService;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.application.dto.AnonymousMemberId;
import edonymyeon.backend.member.application.dto.MemberId;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthService authService;

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
        String authorization = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null) {
            if (!Objects.requireNonNull(parameter.getParameterAnnotation(AuthPrincipal.class)).required()) {
                return new AnonymousMemberId();
            }
            throw new EdonymyeonException(AUTHORIZATION_EMPTY);
        }

        final String email = parseToEmail(authorization);

        if (Objects.isNull(email)) {
            throw new EdonymyeonException(AUTHORIZATION_EMPTY);
        }

        return authService.getAuthenticatedUser(email);
    }

    private String parseToEmail(final String authorization) {
        String[] authHeader = authorization.split(" ");
        if (!authHeader[0].equalsIgnoreCase("basic")) {
            throw new EdonymyeonException(AUTHORIZATION_EMPTY);
        }

        String decodedString = new String(Base64.decodeBase64(authHeader[1]));

        String[] credentials = decodedString.split(":");
        return credentials[0];
    }
}
