package edonymyeon.backend.service;

import edonymyeon.backend.domain.Post;
import edonymyeon.backend.repository.PostRepository;
import edonymyeon.backend.service.request.PostRequest;
import edonymyeon.backend.service.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public PostResponse createPost(final PostRequest postRequest) {
        final Post post = new Post(
                postRequest.getTitle(),
                postRequest.getContent(),
                postRequest.getPrice()
        );

        postRepository.save(post);
        return new PostResponse(post.getId());
    }
}
