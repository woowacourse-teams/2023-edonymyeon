package edonymyeon.backend.post.repository;

import edonymyeon.backend.post.domain.Post;
import org.springframework.data.jpa.domain.Specification;

public class PostSpecification {

    public static Specification<Post> searchBy(String searchWord) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.like(root.get("title"), "%" + searchWord + "%"),
                criteriaBuilder.like(root.get("content"), "%" + searchWord + "%")
        );
    }
}
