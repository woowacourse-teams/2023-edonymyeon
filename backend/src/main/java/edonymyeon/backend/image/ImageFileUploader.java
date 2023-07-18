package edonymyeon.backend.image;

import edonymyeon.backend.image.domain.ImageInfo;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
public class ImageFileUploader {

    private final ImageFileNameStrategy imageFileNameStrategy;

    @Value("${file.dir}")
    private String fileDirectory;

    public ImageInfo uploadFile(final MultipartFile multipartFile) {
        try {
            final String originalFileName = multipartFile.getOriginalFilename();
            final String uploadedFileName = imageFileNameStrategy.createName(originalFileName);
            final ImageInfo imageInfo = new ImageInfo(fileDirectory, uploadedFileName);

            String fullPath = fileDirectory + uploadedFileName;
            final Path absolutePath = Paths.get(fullPath).toAbsolutePath();
            multipartFile.transferTo(absolutePath);

            return imageInfo;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
