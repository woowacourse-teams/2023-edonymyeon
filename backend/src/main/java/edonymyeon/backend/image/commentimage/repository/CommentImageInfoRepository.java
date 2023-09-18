package edonymyeon.backend.image.commentimage.repository;

import edonymyeon.backend.image.commentimage.domain.CommentImageInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentImageInfoRepository extends JpaRepository<CommentImageInfo, Long> {

    @Modifying //todo: 옵션.. 이대로 괜찮은가?
    @Query("update CommentImageInfo c set c.deleted = true where c.id in :ids")
    void deleteAllById(@Param("ids") List<Long> ids);
}
