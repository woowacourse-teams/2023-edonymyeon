package edonymyeon.backend.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@RequiredArgsConstructor
@IntegrationTest
public class DocsTest {

    protected final MockMvc mockMvc;

    protected final ObjectMapper objectMapper;
}
