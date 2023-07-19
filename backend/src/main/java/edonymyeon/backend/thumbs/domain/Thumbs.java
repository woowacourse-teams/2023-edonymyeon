package edonymyeon.backend.thumbs.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.THUMBS_UP_ALREADY_EXIST;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.domain.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Thumbs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member member;

    @Enumerated(value = EnumType.STRING)
    private ThumbsType thumbsType;

    public Thumbs(final Post post, final Member member, final ThumbsType thumbsType) {
        this.post = post;
        this.member = member;
        this.thumbsType = thumbsType;
    }

    private boolean isUp() {
        return thumbsType == ThumbsType.UP;
    }

    private void updateThumbsType() {
        this.thumbsType = thumbsType.getReverseType();
    }

    public void up() {
        if(isUp()){
            throw new EdonymyeonException(THUMBS_UP_ALREADY_EXIST);
        }
        updateThumbsType();
    }
}
