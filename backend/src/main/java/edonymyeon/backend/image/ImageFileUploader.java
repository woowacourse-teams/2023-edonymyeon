package edonymyeon.backend.image;

import static edonymyeon.backend.global.exception.ExceptionInformation.IMAGE_EXTENSION_INVALID;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.image.domain.ImageInfo;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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

    public List<ImageInfo> uploadFiles(final List<MultipartFile> multipartFiles) {
        return multipartFiles.stream()
                .map(this::uploadFile)
                .toList();
    }

    public ImageInfo uploadFile(final MultipartFile multipartFile) {
        final String originalFileName = multipartFile.getOriginalFilename();
        validateExtension(originalFileName);
        final String uploadedFileName = imageFileNameStrategy.createName(originalFileName);
        final ImageInfo imageInfo = new ImageInfo(uploadedFileName);

        uploadRealStorage(multipartFile, uploadedFileName);
        return imageInfo;
    }

    private void validateExtension(final String originalFileName) {
        final String ext = ImageExtension.extractExt(originalFileName);
        if (ImageExtension.contains(ext)) {
            return;
        }
        throw new EdonymyeonException(IMAGE_EXTENSION_INVALID);
    }

    protected void uploadRealStorage(final MultipartFile multipartFile, final String uploadedFileName) {
        try {
            String fullPath = getFullPath(uploadedFileName);
            final Path path = Paths.get(fullPath);
            multipartFile.transferTo(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFullPath(String storeName) {
        return fileDirectory + storeName;
    }

    public void removeFile(final ImageInfo imageInfo) {
        final File file = new File(getFullPath(imageInfo.getStoreName()));
        file.delete();
    }
}
