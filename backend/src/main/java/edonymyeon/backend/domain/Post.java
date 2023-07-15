package edonymyeon.backend.domain;

import static edonymyeon.backend.domain.exception.ExceptionCode.POST_CONTENT_ILLEGAL_LENGTH;
import static edonymyeon.backend.domain.exception.ExceptionCode.POST_PRICE_ILLEGAL_SIZE;
import static edonymyeon.backend.domain.exception.ExceptionCode.POST_TITLE_ILLEGAL_LENGTH;

import edonymyeon.backend.domain.exception.EdonymyeonException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Post {

    private static final int MAX_TITLE_LENGTH = 30;
    private static final int MAX_CONTENT_LENGTH = 1_000;
    private static final long MAX_PRICE = 10_000_000_000L;
    private static final int MIN_PRICE = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "longtext")
    private String content;

    @Column(nullable = false)
    private Long price;

    // TODO: 작성자
    // TODO: 사진들

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createAt;

    @ColumnDefault("0")
    private Long viewCount;

    private Post(
            final Long id,
            final String title,
            final String content,
            final Long price,
            final LocalDateTime createAt,
            final Long viewCount
    ) {
        validate(title, content, price);
        this.id = id;
        this.title = title;
        this.content = content;
        this.price = price;
        this.createAt = createAt;
        this.viewCount = viewCount;
    }

    public Post(
            final String title,
            final String content,
            final Long price
    ) {
        this(null, title, content, price, null, null);
    }

    private void validate(
            final String title,
            final String content,
            final Long price
    ) {
        validateTitle(title);
        validateContent(content);
        validatePrice(price);
    }

    private void validateTitle(final String title) {
        if (title.isBlank() || title.length() > MAX_TITLE_LENGTH) {
            throw new EdonymyeonException(POST_TITLE_ILLEGAL_LENGTH);
        }
    }

    private void validateContent(final String content) {
        if (content.isBlank() || content.length() > MAX_CONTENT_LENGTH) {
            throw new EdonymyeonException(POST_CONTENT_ILLEGAL_LENGTH);
        }
    }

    private void validatePrice(final Long price) {
        if (Objects.isNull(price) || price < MIN_PRICE || price > MAX_PRICE) {
            throw new EdonymyeonException(POST_PRICE_ILLEGAL_SIZE);
        }
    }
}
