package edonymyeon.backend.image.infrastructure;

import static edonymyeon.backend.global.exception.ExceptionInformation.IMAGE_GET_BYTE_FAILED;
import static edonymyeon.backend.global.exception.ExceptionInformation.UNSUPPORTED_METHOD_CALL;

import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.image.application.ImageClient;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Profile("prod")
@RequiredArgsConstructor
@Component
public class S3ImageClient implements ImageClient {

    private final S3Client s3Client;

    @Value("${s3.bucket}")
    private String bucket;

    @Override
    public void upload(final MultipartFile image, final String directory, final String storeName) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(directory + storeName)
                .build();
        s3Client.putObject(request, RequestBody.fromBytes(getBytes(image)));
    }

    private byte[] getBytes(final MultipartFile image) {
        try {
            return image.getBytes();
        } catch (IOException e) {
            throw new BusinessLogicException(IMAGE_GET_BYTE_FAILED);
        }
    }

    @Override
    public boolean supportsDeletion() {
        return false;
    }

    @Override
    public void delete(final String imagePath) {
        throw new BusinessLogicException(UNSUPPORTED_METHOD_CALL);
    }
}
