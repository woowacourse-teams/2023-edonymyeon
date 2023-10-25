package edonymyeon.backend.image.infrastructure;

import static edonymyeon.backend.global.exception.ExceptionInformation.IMAGE_CONTENT_TYPE_FAIL;
import static edonymyeon.backend.global.exception.ExceptionInformation.IMAGE_DELETION_FAIL;
import static edonymyeon.backend.global.exception.ExceptionInformation.IMAGE_GET_BYTE_FAILED;

import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.image.application.ImageMigrationClient;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Profile("prod")
@RequiredArgsConstructor
@Component
public class S3ImageMigrationClient implements ImageMigrationClient {

    private final S3Client s3Client;

    @Value("${s3.bucket}")
    private String bucket;

    @Override
    public void migrate(final File image, final String directory, final String storeName) {
        String contentType;
        try {
            contentType = Files.probeContentType(Paths.get(image.toURI()));
        } catch (Exception e) {
            throw new EdonymyeonException(IMAGE_CONTENT_TYPE_FAIL);
        }
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(directory + storeName)
                .contentType(contentType)
                .build();
        s3Client.putObject(request, RequestBody.fromBytes(getBytes(image)));

        deleteFromOriginalDirectory(image);
    }

    private byte[] getBytes(final File image) {
        try {
            return Files.readAllBytes(image.toPath());
        } catch (IOException e) {
            throw new BusinessLogicException(IMAGE_GET_BYTE_FAILED);
        }
    }

    private void deleteFromOriginalDirectory(final File file) {
        final boolean delete = file.delete();
        if(!delete) {
            throw new BusinessLogicException(IMAGE_DELETION_FAIL);
        }
    }
}
