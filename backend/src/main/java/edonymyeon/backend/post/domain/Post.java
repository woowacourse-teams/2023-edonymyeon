package edonymyeon.backend.post.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.POST_CONTENT_ILLEGAL_LENGTH;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_IMAGE_NUMBER_INVALID;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_MEMBER_EMPTY;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_PRICE_ILLEGAL_SIZE;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_TITLE_ILLEGAL_LENGTH;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.image.postimage.domain.PostImageInfo;
import edonymyeon.backend.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    public static final int MAX_IMAGE_NUMBER = 10;
    public static final int MIN_IMAGE_NUMBER = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "longtext")
    private String content;

    @Column(nullable = false)
    private Long price;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Member member;

    // TODO: cascade
    @OneToMany(mappedBy = "post")
    private List<PostImageInfo> postImageInfos;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // todo: 테스트 코드에서 자꾸 null 값으로 조회되서 일단 하드코딩
    @ColumnDefault("0")
    private Long viewCount = 0L;

    public Post(
            final String title,
            final String content,
            final Long price,
            final Member member
    ) {
        validate(title, content, price, member);
        this.title = title;
        this.content = content;
        this.price = price;
        this.member = member;
        this.postImageInfos = new ArrayList<>();
    }

    private void validate(
            final String title,
            final String content,
            final Long price,
            final Member member
    ) {
        validateTitle(title);
        validateContent(content);
        validatePrice(price);
        validateMember(member);
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

    private void validateMember(final Member member) {
        if (Objects.isNull(member)) {
            throw new EdonymyeonException(POST_MEMBER_EMPTY);
        }
    }

    public void addPostImageInfo(final PostImageInfo postImageInfo) {
        if (this.postImageInfos.contains(postImageInfo)) {
            return;
        }
        checkImageNumber(this.postImageInfos);
        this.postImageInfos.add(postImageInfo);
    }

    private void checkImageNumber(final List<PostImageInfo> postImageInfos) {
        if (!isValidImageNumber(postImageInfos.size())) {
            throw new EdonymyeonException(POST_IMAGE_NUMBER_INVALID);
        }
    }

    public boolean isValidImageNumber(final Integer imageNumber) {
        return imageNumber >= MIN_IMAGE_NUMBER && imageNumber < MAX_IMAGE_NUMBER;
    }

    public void update(final String title, final String content, final Long price) {
        updateTitle(title);
        updateContent(content);
        updatePrice(price);
    }

    private void updateTitle(final String title) {
        validateTitle(title);
        this.title = title;
    }

    private void updateContent(final String content) {
        validateContent(content);
        this.content = content;
    }

    private void updatePrice(final Long price) {
        validatePrice(price);
        this.price = price;
    }

    public void updateImages(final List<PostImageInfo> postImageInfos) {
        checkImageNumber(postImageInfos);
        this.postImageInfos.clear();
        this.postImageInfos.addAll(postImageInfos);
    }

    public boolean isSameMember(final Member member) {
        return this.member.equals(member);
    }
}
