package edonymyeon.backend.thumbs.application;

import edonymyeon.backend.thumbs.domain.Thumbs;
import edonymyeon.backend.thumbs.domain.ThumbsType;
import java.util.List;

public record AllThumbsInPost (List<Thumbs> allThumbs) {

    public int getUpCount(){
        long count = allThumbs.stream()
                .filter(thumbs -> thumbs.getThumbsType() == ThumbsType.UP)
                .count();
        return (int) count;
    }

    public int getDownCount(){
        return allThumbs.size() - getUpCount();
    }
}
