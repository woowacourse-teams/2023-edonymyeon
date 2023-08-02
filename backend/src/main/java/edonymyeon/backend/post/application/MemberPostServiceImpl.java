package edonymyeon.backend.post.application;

import edonymyeon.backend.member.application.MemberPostService;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberPostServiceImpl implements MemberPostService {

    private final PostRepository postRepository;

    @Override
    public Slice<Post> findAllByMemberId(final Long memberId, final Pageable pageable) {
        return postRepository.findAllByMemberId(memberId, pageable);
    }
}
