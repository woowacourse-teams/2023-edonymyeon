package edonymyeon.backend.image.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.IMAGE_ORIGINAL_DIRECTORY_INVALID;
import static java.util.Objects.isNull;

import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.image.commentimage.domain.CommentImageInfo;
import edonymyeon.backend.image.commentimage.repository.CommentImageInfoRepository;
import edonymyeon.backend.image.domain.ImageType;
import edonymyeon.backend.image.postimage.domain.PostImageInfo;
import edonymyeon.backend.image.postimage.repository.PostImageInfoRepository;
import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ImageMigrationService {

    private final PostImageInfoRepository postImageInfoRepository;

    private final CommentImageInfoRepository commentImageInfoRepository;

    private final ImageMigrationClient imageMigrationClient;

    @Value("${image.root-dir}")
    private String newDir;

    @Value("${file.dir}")
    private String originalDir;

    public void migrate() {
        File[] files = new File(originalDir).listFiles(getFilter());
        if (isNull(files)) {
            throw new BusinessLogicException(IMAGE_ORIGINAL_DIRECTORY_INVALID);
        }

        final List<PostImageInfo> postImages = postImageInfoRepository.findAllImages();
        final List<CommentImageInfo> commentImages = commentImageInfoRepository.findAllImages();
        //todo: 프로필 이미지 꺼내오기

        for (File file : files) {
            final String name = file.getName();
            // 게시글 이미지인지 판단
            if (isPostImage(postImages, name)) {
                imageMigrationClient.migrate(file, newDir + ImageType.POST.getSaveDirectory(), name);
            }
            // 댓글 이미지인지 판단
            if (isCommentImage(commentImages, name)) {
                imageMigrationClient.migrate(file, newDir + ImageType.COMMENT.getSaveDirectory(), name);
            }
            // todo: 프로필 이미지라면 -> cloudfront의 profile 폴더로 이동
            // todo: 어디에도 해당되지 않는 경우(db에 정보가 없는!) -> 삭제
        }
    }

    @NotNull
    private FilenameFilter getFilter() {
        return new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return !name.equals("edonymyeon-firebase.json");
            }
        };
    }

    private boolean isPostImage(final List<PostImageInfo> postImages, final String name) {
        return postImages.stream().anyMatch(each -> each.getStoreName().equals(name));
    }

    private boolean isCommentImage(final List<CommentImageInfo> commentImages, final String name) {
        return commentImages.stream().anyMatch(each -> each.getStoreName().equals(name));
    }
}