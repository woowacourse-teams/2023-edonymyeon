package edonymyeon.backend.support;

import edonymyeon.backend.content.comment.domain.Comment;
import edonymyeon.backend.content.comment.repository.CommentRepository;
import edonymyeon.backend.content.comment.commentimage.domain.CommentImageInfo;
import edonymyeon.backend.content.comment.commentimage.repository.CommentImageInfoRepository;
import edonymyeon.backend.image.domain.ImageInfo;
import edonymyeon.backend.member.profile.domain.Member;
import edonymyeon.backend.content.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentTestSupport {

    private final PostTestSupport postTestSupport;

    private final TestMemberBuilder memberTestSupport;

    private final CommentRepository commentRepository;

    private final CommentImageInfoRepository commentImageInfoRepository;

    public CommentTestSupport.CommentBuilder builder() {
        return new CommentTestSupport.CommentBuilder();
    }

    public final class CommentBuilder {

        private Post post;

        private String content;

        private String imageStoreName;

        private Member member;

        public CommentTestSupport.CommentBuilder post(final Post post) {
            this.post = post;
            return this;
        }

        public CommentTestSupport.CommentBuilder content(final String content) {
            this.content = content;
            return this;
        }

        public CommentTestSupport.CommentBuilder imageStoreName(final String imageStoreName) {
            this.imageStoreName = imageStoreName;
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
                            imageStoreName == null ? null : commentImageInfoRepository.save(
                                    CommentImageInfo.from(new ImageInfo(imageStoreName))),
                            member == null ? memberTestSupport.builder().build() : member
                    )
            );
        }
    }
}
