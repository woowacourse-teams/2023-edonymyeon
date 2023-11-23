package edonymyeon.backend.support;

import edonymyeon.backend.membber.member.domain.Member;
import edonymyeon.backend.content.post.domain.Post;
import edonymyeon.backend.content.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostTestSupport {

    private static final String DEFAULT_TITLE = "title";

    private static final String DEFAULT_CONTENT = "content";

    private static final int DEFAULT_PRICE = 1_000;

    private final PostRepository postRepository;

    private final TestMemberBuilder memberTestSupport;

    public PostBuilder builder() {
        return new PostBuilder();
    }

    public final class PostBuilder {

        private Long id;

        private String title;

        private String content;

        private Long price;

        private Member member;

        public PostBuilder title(final String title) {
            this.title = title;
            return this;
        }

        public PostBuilder content(final String content) {
            this.content = content;
            return this;
        }

        public PostBuilder price(final Long price) {
            this.price = price;
            return this;
        }

        public PostBuilder member(final Member member) {
            this.member = member;
            return this;
        }

        public Post build() {
            return postRepository.save(
                    new Post(
                            title == null ? DEFAULT_TITLE : title,
                            content == null ? DEFAULT_CONTENT : content,
                            price == null ? DEFAULT_PRICE : price,
                            member == null ? memberTestSupport.builder().build() : member
                    )
            );
        }
    }
}
