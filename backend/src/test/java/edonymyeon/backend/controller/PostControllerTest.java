package edonymyeon.backend.controller;

import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.support.MemberTestSupport;
import java.io.InputStream;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("NonAsciiCharacters")
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
@AutoConfigureMockMvc
@SpringBootTest
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberTestSupport memberTestSupport;

    @Test
    void 사진_첨부_성공_테스트() throws Exception {
        final Member member = memberTestSupport.builder()
                .email("email")
                .password("password")
                .build();

        // 파일 1
        InputStream file1InputStream = getClass().getResourceAsStream("/static/img/file/test_image_1.jpg");
        MockMultipartFile file1 = new MockMultipartFile("images", "test_image_1.jpg", "image/jpg",
                file1InputStream);

        // 파일 2
        InputStream file2InputStream = getClass().getResourceAsStream("/static/img/file/test_image_2.jpg");
        MockMultipartFile file2 = new MockMultipartFile("images", "test_image_2.jpg", "image/jpg",
                file2InputStream);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/posts")
                        .file(file1)
                        .file(file2)
                        .param("title", "test title")
                        .param("content", "test content")
                        .param("price", "1000")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "Basic "
                                + java.util.Base64.getEncoder()
                                .encodeToString((member.getEmail() + ":" + member.getPassword()).getBytes()))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void 회원이_아니면_게시글_작성_불가_테스트() throws Exception {
        // 파일 1
        InputStream file1InputStream = getClass().getResourceAsStream("/static/img/file/test_image_1.jpg");
        MockMultipartFile file1 = new MockMultipartFile("images", "test_image_1.jpg", "image/jpg",
                file1InputStream);

        // 파일 2
        InputStream file2InputStream = getClass().getResourceAsStream("/static/img/file/test_image_2.jpg");
        MockMultipartFile file2 = new MockMultipartFile("images", "test_image_2.jpg", "image/jpg",
                file2InputStream);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/posts")
                        .file(file1)
                        .file(file2)
                        .param("title", "test title")
                        .param("content", "test content")
                        .param("price", "1000")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
