package edonymyeon.backend.support;

import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.repository.PostRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.File;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostIntegrationTestSupport {

    private static final String DEFAULT_TITLE = "title";

    private static final String DEFAULT_CONTENT = "content";

    private static final int DEFAULT_PRICE = 1_000;

    private static File DEFAULT_IMAGE1 = new File("./src/test/resources/static/img/file/test_image_1.jpg");

    private static File DEFAULT_IMAGE2 = new File("./src/test/resources/static/img/file/test_image_2.jpg");

    private final PostRepository postRepository;

    private final MemberTestSupport memberTestSupport;

    public PostIntegrationBuilder builder() {
        return new PostIntegrationBuilder();
    }

    public final class PostIntegrationBuilder {

        private Long id;

        private String title;

        private String content;

        private Long price;

        private Member member;

        private File image;

        private File image2;

        public PostIntegrationBuilder title(final String title) {
            this.title = title;
            return this;
        }

        public PostIntegrationBuilder content(final String content) {
            this.content = content;
            return this;
        }

        public PostIntegrationBuilder price(final Long price) {
            this.price = price;
            return this;
        }

        public PostIntegrationBuilder member(final Member member) {
            this.member = member;
            return this;
        }

        public PostIntegrationBuilder image1(final File image) {
            this.image = image;
            return this;
        }

        public PostIntegrationBuilder image2(final File image) {
            this.image2 = image2;
            return this;
        }

        public ExtractableResponse<Response> build() {
            Member member = this.member == null ? memberTestSupport.builder().build() : this.member;

            return RestAssured
                    .given()
                    .auth().preemptive().basic(member.getEmail(), member.getPassword())
                    .multiPart("title", this.title == null ? DEFAULT_TITLE : this.title)
                    .multiPart("content", this.content == null ? DEFAULT_CONTENT : this.content)
                    .multiPart("price", this.price == null ? DEFAULT_PRICE : this.price)
                    .multiPart("newImages", this.image == null ? DEFAULT_IMAGE1 : this.image,
                            MediaType.IMAGE_JPEG_VALUE)
                    .multiPart("newImages", this.image2 == null ? DEFAULT_IMAGE2 : this.image2,
                            MediaType.IMAGE_JPEG_VALUE)
                    .when()
                    .post("/posts")
                    .then()
                    .extract();
        }
    }
}
