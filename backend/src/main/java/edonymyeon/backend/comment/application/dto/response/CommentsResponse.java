package edonymyeon.backend.comment.application.dto.response;

import java.util.List;

public record CommentsResponse(int commentCount, List<CommentDto> comments) {

}
