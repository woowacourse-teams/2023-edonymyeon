package edonymyeon.backend.member.application;

import edonymyeon.backend.post.domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MemberPostService {

    Slice<Post> findAllByMemberId(Long memberId, Pageable pageable);
}
