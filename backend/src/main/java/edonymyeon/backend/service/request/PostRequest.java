package edonymyeon.backend.service.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostRequest {

    private String title;

    private String content;

    private Long price;
    // todo private final List<MultipartFile> images;
}

