package edonymyeon.backend.ui.argumentresolver;

import static edonymyeon.backend.domain.exception.ExceptionInformation.AUTHORIZATION_EMPTY;

import edonymyeon.backend.domain.exception.EdonymyeonException;
import edonymyeon.backend.service.AuthenticationService;
import edonymyeon.backend.service.response.MemberIdDto;
import edonymyeon.backend.ui.annotation.AuthenticationPrincipal;
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
public class MemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthenticationService authenticationService;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().equals(MemberIdDto.class)
                && parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory)
            throws Exception {
        String authorization = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null) {
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

        return authenticationService.findMember(email, password);
    }
}
