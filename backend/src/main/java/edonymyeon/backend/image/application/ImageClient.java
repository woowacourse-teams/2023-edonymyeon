package edonymyeon.backend.image.application;

import org.springframework.web.multipart.MultipartFile;

public interface ImageClient {

    void upload(final MultipartFile image, final String directory, final String storeName);

    boolean supportsDeletion();

    void delete(final String imagePath);
}
