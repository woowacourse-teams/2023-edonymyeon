package edonymyeon.backend.comment.application;

import static org.assertj.core.api.Assertions.assertThat;

import edonymyeon.backend.content.comment.application.CommentService;
import edonymyeon.backend.content.comment.domain.Comment;
import edonymyeon.backend.content.comment.repository.CommentRepository;
import edonymyeon.backend.membber.member.application.dto.ActiveMemberId;
import edonymyeon.backend.membber.member.domain.Member;
import edonymyeon.backend.content.post.domain.Post;
import edonymyeon.backend.support.IntegrationFixture;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
public class CommentServiceTest extends IntegrationFixture {

    private final CommentService commentService;

    @Test
    void 댓글이_삭제되면_조회되지_않는다(@Autowired CommentRepository commentRepository) {
        final Post 게시글 = postTestSupport.builder().build();
        final Member 사용자 = memberTestSupport.builder().build();
        final Comment 댓글 = commentTestSupport.builder().post(게시글).member(사용자).build();

        commentService.deleteComment(new ActiveMemberId(사용자.getId()), 게시글.getId(), 댓글.getId());
        final Optional<Comment> 조회_결과 = commentRepository.findById(댓글.getId());

        assertThat(조회_결과).isEmpty();
    }
}
