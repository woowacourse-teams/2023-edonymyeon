package edonymyeon.backend.image.ui;

import edonymyeon.backend.image.ImageExtension;
import edonymyeon.backend.image.ImageFileUploader;
import java.net.MalformedURLException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ImageController {

    private final ImageFileUploader imageFileUploader;

    @GetMapping("/images/{fileName}")
    public ResponseEntity<Resource> loadImage(@PathVariable String fileName) throws MalformedURLException {
        final UrlResource urlResource = new UrlResource("file:" + imageFileUploader.getFullPath(fileName));

        return ResponseEntity.ok()
                .contentType(ImageExtension.findMediaType(fileName))
                .body(urlResource);
    }
}
