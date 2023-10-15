package edonymyeon.backend.global.version;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

@RequiredArgsConstructor
public class ApiVersionRequestCondition implements RequestCondition<ApiVersionRequestCondition> {

    private static final String VERSION_HEADER = "X-API-VERSION";

    private final int[] versions;

    @Override
    public ApiVersionRequestCondition getMatchingCondition(HttpServletRequest request) {
        final String header = request.getHeader(VERSION_HEADER);
        if(header == null) {
            return null;
        }

        final boolean isExist = Arrays.stream(versions)
                .anyMatch(version -> version == Integer.parseInt(header));
        if(isExist) {
            return this;
        }
        return null;
    }

    @Override
    public ApiVersionRequestCondition combine(ApiVersionRequestCondition other) {
        throw new IllegalStateException("Not Supported");
    }

    @Override
    public int compareTo(ApiVersionRequestCondition other, HttpServletRequest request) {
        throw new IllegalStateException("Not Supported");
    }
}
