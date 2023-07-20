package edonymyeon.backend.thumbs.domain;

import static edonymyeon.backend.thumbs.domain.ThumbsType.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.domain.Post;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class AllThumbsInPostTest {

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
        Member member = new Member("email", "password", "nick", null);
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