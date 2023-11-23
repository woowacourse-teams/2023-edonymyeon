package edonymyeon.backend.support;

import edonymyeon.backend.content.post.postimage.domain.PostImageInfo;
import edonymyeon.backend.content.post.postimage.repository.PostImageInfoRepository;
import edonymyeon.backend.content.post.domain.Post;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostImageInfoTestSupport {

    private final PostImageInfoRepository postImageInfoRepository;

    private final PostTestSupport postTestSupport;

    public PostImageInfoBuilder builder() {
        return new PostImageInfoBuilder();
    }

    public final class PostImageInfoBuilder {

        private String storeName;

        private Post post;

        public PostImageInfoBuilder storeName(final String storeName) {
            this.storeName = storeName;
            return this;
        }

        public PostImageInfoBuilder post(final Post post) {
            this.post = post;
            return this;
        }

        public PostImageInfo build() {
            return postImageInfoRepository.save(
                    new PostImageInfo(
                            storeName == null ? UUID.randomUUID().toString() : storeName,
                            post == null ? postTestSupport.builder().build() : post
                    )
            );
        }
    }
}
