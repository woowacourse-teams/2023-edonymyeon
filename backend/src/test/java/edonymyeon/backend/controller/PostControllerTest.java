package edonymyeon.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("NonAsciiCharacters")
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    public static final String POST_REQUEST1_TITLE = "Lost in Time";
    public static final String POST_REQUEST1_CONTENT = "A young archaeologist discovers a mysterious artifact that transports her back in time, forcing her to navigate ancient civilizations and find a way back home before history unravels.";
    public static final long POST_REQUEST1_PRICE = 14_000L;

    public static final String POST_REQUEST2_TITLE = "Whispers in the Dark";
    public static final String POST_REQUEST2_CONTENT = "A renowned detective embarks on a perilous journey to solve a series of murders that are linked to a secret society.";
    public static final long POST_REQUEST2_PRICE = 20_000L;

    public static final String POST_REQUEST3_TITLE = "Silent Shadows";
    public static final String POST_REQUEST3_CONTENT = "In a desolate town plagued by a decades-old curse, a curious teenager ventures into an abandoned mansion.";
    public static final long POST_REQUEST3_PRICE = 25_000L;

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("/dummydata.sql")
    void 사진_첨부_성공_테스트() throws Exception {
        // 파일 1
        InputStream file1InputStream = getClass().getResourceAsStream("/static/img/file/test_image_1.jpg");
        MockMultipartFile file1 = new MockMultipartFile("images", "test_image_1.jpg", "image/jpg",
                file1InputStream);

        // 파일 2
        InputStream file2InputStream = getClass().getResourceAsStream("/static/img/file/test_image_2.jpg");
        MockMultipartFile file2 = new MockMultipartFile("images", "test_image_2.jpg", "image/jpg",
                file2InputStream);

        mockMvc.perform(multipart("/posts")
                        .file(file1)
                        .file(file2)
                        .param("title", "test title")
                        .param("content", "test content")
                        .param("price", "1000")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "Basic "
                                + java.util.Base64.getEncoder().encodeToString("email:password".getBytes()))
                )
                .andExpect(status().isCreated());
    }

    @Test
    @Sql("/dummydata.sql")
    void 회원이_아니면_게시글_작성_불가_테스트() throws Exception {
        // 파일 1
        InputStream file1InputStream = getClass().getResourceAsStream("/static/img/file/test_image_1.jpg");
        MockMultipartFile file1 = new MockMultipartFile("images", "test_image_1.jpg", "image/jpg",
                file1InputStream);

        // 파일 2
        InputStream file2InputStream = getClass().getResourceAsStream("/static/img/file/test_image_2.jpg");
        MockMultipartFile file2 = new MockMultipartFile("images", "test_image_2.jpg", "image/jpg",
                file2InputStream);

        mockMvc.perform(multipart("/posts")
                        .file(file1)
                        .file(file2)
                        .param("title", "test title")
                        .param("content", "test content")
                        .param("price", "1000")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Sql("/dummydata.sql")
    void 전체_게시글을_조회한다() throws Exception {
        게시글_하나를_등록한다1();
        게시글_하나를_등록한다2();
        게시글_하나를_등록한다3();

        mockMvc.perform(get("/posts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$", Matchers.hasSize(3)));
    }

    @Test
    @Sql("/dummydata.sql")
    void 전체_게시글을_한_페이지당_1개씩_조회한다() throws Exception {
        게시글_하나를_등록한다1();
        게시글_하나를_등록한다2();
        게시글_하나를_등록한다3();

        mockMvc.perform(get("/posts?size=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    @Sql("/dummydata.sql")
    void 전체_게시글을_한_페이지당_1개씩_2페이지부터_조회한다() throws Exception {
        게시글_하나를_등록한다1();
        게시글_하나를_등록한다2();
        게시글_하나를_등록한다3();

        mockMvc.perform(get("/posts?size=1&page=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].content", Matchers.equalTo(POST_REQUEST2_CONTENT)));
    }

    @Test
    @Sql("/dummydata.sql")
    void 조회할_게시글_정보가_없다() throws Exception {
        mockMvc.perform(get("/posts?size=1&page=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }

    private void 게시글_하나를_등록한다1() throws Exception {
        // 파일 1
        InputStream file1InputStream = getClass().getResourceAsStream("/static/img/file/test_image_1.jpg");
        MockMultipartFile file1 = new MockMultipartFile("images", "test_image_1.jpg", "image/jpg",
                file1InputStream);

        // 파일 2
        InputStream file2InputStream = getClass().getResourceAsStream("/static/img/file/test_image_2.jpg");
        MockMultipartFile file2 = new MockMultipartFile("images", "test_image_2.jpg", "image/jpg",
                file2InputStream);

        mockMvc.perform(multipart("/posts")
                        .file(file1)
                        .file(file2)
                        .param("title", POST_REQUEST1_TITLE)
                        .param("content", POST_REQUEST1_CONTENT)
                        .param("price", String.valueOf(POST_REQUEST1_PRICE))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "Basic "
                                + java.util.Base64.getEncoder().encodeToString("email:password".getBytes()))
                )
                .andExpect(status().isCreated());
    }

    private void 게시글_하나를_등록한다2() throws Exception {
        // 파일 1
        InputStream file1InputStream = getClass().getResourceAsStream("/static/img/file/test_image_1.jpg");
        MockMultipartFile file1 = new MockMultipartFile("images", "test_image_1.jpg", "image/jpg",
                file1InputStream);

        // 파일 2
        InputStream file2InputStream = getClass().getResourceAsStream("/static/img/file/test_image_2.jpg");
        MockMultipartFile file2 = new MockMultipartFile("images", "test_image_2.jpg", "image/jpg",
                file2InputStream);

        mockMvc.perform(multipart("/posts")
                        .file(file1)
                        .file(file2)
                        .param("title", POST_REQUEST2_TITLE)
                        .param("content", POST_REQUEST2_CONTENT)
                        .param("price", String.valueOf(POST_REQUEST2_PRICE))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "Basic "
                                + java.util.Base64.getEncoder().encodeToString("email:password".getBytes()))
                )
                .andExpect(status().isCreated());
    }

    private void 게시글_하나를_등록한다3() throws Exception {
        // 파일 1
        InputStream file1InputStream = getClass().getResourceAsStream("/static/img/file/test_image_1.jpg");
        MockMultipartFile file1 = new MockMultipartFile("images", "test_image_1.jpg", "image/jpg",
                file1InputStream);

        // 파일 2
        InputStream file2InputStream = getClass().getResourceAsStream("/static/img/file/test_image_2.jpg");
        MockMultipartFile file2 = new MockMultipartFile("images", "test_image_2.jpg", "image/jpg",
                file2InputStream);

        mockMvc.perform(multipart("/posts")
                        .file(file1)
                        .file(file2)
                        .param("title", POST_REQUEST3_TITLE)
                        .param("content", POST_REQUEST3_CONTENT)
                        .param("price", String.valueOf(POST_REQUEST3_PRICE))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "Basic "
                                + java.util.Base64.getEncoder().encodeToString("email:password".getBytes()))
                )
                .andExpect(status().isCreated());
    }
}
