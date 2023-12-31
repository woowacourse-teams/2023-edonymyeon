package edonymyeon.backend.post.repository;

import edonymyeon.backend.post.domain.Post;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PostSpecification {

    public static Specification<Post> searchBy(String searchWord) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (Objects.isNull(searchWord) || searchWord.isBlank()) {
                appendNoCondition(criteriaBuilder, predicates);
                return combineAllConditions(criteriaBuilder, predicates);
            }
            appendSearchCondition(searchWord, root, criteriaBuilder, predicates);
            return combineAllConditions(criteriaBuilder, predicates);
        });
    }

    private static void appendNoCondition(CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        predicates.add(criteriaBuilder.disjunction());
    }

    private static Predicate combineAllConditions(CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }

    private static void appendSearchCondition(String searchWord, Root<Post> root, CriteriaBuilder criteriaBuilder,
                                              List<Predicate> predicates) {
        List<String> searchWords = splitKeyWordByBlank(searchWord);
        for (String word : searchWords) {
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("title")), "%" + word + "%"),
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("content")), "%" + word + "%")
            ));
        }
    }

    private static List<String> splitKeyWordByBlank(String searchWord) {
        return Arrays.stream(searchWord.split(" "))
                .filter(word -> !word.isBlank())
                .map(String::toUpperCase)
                .toList();
    }
}
