package edonymyeon.backend.post.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import edonymyeon.backend.EdonymyeonTest;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.support.PostTestSupport;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.jpa.domain.Specification;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@EdonymyeonTest
public class PostSpecificationTest {

    private final PostTestSupport postTestSupport;
    private final PostRepository postRepository;

    @ParameterizedTest
    @ValueSource(strings = {" 가 나 다  라 마  바", " ", "", "가 나 다"})
    void 빈칸을_기준으로_제대로_나뉘는지_확인한다(String searchWord) {
        List<String> listSplitByBlank = Arrays.stream(searchWord.split(" "))
                .filter(word -> !word.isBlank())
                .toList();

        assertThat(!listSplitByBlank.contains("") && !listSplitByBlank.contains(" ")).isTrue();
    }

    @Test
    void 대소문자_상관없이_제대로_검색되는지_확인한다() {
        postTestSupport.builder()
                .title("ecec")
                .content("I use EnGlish and KoRean")
                .build();

        Specification<Post> searchedBy = PostSpecification.searchBy("reA gLI i");
        List<Post> all = postRepository.findAll(searchedBy);

        assertThat(all.size()).isEqualTo(1);
    }

    @Test
    void 게시글_검색시_제목과_내용에_키워드가_나눠져_있어도_검색이_되는지_확인한다() {
        postTestSupport.builder()
                .title("ecec")
                .content("I use EnGlish and KoRean")
                .build();

        Specification<Post> searchedBy = PostSpecification.searchBy("reA ce ");
        List<Post> all = postRepository.findAll(searchedBy);

        assertThat(all.size()).isEqualTo(1);
    }
}

