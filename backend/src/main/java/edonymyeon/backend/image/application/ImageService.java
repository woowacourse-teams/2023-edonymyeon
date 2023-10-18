package edonymyeon.backend.image.application;

import edonymyeon.backend.image.domain.ImageInfo;
import edonymyeon.backend.image.domain.ImageInfoFactory;
import edonymyeon.backend.image.domain.ImageType;
import edonymyeon.backend.image.domain.UrlManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final ImageClient imageClient;

    private final ImageInfoFactory imageInfoFactory;

    private final UrlManager urlManager;

    @Value("${image.root-dir}")
    private String rootDirectory;

    public ImageInfo save(final MultipartFile image, final ImageType imageType) {
        final ImageInfo imageInfo = imageInfoFactory.create(image);
        uploadRealStorage(image, imageType, imageInfo.getStoreName());
        return imageInfo;
    }

    private void uploadRealStorage(final MultipartFile image, final ImageType imageType, final String storeName) {
        imageClient.upload(
                image,
                rootDirectory + imageType.getSaveDirectory(),
                storeName
        );
    }

    public List<ImageInfo> saveAll(final List<MultipartFile> images, final ImageType imageType) {
        return images.stream()
                .map(each -> save(each, imageType))
                .toList();
    }

    public void removeImage(final ImageInfo imageInfo, final ImageType imageType) {
        if (!imageClient.supportsDeletion()) {
            return;
        }
        imageClient.delete(findResourcePath(imageInfo.getStoreName(), imageType));
    }

    /**
     * @param storeName 이미지의 이름
     * @param imageType 이미지 사용처
     * @return 리소스가 저장된 실제 경로
     */
    private String findResourcePath(final String storeName, final ImageType imageType) {
        return rootDirectory + imageType.getSaveDirectory() + storeName;
    }

    /**
     * @param fileName 예를 들면 post/name.png 처럼 이미지 타입까지 같이 들어온다
     * @return 리소스가 저장된 실제 경로
     */
    public String findResourcePath(final String fileName) {
        return rootDirectory + fileName;
    }

    /**
     * @param imageType 이미지 사용처
     * @return 사용자에게 보여줄 이미지 도메인 주소(단 이미지 파일 이름은 제외, 예를 들어 https://localhost:8080/images/post/)
     */
    public String findBaseUrl(final ImageType imageType) {
        return urlManager.findBaseUrl(imageType);
    }

    public String convertToImageUrl(final ImageInfo imageInfo, final ImageType imageType) {
        return urlManager.convertToImageUrl(imageType, imageInfo);
    }

    public List<String> convertToStoreName(final List<String> originalImageUrls, final ImageType imageType) {
        return urlManager.convertToStoreName(originalImageUrls, imageType.getSaveDirectory());
    }
}
