package edonymyeon.backend.image.ui;

import edonymyeon.backend.image.ImageExtension;
import edonymyeon.backend.image.application.ImageMigrationService;
import edonymyeon.backend.image.application.ImageService;
import java.net.MalformedURLException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

// todo: 이미지 migration 되면 없어질 컨트롤러
@RequiredArgsConstructor
@RestController
public class ImageController {

    private final ImageService imageService;
    private final ImageMigrationService imageMigrationService;

    @GetMapping("/images/{fileName}")
    public ResponseEntity<Resource> loadImage(@PathVariable String fileName) throws MalformedURLException {
        final UrlResource urlResource = new UrlResource("file:" + imageService.findResourcePath(fileName));

        return ResponseEntity.ok()
                .contentType(ImageExtension.findMediaType(fileName))
                .body(urlResource);
    }

    @GetMapping("/image-migration")
    public ResponseEntity<Void> migrateImages() {
        imageMigrationService.migrate();
        return ResponseEntity.ok().build();
    }
}
