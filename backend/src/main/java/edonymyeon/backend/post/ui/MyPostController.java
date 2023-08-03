package edonymyeon.backend.post.ui;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.post.application.MyPostService;
import edonymyeon.backend.post.application.dto.GeneralFindingCondition;
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

    @GetMapping("/my-posts")
    public ResponseEntity<Slice<MyPostResponse>> findMyPosts(@AuthPrincipal final MemberIdDto memberIdDto,
                                                             @PostPaging GeneralFindingCondition generalFindingCondition) {
        Slice<MyPostResponse> response = myPostService.findMyPosts(memberIdDto, generalFindingCondition);
        return ResponseEntity.ok(response);
    }
}
