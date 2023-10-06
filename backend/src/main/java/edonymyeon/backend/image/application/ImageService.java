package edonymyeon.backend.image.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.IMAGE_EXTENSION_INVALID;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.image.ImageExtension;
import edonymyeon.backend.image.ImageFileNameStrategy;
import edonymyeon.backend.image.domain.Domain;
import edonymyeon.backend.image.domain.ImageInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageClient imageClient;

    private final ImageFileNameStrategy imageFileNameStrategy;

    private final Domain domain;

    @Value("${image.root-dir}")
    private String rootDirectory; //todo: 어디다 두는게 좋을까?

    public ImageInfo save(final MultipartFile image, final ImageType imageType) {
        final String originalFileName = image.getOriginalFilename();
        validateExtension(originalFileName);
        final String storeName = imageFileNameStrategy.createName(originalFileName);
        final ImageInfo imageInfo = new ImageInfo(storeName); //todo: imageType에 따라 imageInfo 변환
        imageClient.upload(
                image,
                rootDirectory + imageType.getSaveDirectory(),
                storeName
        );
        return imageInfo; //todo: 생성된 imageInfo 저장 및 반환
    }

    public List<ImageInfo> saveAll(final List<MultipartFile> images, final ImageType imageType) {
        return images.stream()
                .map(each -> save(each, imageType))
                .toList();
    }

    private void validateExtension(final String originalFileName) {
        final String ext = ImageExtension.extractExt(originalFileName);
        if (ImageExtension.contains(ext)) {
            return;
        }
        throw new EdonymyeonException(IMAGE_EXTENSION_INVALID);
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
        return domain.getDomain() + imageType.getSaveDirectory();
    }

    public void removeImage(final ImageInfo imageInfo, final ImageType imageType) {
        if(!imageClient.supportsDeletion()){
            return;
        }
        //todo: 이미지 삭제?
        imageClient.delete(findResourcePath(imageInfo.getStoreName(), imageType));
    }

    public String convertToImageUrl(final String fileName, final ImageType imageType) {
        return domain.convertToImageUrl(imageType.getSaveDirectory() + fileName);
    }

    public List<String> removeDomainFromUrl(final List<String> originalImageUrls, final ImageType imageType) {
        return domain.removeDomainFromUrl(originalImageUrls, imageType.getSaveDirectory());
    }
}
