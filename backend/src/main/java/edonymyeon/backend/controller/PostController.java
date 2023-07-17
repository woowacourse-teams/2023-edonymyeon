package edonymyeon.backend.controller;

import edonymyeon.backend.service.PostService;
import edonymyeon.backend.service.request.PostRequest;
import edonymyeon.backend.service.response.MemberIdDto;
import edonymyeon.backend.service.response.PostResponse;
import edonymyeon.backend.ui.annotation.AuthenticationPrincipal;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/posts")
@RestController
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@AuthenticationPrincipal MemberIdDto memberId,
                                                   @ModelAttribute PostRequest postRequest) {
        PostResponse response = postService.createPost(postRequest);
        return ResponseEntity.created(URI.create("/posts/" + response.id()))
                .body(response);
    }
}
