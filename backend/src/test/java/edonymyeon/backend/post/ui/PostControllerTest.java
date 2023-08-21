package edonymyeon.backend.post.ui;

import static edonymyeon.backend.global.exception.ExceptionInformation.POST_MEMBER_NOT_SAME;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edonymyeon.backend.global.controlleradvice.dto.ExceptionResponse;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.domain.TestMemberBuilder;
import edonymyeon.backend.post.ImageFileCleaner;
import edonymyeon.backend.post.application.dto.PostResponse;
import edonymyeon.backend.support.IntegrationTest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SuppressWarnings("NonAsciiCharacters")
@AutoConfigureMockMvc
@IntegrationTest
class PostControllerTest implements ImageFileCleaner {

    private static MockMultipartFile 이미지1;
    private static MockMultipartFile 이미지2;

    static {
        try {
            이미지1 = new MockMultipartFile(
                    "images",
                    "test_image_1.jpg",
                    "image/jpg",
                    PostControllerTest.class.getResourceAsStream("/static/img/file/test_image_1.jpg")
            );
            이미지2 = new MockMultipartFile(
                    "images",
                    "test_image_2.jpg",
                    "image/jpg",
                    PostControllerTest.class.getResourceAsStream("/static/img/file/test_image_2.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    protected TestMemberBuilder testMemberBuilder;

    @Autowired
    MockMvc mockMvc;

    @Test
    void 사진_첨부_성공_테스트() throws Exception {
        final Member member = testMemberBuilder.builder()
                .build();

        mockMvc.perform(MockMvcRequestBuilders.multipart("/posts")
                        .file(이미지1)
                        .file(이미지2)
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
        mockMvc.perform(MockMvcRequestBuilders.multipart("/posts")
                        .file(이미지1)
                        .file(이미지2)
                        .param("title", "test title")
                        .param("content", "test content")
                        .param("price", "1000")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void 본인이_작성한_게시글_삭제_가능_테스트() throws Exception {
        final Member member = testMemberBuilder.builder()
                .build();

        final MvcResult 게시글_생성_요청_결과 = mockMvc.perform(MockMvcRequestBuilders.multipart("/posts")
                        .file(이미지1)
                        .file(이미지2)
                        .param("title", "test title")
                        .param("content", "test content")
                        .param("price", "1000")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "Basic "
                                + Base64.getEncoder()
                                .encodeToString((member.getEmail() + ":" + member.getPassword()).getBytes()))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();

        PostResponse 게시글_생성_응답 = extractResponseFromResult(게시글_생성_요청_결과, PostResponse.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/" + 게시글_생성_응답.id())
                        .header(HttpHeaders.AUTHORIZATION, "Basic "
                                + Base64.getEncoder()
                                .encodeToString((member.getEmail() + ":" + member.getPassword()).getBytes())))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    private <T> T extractResponseFromResult(MvcResult mvcResult, Class<T> valueType)
            throws UnsupportedEncodingException, JsonProcessingException {
        MockHttpServletResponse mockHttpServletResponse = mvcResult.getResponse();
        String responseBody = mockHttpServletResponse.getContentAsString(StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(responseBody, valueType);
    }

    @Test
    void 본인이_작성하지_않은_게시글_삭제_불가능_테스트() throws Exception {
        final Member member = testMemberBuilder.builder()
                .build();

        final MvcResult 게시글_생성_요청_결과 = mockMvc.perform(MockMvcRequestBuilders.multipart("/posts")
                        .file(이미지1)
                        .file(이미지2)
                        .param("title", "test title")
                        .param("content", "test content")
                        .param("price", "1000")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "Basic "
                                + Base64.getEncoder()
                                .encodeToString((member.getEmail() + ":" + member.getPassword()).getBytes()))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();

        PostResponse 게시글_생성_응답 = extractResponseFromResult(게시글_생성_요청_결과, PostResponse.class);
        final Member otherMember = testMemberBuilder.builder()
                .build();

        final MvcResult 게시글_삭제_요청_결과 = mockMvc.perform(MockMvcRequestBuilders.delete("/posts/" + 게시글_생성_응답.id())
                        .header(HttpHeaders.AUTHORIZATION, "Basic "
                                + Base64.getEncoder()
                                .encodeToString((otherMember.getEmail() + ":" + otherMember.getPassword()).getBytes())))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn();

        ExceptionResponse 예외_응답 = extractResponseFromResult(게시글_삭제_요청_결과, ExceptionResponse.class);

        assertSoftly(softly -> {
                    softly.assertThat(예외_응답.errorCode()).isEqualTo(POST_MEMBER_NOT_SAME.getCode());
                    softly.assertThat(예외_응답.errorCode()).isEqualTo(POST_MEMBER_NOT_SAME.getCode());
                }
        );
    }
}
