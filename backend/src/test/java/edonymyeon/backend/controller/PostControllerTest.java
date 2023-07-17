package edonymyeon.backend.controller;

import java.io.InputStream;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
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
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }


}
