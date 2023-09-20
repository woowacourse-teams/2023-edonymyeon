package edonymyeon.backend.notification.repository;

import edonymyeon.backend.notification.domain.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Slice<Notification> findByMemberId(Long memberId, final Pageable page);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.postId = :postId")
    void deleteAllByPostId(Long postId);
}
