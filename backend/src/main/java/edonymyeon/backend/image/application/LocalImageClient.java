package edonymyeon.backend.image.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.IMAGE_UPLOAD_FAIL;

import edonymyeon.backend.global.exception.BusinessLogicException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Profile("!prod")
@RequiredArgsConstructor
@Component
public class LocalImageClient implements ImageClient {

    @Override
    public void upload(final MultipartFile image, final String directory, final String storeName) {
        try {
            final Path path = Paths.get(directory + storeName);
            image.transferTo(path);
        } catch (IOException e) {
            throw new BusinessLogicException(IMAGE_UPLOAD_FAIL);
        }
    }

    @Override
    public boolean supportsDeletion() {
        return true;
    }

    @Override
    public void delete(final String imagePath) {
        final File file = new File(imagePath);
        file.delete();
    }
}
