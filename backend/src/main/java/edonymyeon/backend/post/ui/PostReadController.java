package edonymyeon.backend.post.ui;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.post.application.PostReadService;
import edonymyeon.backend.post.application.dto.GeneralFindingCondition;
import edonymyeon.backend.post.application.dto.GeneralPostInfoResponse;
import edonymyeon.backend.post.application.dto.SpecificPostInfoResponse;
import edonymyeon.backend.post.ui.annotation.PostPaging;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PostReadController {

    private final PostReadService postReadService;

    @GetMapping("/posts")
    public ResponseEntity<Slice<GeneralPostInfoResponse>> findAllPosts(@PostPaging GeneralFindingCondition generalFindingCondition) {
        Slice<GeneralPostInfoResponse> posts = postReadService.findPostsByPagingCondition(generalFindingCondition);
        return ResponseEntity.ok()
                .body(posts);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<SpecificPostInfoResponse> findSpecificPost(
            @AuthPrincipal(required = false) MemberId memberId, @PathVariable Long postId) {
        SpecificPostInfoResponse specificPost = postReadService.findSpecificPost(postId, memberId);
        return ResponseEntity.ok()
                .body(specificPost);
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<GeneralPostInfoResponse>> searchPosts(
            @RequestParam String query,
            @PostPaging GeneralFindingCondition generalFindingCondition
    ) {
        Slice<GeneralPostInfoResponse> posts = postReadService.searchPosts(query, generalFindingCondition);
        return ResponseEntity.ok()
                .body(posts);
    }
}
