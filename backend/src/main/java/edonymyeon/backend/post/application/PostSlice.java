package edonymyeon.backend.post.application;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Slice;

@Getter
public class PostSlice<T> {

    private List<T> content = new ArrayList<>();

    private boolean last;

    public PostSlice(final List<T> content, final boolean last) {
        this.content.addAll(content);
        this.last = last;
    }

    public static <T> PostSlice<T> from(Slice<T> slice) {
        return new PostSlice<>(slice.getContent(), slice.isLast());
    }
}
