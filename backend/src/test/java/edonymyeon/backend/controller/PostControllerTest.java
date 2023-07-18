package edonymyeon.backend.controller;

import static edonymyeon.backend.global.exception.ExceptionInformation.POST_MEMBER_FORBIDDEN;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import edonymyeon.backend.global.controlleradvice.dto.ExceptionResponse;
import edonymyeon.backend.post.application.dto.PostResponse;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("NonAsciiCharacters")
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

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

        mockMvc.perform(MockMvcRequestBuilders.multipart("/posts")
                        .file(file1)
                        .file(file2)
                        .param("title", "test title")
                        .param("content", "test content")
                        .param("price", "1000")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "Basic "
                                + java.util.Base64.getEncoder().encodeToString("email:password".getBytes()))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated());
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

    @Test
    @Sql("/dummydata.sql")
    void 본인이_작성한_게시글_삭제_테스트() throws Exception {
        // 파일 1
        InputStream file1InputStream = getClass().getResourceAsStream("/static/img/file/test_image_1.jpg");
        MockMultipartFile file1 = new MockMultipartFile("images", "test_image_1.jpg", "image/jpg",
                file1InputStream);

        // 파일 2
        InputStream file2InputStream = getClass().getResourceAsStream("/static/img/file/test_image_2.jpg");
        MockMultipartFile file2 = new MockMultipartFile("images", "test_image_2.jpg", "image/jpg",
                file2InputStream);

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/posts")
                        .file(file1)
                        .file(file2)
                        .param("title", "test title")
                        .param("content", "test content")
                        .param("price", "1000")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "Basic "
                                + Base64.getEncoder().encodeToString("email:password".getBytes()))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();

        MockHttpServletResponse mockHttpServletResponse = result.getResponse();
        String responseBody = mockHttpServletResponse.getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        PostResponse response = objectMapper.readValue(responseBody, PostResponse.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/" + response.id())
                        .header(HttpHeaders.AUTHORIZATION, "Basic "
                                + Base64.getEncoder().encodeToString("email:password".getBytes())))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @Sql("/dummydata.sql")
    void 본인이_작성하지_않은_게시글_삭제_테스트() throws Exception {
        // 파일 1
        InputStream file1InputStream = getClass().getResourceAsStream("/static/img/file/test_image_1.jpg");
        MockMultipartFile file1 = new MockMultipartFile("images", "test_image_1.jpg", "image/jpg",
                file1InputStream);

        // 파일 2
        InputStream file2InputStream = getClass().getResourceAsStream("/static/img/file/test_image_2.jpg");
        MockMultipartFile file2 = new MockMultipartFile("images", "test_image_2.jpg", "image/jpg",
                file2InputStream);

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/posts")
                        .file(file1)
                        .file(file2)
                        .param("title", "test title")
                        .param("content", "test content")
                        .param("price", "1000")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "Basic "
                                + Base64.getEncoder().encodeToString("email:password".getBytes()))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();

        MockHttpServletResponse mockHttpServletResponse = result.getResponse();
        String responseBody = mockHttpServletResponse.getContentAsString(StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        PostResponse response = objectMapper.readValue(responseBody, PostResponse.class);

        final MvcResult result2 = mockMvc.perform(MockMvcRequestBuilders.delete("/posts/" + response.id())
                        .header(HttpHeaders.AUTHORIZATION, "Basic "
                                + Base64.getEncoder().encodeToString("badman:password".getBytes())))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn();

        MockHttpServletResponse mockHttpServletResponse2 = result2.getResponse();
        String responseBody2 = mockHttpServletResponse2.getContentAsString(StandardCharsets.UTF_8);
        ExceptionResponse response2 = objectMapper.readValue(responseBody2, ExceptionResponse.class);

        assertThat(response2.errorCode()).isEqualTo(POST_MEMBER_FORBIDDEN.getCode());
        assertThat(response2.errorMessage()).isEqualTo(POST_MEMBER_FORBIDDEN.getMessage());
    }
}
