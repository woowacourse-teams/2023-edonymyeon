package edonymyeon.backend.content.comment.application.dto.response;

import java.util.List;

public record CommentsResponse(int commentCount, List<CommentDto> comments) {

}
