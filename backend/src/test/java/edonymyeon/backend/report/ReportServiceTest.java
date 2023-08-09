package edonymyeon.backend.report;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.report.application.ReportRepository;
import edonymyeon.backend.report.application.ReportRequest;
import edonymyeon.backend.report.application.ReportService;
import edonymyeon.backend.report.domain.Report;
import edonymyeon.backend.support.PostTestSupport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest
class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private PostTestSupport postTestSupport;

    @Test
    void 특정_게시글을_신고할_수_있다() {
        final Post post = postTestSupport.builder().build();
        final Long reportId = reportService.report(new ReportRequest(post.getId(), 4, ""));

        final Report report = reportRepository.findById(reportId).get();
        Assertions.assertThat(report).extracting("id").isEqualTo(reportId);
    }

    @Test
    void 존재하지_않는_게시글은_신고할_수_없다() {
        Assertions.assertThatThrownBy(() -> reportService.report(new ReportRequest(-2L, 4, "")))
                .isInstanceOf(EdonymyeonException.class);
    }
}
