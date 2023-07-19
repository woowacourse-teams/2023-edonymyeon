package edonymyeon.backend.auth.ui.argumentresolver;

import static edonymyeon.backend.global.exception.ExceptionInformation.AUTHORIZATION_EMPTY;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.auth.application.AuthService;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.application.dto.MemberIdDto;
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

    public static final long NON_EXISTING_MEMBER_ID = 0L;
    private final AuthService authService;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().equals(MemberIdDto.class)
                && parameter.hasParameterAnnotation(AuthPrincipal.class);
    }

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    )
            throws Exception {
        String authorization = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null) {
            if (!Objects.requireNonNull(parameter.getParameterAnnotation(AuthPrincipal.class)).required()) {
                return new MemberIdDto(NON_EXISTING_MEMBER_ID);
            }
            throw new EdonymyeonException(AUTHORIZATION_EMPTY);
        }

        String[] authHeader = authorization.split(" ");
        if (!authHeader[0].equalsIgnoreCase("basic")) {
            throw new EdonymyeonException(AUTHORIZATION_EMPTY);
        }

        byte[] decodedBytes = Base64.decodeBase64(authHeader[1]);
        String decodedString = new String(decodedBytes);

        String[] credentials = decodedString.split(":");
        String email = credentials[0];
        String password = credentials[1];

        if (Objects.isNull(email) || Objects.isNull(password)) {
            throw new EdonymyeonException(AUTHORIZATION_EMPTY);
        }

        return authService.findMember(email, password);
    }
}
