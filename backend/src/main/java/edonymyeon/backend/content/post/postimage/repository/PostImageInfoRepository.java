package edonymyeon.backend.content.post.postimage.repository;

import edonymyeon.backend.content.post.postimage.domain.PostImageInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostImageInfoRepository extends JpaRepository<PostImageInfo, Long>, PostImageInfoCustomRepository {

    List<PostImageInfo> findAllByPostId(final Long postId);

    @Modifying
    @Query("update PostImageInfo pi set pi.deleted = true  where pi.post.id=:postId")
    void deleteAllByPostId(@Param("postId") final Long postId);

    @Modifying
    @Query("update PostImageInfo pi set pi.deleted = true  where pi.id in (:imageIds)")
    void deleteAllByIds(@Param("imageIds") final List<Long> imageIds);
}
