package edonymyeon.backend.post.application;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
