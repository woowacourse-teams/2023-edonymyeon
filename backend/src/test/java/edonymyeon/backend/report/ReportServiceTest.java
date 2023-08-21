package edonymyeon.backend.report;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.domain.TestMemberBuilder;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.report.application.ReportRepository;
import edonymyeon.backend.report.application.ReportRequest;
import edonymyeon.backend.report.application.ReportService;
import edonymyeon.backend.report.domain.Report;
import edonymyeon.backend.support.IntegrationTest;
import edonymyeon.backend.support.PostTestSupport;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@IntegrationTest
class ReportServiceTest {

    private final ReportService reportService;

    private final ReportRepository reportRepository;

    private final PostTestSupport postTestSupport;

    private final TestMemberBuilder memberTestSupport;

    @Test
    void 특정_게시글을_신고할_수_있다() {
        final Post post = postTestSupport.builder().build();
        final Long reportId = reportService
                .report(
                        new ReportRequest(post.getId(), 4, ""),
                        new ActiveMemberId(memberTestSupport.builder().build().getId())
                );

        final Report report = reportRepository.findById(reportId).get();
        Assertions.assertThat(report).extracting("id").isEqualTo(reportId);
    }

    @Test
    void 존재하지_않는_게시글은_신고할_수_없다() {
        Assertions.assertThatThrownBy(() -> reportService
                        .report(
                                new ReportRequest(-2L, 4, ""),
                                new ActiveMemberId(memberTestSupport.builder().build().getId()))
                )
                .isInstanceOf(EdonymyeonException.class);
    }
}
