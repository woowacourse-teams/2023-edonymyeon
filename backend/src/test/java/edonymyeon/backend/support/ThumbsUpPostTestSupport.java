package edonymyeon.backend.support;

import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.thumbs.domain.Thumbs;
import edonymyeon.backend.thumbs.domain.ThumbsType;
import edonymyeon.backend.thumbs.repository.ThumbsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ThumbsUpPostTestSupport {

    private final ThumbsRepository thumbsRepository;

    private final PostTestSupport postTestSupport;

    private final MemberTestSupport memberTestSupport;

    public ThumbsBuilder builder() {
        return new ThumbsBuilder();
    }

    public final class ThumbsBuilder {

        private Post post;

        private Member member;

        private ThumbsType thumbsType;

        public ThumbsBuilder post(final Post post) {
            this.post = post;
            return this;
        }

        public ThumbsBuilder member(final Member member) {
            this.member = member;
            return this;
        }

        public ThumbsBuilder thumbsType(final ThumbsType thumbsType) {
            this.thumbsType = thumbsType;
            return this;
        }

        public Thumbs build() {
            return thumbsRepository.save(
                    new Thumbs(
                            post == null ? postTestSupport.builder().build() : post,
                            member == null ? memberTestSupport.builder().build() : member,
                            thumbsType == null ? ThumbsType.UP : thumbsType
                    )
            );
        }
    }

}
