package edonymyeon.backend.image.postimage.repository;

import edonymyeon.backend.image.postimage.domain.PostImageInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostImageInfoRepository extends JpaRepository<PostImageInfo, Long> {

    List<PostImageInfo> findAllByPostId(final Long postId);

    @Modifying
    @Query("delete from PostImageInfo pi where pi.post.id=:postId")
    void deleteAllByPostId(@Param("postId") final Long postId);
}
