package edonymyeon.backend.post.ui;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.post.application.PostService;
import edonymyeon.backend.post.application.dto.GeneralFindingCondition;
import edonymyeon.backend.post.application.dto.GeneralPostInfoResponse;
import edonymyeon.backend.post.application.dto.GeneralPostsResponse;
import edonymyeon.backend.post.application.dto.PostModificationRequest;
import edonymyeon.backend.post.application.dto.PostRequest;
import edonymyeon.backend.post.application.dto.PostResponse;
import edonymyeon.backend.post.application.dto.SortBy;
import edonymyeon.backend.post.application.dto.SortDirection;
import edonymyeon.backend.post.application.dto.SpecificPostInfoResponse;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/posts")
@RestController
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@AuthPrincipal MemberIdDto memberId,
                                                   @ModelAttribute PostRequest postRequest) {
        PostResponse response = postService.createPost(memberId, postRequest);
        return ResponseEntity.created(URI.create("/posts/" + response.id()))
                .body(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@AuthPrincipal MemberIdDto memberId, @PathVariable Long postId) {
        postService.deletePost(memberId, postId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(@AuthPrincipal MemberIdDto memberId,
                                                   @ModelAttribute PostModificationRequest postModificationRequest,
                                                   @PathVariable Long postId) {
        final PostResponse postResponse = postService.updatePost(memberId, postId, postModificationRequest);
        return ResponseEntity.ok().body(postResponse);
    }

    @GetMapping
    public ResponseEntity<GeneralPostsResponse> findAllPosts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection
    ) {
        final GeneralFindingCondition generalFindingCondition = GeneralFindingCondition.builder()
                .page(Objects.isNull(page) ? GeneralFindingCondition.DEFAULT_SIZE : page)
                .size(Objects.isNull(size) ? GeneralFindingCondition.DEFAULT_LIMIT : size)
                .sortBy(Objects.isNull(sortBy) ? GeneralFindingCondition.DEFAULT_SORT_BY : SortBy.valueOf(sortBy))
                .sortDirection(Objects.isNull(sortDirection) ? GeneralFindingCondition.DEFAULT_SORT_DIRECTION
                        : SortDirection.valueOf(sortDirection))
                .build();

        final List<GeneralPostInfoResponse> posts = postService.findAllPost(generalFindingCondition);
        return ResponseEntity
                .ok()
                .body(new GeneralPostsResponse(posts));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<SpecificPostInfoResponse> findSpecificPost(
            @AuthPrincipal(required = false) MemberIdDto memberIdDto, @PathVariable Long postId) {
        return ResponseEntity.ok()
                .body(postService.findSpecificPost(postId, memberIdDto));
    }
}
