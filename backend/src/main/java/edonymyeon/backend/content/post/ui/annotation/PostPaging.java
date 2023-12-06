package edonymyeon.backend.content.post.ui.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 보통의 게시글 목록을 조회하는 정책
 * page, size, sort 기준, sort 순서 조건을 입력 받는다
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostPaging {

}
