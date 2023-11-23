package edonymyeon.backend.content.thumbs.application.event;

import edonymyeon.backend.content.post.domain.Post;

public record ThumbsDownEvent(Post post) {
}
