package edonymyeon.backend.post.domain;

import edonymyeon.backend.global.domain.TemporalRecord;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.image.postimage.domain.PostImageInfo;
import edonymyeon.backend.image.postimage.domain.PostImageInfos;
import edonymyeon.backend.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Where;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static edonymyeon.backend.global.exception.ExceptionInformation.*;

@Getter
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicInsert
@Table(indexes = @Index(name = "i_post_created_at", columnList = "createdAt"))
@Where(clause = "deleted = false")
@Entity
public class Post extends TemporalRecord {

    public static final int DEFAULT_BATCH_SIZE = 20;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    private PostImageInfos postImageInfos;

    @ColumnDefault("0")
    private int viewCount;

    @Formula("(select count(c.id) from comment c where c.post_id = id and c.deleted = false)")
    private int commentCount;

    @ColumnDefault("false")
    private boolean deleted;

    public Post(
            final Long id,
            final String title,
            final String content,
            final Long price,
            final Member member
    ) {
        this(title, content, price, member);
        this.id = id;
    }

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
        this.postImageInfos = PostImageInfos.create();
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
        if (Objects.isNull(content) || content.length() > MAX_CONTENT_LENGTH) {
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

    public void validateImageCount(final Integer imageCount) {
        this.postImageInfos.validateImageCount(imageCount);
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

    /**
     * 게시글 수정시 사용, 새로 추가되는 이미지가 없고 기존 이미지에 대한 수정만 일어나는 경우
     */
    public List<Long> getImageIdsToDeleteBy(final List<String> remainedImageNames) {
        return this.postImageInfos.getImageIdsToDeleteBy(remainedImageNames, Collections.emptyList());
    }

    /**
     * 게시글 수정시 사용, 새로 추가되는 이미지도 있는 경우
     */
    public List<Long> getImageIdsToDeleteBy(final List<String> remainedImageNames, final PostImageInfos imagesToAdd) {
        return this.postImageInfos.getImageIdsToDeleteBy(remainedImageNames, imagesToAdd.getPostImageInfos());
    }

    public boolean isSameMember(final Member member) {
        return this.member.equals(member);
    }

    public boolean isSameMember(final Long memberId) {
        return this.member.hasId(memberId);
    }

    public Member getMember() {
        return this.member;
    }

    public Long getWriterId() {
        return this.member.getId();
    }

    public boolean hasThumbnail() {
        return !this.postImageInfos.isEmpty();
    }

    public String getThumbnailName() {
        return this.postImageInfos.getThumbnailName();
    }

    public List<PostImageInfo> getPostImageInfos() {
        return this.postImageInfos.getPostImageInfos();
    }

    public void validateWriter(final Long memberId) {
        if (!isSameMember(memberId)) {
            throw new EdonymyeonException(POST_MEMBER_NOT_SAME);
        }
    }

    public void updateView(final Member member) {
        if (this.member.equals(member)) {
            return;
        }
        this.viewCount++;
    }

    public void delete() {
        this.deleted = true;
    }

    public Optional<String> getDeviceTokenFromWriter() {
        return Optional.ofNullable(this.member.getActiveDeviceToken());
    }
}
