package edonymyeon.backend.post.ui;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.post.application.PostService;
import edonymyeon.backend.post.application.dto.PostFindingCondition;
import edonymyeon.backend.post.application.dto.PostFindingResponse;
import edonymyeon.backend.post.application.dto.PostRequest;
import edonymyeon.backend.post.application.dto.PostResponse;
import edonymyeon.backend.post.application.dto.SortBy;
import edonymyeon.backend.post.application.dto.SortDirection;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping
    public ResponseEntity<List<PostFindingResponse>> findAllPosts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection
    ) {
        final PostFindingCondition postFindingCondition = PostFindingCondition.builder()
                .page(Objects.isNull(page) ? PostFindingCondition.DEFAULT_SIZE : page)
                .size(Objects.isNull(size) ? PostFindingCondition.DEFAULT_LIMIT : size)
                .sortBy(Objects.isNull(sortBy) ? PostFindingCondition.DEFAULT_SORT_BY : SortBy.valueOf(sortBy))
                .sortDirection(Objects.isNull(sortDirection) ? PostFindingCondition.DEFAULT_SORT_DIRECTION : SortDirection.valueOf(sortDirection))
                .build();
        return ResponseEntity
                .ok()
                .body(postService.findAllPost(postFindingCondition));
    }
}
