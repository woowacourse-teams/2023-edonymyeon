package edonymyeon.backend.image.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.IMAGE_EXTENSION_INVALID;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.image.ImageExtension;
import edonymyeon.backend.image.ImageFileNameStrategy;
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
     * @return 이미지가 저장된 실제 경로
     */
    public String findFullPath(final String storeName, final ImageType imageType) {
        return rootDirectory + storeName;
    }

    /**
     *
     * @param fileName 예를 들면 post/name.png 처럼 이미지 타입까지 같이 들어온다
     * @return 이미지가 저장된 실제 경로
     */
    public String findFullPath(final String fileName) {
        return rootDirectory + fileName;
    }

    //todo: 도메인 붙여서 url 반환하는 법

    public void removeImage(final ImageInfo imageInfo, final ImageType imageType) {
        if(!imageClient.supportsDeletion()){
            return;
        }
        //todo: 이미지 삭제도?
        imageClient.delete(findFullPath(imageInfo.getStoreName(), imageType));
    }
}
