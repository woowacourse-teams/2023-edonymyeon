package edonymyeon.backend.content.post.ui;

import edonymyeon.backend.content.post.application.GeneralFindingCondition;
import edonymyeon.backend.content.post.application.MyPostService;
import edonymyeon.backend.content.post.application.PostSlice;
import edonymyeon.backend.content.post.application.dto.response.MyPostResponse;
import edonymyeon.backend.membber.auth.annotation.AuthPrincipal;
import edonymyeon.backend.global.version.ApiVersion;
import edonymyeon.backend.membber.member.application.dto.MemberId;
import edonymyeon.backend.content.post.ui.annotation.PostPaging;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/profile")
@RestController
public class MyPostController {

    private final MyPostService myPostService;

    @ApiVersion(from = "1.0")
    @GetMapping("/my-posts")
    public ResponseEntity<PostSlice<MyPostResponse>> findMyPosts(@AuthPrincipal final MemberId memberId,
                                                                 @PostPaging GeneralFindingCondition generalFindingCondition) {
        PostSlice<MyPostResponse> response = myPostService.findMyPosts(memberId, generalFindingCondition);
        return ResponseEntity.ok(response);
    }
}
