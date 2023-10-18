package edonymyeon.backend.global.version;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiVersion {

    String from();
    String to() default Integer.MAX_VALUE + "." + Integer.MAX_VALUE;

    /**
     * from 버전 포함 to 버전 포함
     * 예를들어
     * from = "1.0"
     * to = "1.3"
     * 이면, from 버전부터, to 버전까지 지원한다! 1.4 부터 지원하지 않음
     */
}
