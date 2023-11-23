package edonymyeon.backend.thumbs.domain;

import edonymyeon.backend.content.thumbs.domain.AllThumbsInPost;
import edonymyeon.backend.content.thumbs.domain.Thumbs;
import edonymyeon.backend.membber.member.domain.Member;
import edonymyeon.backend.content.post.domain.Post;
import edonymyeon.backend.support.TestMemberBuilder;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static edonymyeon.backend.content.thumbs.domain.ThumbsType.DOWN;
import static edonymyeon.backend.content.thumbs.domain.ThumbsType.UP;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
class AllThumbsInPostTest {

    private final TestMemberBuilder testMemberBuilder = new TestMemberBuilder(null);

    @Test
    void 추천과_비추천이_모두_0이다() {
        AllThumbsInPost allThumbsInPost = AllThumbsInPost.from(Collections.emptyList());

        assertSoftly(softly -> {
                    softly.assertThat(allThumbsInPost.getUpCount()).isEqualTo(0);
                    softly.assertThat(allThumbsInPost.getDownCount()).isEqualTo(0);
                }
        );
    }

    @Test
    void 추천과_비추천이_수를_제대로_카운트_한다() {
        Member member = testMemberBuilder.builder()
                .buildWithoutSaving();

        Post post = new Post("title", "content", 100L, member);

        List<Thumbs> thumbs = List.of(
                new Thumbs(post, member, UP),
                new Thumbs(post, member, UP),
                new Thumbs(post, member, UP),
                new Thumbs(post, member, UP),
                new Thumbs(post, member, UP),
                new Thumbs(post, member, DOWN),
                new Thumbs(post, member, DOWN)
        );

        AllThumbsInPost allThumbsInPost = AllThumbsInPost.from(thumbs);

        assertSoftly(softly -> {
                    softly.assertThat(allThumbsInPost.getUpCount()).isEqualTo(5);
                    softly.assertThat(allThumbsInPost.getDownCount()).isEqualTo(2);
                }
        );
    }
}
