package edonymyeon.backend.thumbs.ui;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.thumbs.application.ThumbsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ThumbsController {

    private final ThumbsService thumbsService;

    @PutMapping("posts/{postId}/up")
    public ResponseEntity<Void> thumbsUp(@AuthPrincipal final MemberIdDto memberId,
                                         @PathVariable final Long postId){
        thumbsService.thumbsUp(memberId, postId);
        return ResponseEntity.ok()
                .build();
    }
}
