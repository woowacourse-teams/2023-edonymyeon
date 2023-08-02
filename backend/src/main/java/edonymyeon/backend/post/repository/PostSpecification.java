package edonymyeon.backend.post.repository;

import edonymyeon.backend.post.domain.Post;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostSpecification {

    public static Specification<Post> searchBy(String searchWord) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (searchWord != null && !searchWord.isBlank()) {
                appendSearchCondition(searchWord, root, criteriaBuilder, predicates);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    private static void appendSearchCondition(String searchWord, Root<Post> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        List<String> searchWords = splitKeyWordByBlank(searchWord);
        for (String word : searchWords) {
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("title")), "%" + word + "%"),
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("content")), "%" + word + "%")
            ));
        }
    }

    private static List<String> splitKeyWordByBlank(String searchWord) {
        return Arrays.stream(searchWord.strip().split(" "))
                .filter(word -> !word.isBlank())
                .map(String::toUpperCase)
                .toList();
    }
}
