package edonymyeon.backend.post.ui;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.global.version.ApiVersion;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.post.application.GeneralFindingCondition;
import edonymyeon.backend.post.application.MyPostService;
import edonymyeon.backend.post.application.PostSlice;
import edonymyeon.backend.post.application.dto.response.MyPostResponse;
import edonymyeon.backend.post.ui.annotation.PostPaging;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/profile")
@RestController
public class MyPostController {

    private final MyPostService myPostService;

    @ApiVersion(value = {1})
    @GetMapping("/my-posts")
    public ResponseEntity<PostSlice<MyPostResponse>> findMyPosts(@AuthPrincipal final MemberId memberId,
                                                             @PostPaging GeneralFindingCondition generalFindingCondition) {
        PostSlice<MyPostResponse> response = myPostService.findMyPosts(memberId, generalFindingCondition);
        return ResponseEntity.ok(response);
    }
}
