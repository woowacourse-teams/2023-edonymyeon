package edonymyeon.backend.thumbs.application.event;

import edonymyeon.backend.post.domain.Post;

public record ThumbsDownEvent(Post post) {
}
