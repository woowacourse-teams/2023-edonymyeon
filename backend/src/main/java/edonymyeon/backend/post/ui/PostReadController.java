package edonymyeon.backend.post.ui;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.post.application.PostReadService;
import edonymyeon.backend.post.application.dto.GeneralFindingCondition;
import edonymyeon.backend.post.application.dto.GeneralPostInfoResponse;
import edonymyeon.backend.post.application.dto.GeneralPostsResponse;
import edonymyeon.backend.post.application.dto.SpecificPostInfoResponse;
import edonymyeon.backend.post.ui.annotation.PostPaging;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostReadController {

    private final PostReadService postReadService;

    @GetMapping("/search")
    public ResponseEntity<GeneralPostsResponse> searchPosts(
            @RequestParam String query,
            @PostPaging GeneralFindingCondition generalFindingCondition
    ) {
        final GeneralPostsResponse posts = postReadService.searchPosts(query, generalFindingCondition);
        return ResponseEntity
                .ok()
                .body(posts);
    }

    @GetMapping("/posts")
    public ResponseEntity<GeneralPostsResponse> findAllPosts(@PostPaging GeneralFindingCondition generalFindingCondition) {
        final List<GeneralPostInfoResponse> posts = postReadService.findPostsByPagingCondition(generalFindingCondition);
        return ResponseEntity
                .ok()
                .body(new GeneralPostsResponse(posts));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<SpecificPostInfoResponse> findSpecificPost(
            @AuthPrincipal(required = false) MemberIdDto memberIdDto, @PathVariable Long postId) {
        return ResponseEntity.ok()
                .body(postReadService.findSpecificPost(postId, memberIdDto));
    }
}
