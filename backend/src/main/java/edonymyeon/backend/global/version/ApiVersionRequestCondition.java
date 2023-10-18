package edonymyeon.backend.global.version;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

public class ApiVersionRequestCondition implements RequestCondition<ApiVersionRequestCondition> {

    private static final String VERSION_HEADER = "X-API-VERSION";

    private final BigDecimal versionFrom;

    private final BigDecimal versionTo;

    public ApiVersionRequestCondition(final String versionFrom, final String versionTo) {
        this.versionFrom = new BigDecimal(versionFrom);
        this.versionTo = new BigDecimal(versionTo);
    }

    @Override
    public ApiVersionRequestCondition getMatchingCondition(HttpServletRequest request) {
        final String header = request.getHeader(VERSION_HEADER);
        if(header == null) {
            return null;
        }

        final BigDecimal headerVersion = new BigDecimal(extractVersion(header));
        final int from = versionFrom.compareTo(headerVersion);
        final int to = versionTo.compareTo(headerVersion);
        if (from <= 0 && to >= 0) {
            return this;
        }
        return null;
    }

    @NotNull
    private String extractVersion(final String header) {
        if(!header.contains(".")) {
            return header + ".0";
        }
        final String[] split = header.split("\\.");
        return split[0] + "." + split[1];
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
