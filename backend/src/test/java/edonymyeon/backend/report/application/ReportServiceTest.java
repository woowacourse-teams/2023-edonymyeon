package edonymyeon.backend.report.application;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.report.domain.Report;
import edonymyeon.backend.report.domain.ReportType;
import edonymyeon.backend.support.IntegrationTest;
import edonymyeon.backend.support.PostTestSupport;
import edonymyeon.backend.support.TestMemberBuilder;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
                        new ReportRequest(ReportType.POST, post.getId(), 4, null),
                        new ActiveMemberId(memberTestSupport.builder().build().getId())
                );

        final Report report = reportRepository.findById(reportId).get();
        assertThat(report).extracting("id").isEqualTo(reportId);
    }

    @Test
    void 존재하지_않는_게시글은_신고할_수_없다() {
        assertThatThrownBy(() -> reportService
                .report(
                        new ReportRequest(ReportType.POST, -2L, 4, ""),
                        new ActiveMemberId(memberTestSupport.builder().build().getId()))
        )
                .isInstanceOf(EdonymyeonException.class);
    }
}
