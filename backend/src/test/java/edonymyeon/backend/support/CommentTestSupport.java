package edonymyeon.backend.support;

import edonymyeon.backend.comment.domain.Comment;
import edonymyeon.backend.comment.repository.CommentRepository;
import edonymyeon.backend.image.commentimage.domain.CommentImageInfo;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentTestSupport {

    private final PostTestSupport postTestSupport;

    private final MemberTestSupport memberTestSupport;

    private final CommentRepository commentRepository;

    public CommentTestSupport.CommentBuilder builder() {
        return new CommentTestSupport.CommentBuilder();
    }

    public final class CommentBuilder {

        private Post post;

        private String content;

        private CommentImageInfo commentImageInfo;

        private Member member;

        public CommentTestSupport.CommentBuilder post(final Post post) {
            this.post = post;
            return this;
        }

        public CommentTestSupport.CommentBuilder content(final String content) {
            this.content = content;
            return this;
        }

        public CommentTestSupport.CommentBuilder commentImageInfo(final CommentImageInfo commentImageInfo) {
            this.commentImageInfo = commentImageInfo;
            return this;
        }

        public CommentTestSupport.CommentBuilder member(final Member member) {
            this.member = member;
            return this;
        }

        public Comment build() {
            return commentRepository.save(
                    new Comment(
                            post == null ? postTestSupport.builder().build() : post,
                            content == null ? "댓글이지롱" : content,
                            commentImageInfo == null ? null : commentImageInfo,
                            member == null ? memberTestSupport.builder().build() : member
                    )
            );
        }
    }
}
