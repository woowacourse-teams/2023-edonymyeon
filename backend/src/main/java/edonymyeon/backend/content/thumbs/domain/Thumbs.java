package edonymyeon.backend.content.thumbs.domain;

import edonymyeon.backend.global.domain.TemporalRecord;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.profile.domain.Member;
import edonymyeon.backend.content.post.domain.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static edonymyeon.backend.global.exception.ExceptionInformation.THUMBS_DOWN_ALREADY_EXIST;
import static edonymyeon.backend.global.exception.ExceptionInformation.THUMBS_UP_ALREADY_EXIST;


@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(
        name = "thumbs",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_thumbs_post_id_member_id",
                        columnNames = {"post_id", "member_id"}
                ),
        })
@Entity
public class Thumbs extends TemporalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member member;

    @Enumerated(value = EnumType.STRING)
    private ThumbsType thumbsType;

    public Thumbs(
            final Post post,
            final Member member,
            final ThumbsType thumbsType
    ) {
        this.post = post;
        this.member = member;
        this.thumbsType = thumbsType;
    }

    public boolean isUp() {
        return thumbsType == ThumbsType.UP;
    }

    public boolean isDown() {
        return thumbsType == ThumbsType.DOWN;
    }

    private void updateThumbsType() {
        this.thumbsType = thumbsType.getReverseType();
    }

    public void up() {
        if (isUp()) {
            throw new EdonymyeonException(THUMBS_UP_ALREADY_EXIST);
        }
        updateThumbsType();
    }

    public void down() {
        if (isDown()) {
            throw new EdonymyeonException(THUMBS_DOWN_ALREADY_EXIST);
        }
        updateThumbsType();
    }
}
