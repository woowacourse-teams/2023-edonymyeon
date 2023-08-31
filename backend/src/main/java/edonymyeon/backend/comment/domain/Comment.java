package edonymyeon.backend.comment.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.COMMENT_MEMBER_NOT_SAME;

import edonymyeon.backend.global.domain.TemporalRecord;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.image.commentimage.domain.CommentImageInfo;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.domain.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

@Getter
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted = false")
@Entity
public class Comment extends TemporalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Post post;

    @Column(nullable = false)
    private String comment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private CommentImageInfo commentImageInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    @ColumnDefault("false")
    private boolean deleted;

    public Comment(
            final Post post,
            final String comment,
            final CommentImageInfo commentImageInfo,
            final Member member
    ) {
        this.post = post;
        this.comment = comment;
        this.commentImageInfo = commentImageInfo;
        this.member = member;
    }

    public void delete() {
        this.deleted = true;
        // todo: 댓글 사진이 신고 당했을 경우를 대비해서 댓글 사진도 soft delete로 진행
        if(this.commentImageInfo != null) {
            this.commentImageInfo.delete();
        }
    }

    public void checkWriter(final Long memberId) {
        // todo: 여우의 PR 반영해서 member 비교하는거 고치기
        if (!member.getId().equals(memberId)) {
            throw new EdonymyeonException(COMMENT_MEMBER_NOT_SAME);
        }
    }
}
