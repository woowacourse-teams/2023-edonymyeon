package edonymyeon.backend.report.application;

import edonymyeon.backend.content.comment.domain.Comment;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.profile.application.dto.ActiveMemberId;
import edonymyeon.backend.content.post.domain.Post;
import edonymyeon.backend.report.domain.Report;
import edonymyeon.backend.support.CommentTestSupport;
import edonymyeon.backend.support.IntegrationTest;
import edonymyeon.backend.support.PostTestSupport;
import edonymyeon.backend.support.TestMemberBuilder;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import static edonymyeon.backend.global.exception.ExceptionInformation.COMMENT_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_ID_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@IntegrationTest
class ReportServiceTest {

    private final ReportService reportService;

    private final ReportRepository reportRepository;

    private final PostTestSupport postTestSupport;

    private final CommentTestSupport commentTestSupport;

    private final TestMemberBuilder memberTestSupport;

    @Test
    void 특정_게시글을_신고할_수_있다() {
        final Post post = postTestSupport.builder().build();
        final Long reportId = reportService
                .report(
                        new ReportRequest("POST", post.getId(), 4, null),
                        new ActiveMemberId(memberTestSupport.builder().build().getId())
                );

        final Report report = reportRepository.findById(reportId).get();
        assertThat(report).extracting("id").isEqualTo(reportId);
    }

    @Test
    void 존재하지_않는_게시글은_신고할_수_없다() {
        assertThatThrownBy(() -> reportService
                .report(
                        new ReportRequest("POST", -2L, 4, ""),
                        new ActiveMemberId(memberTestSupport.builder().build().getId()))
        )
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(POST_ID_NOT_FOUND.getMessage());
    }

    @Test
    void 특정_댓글을_신고할_수_있다() {
        final Comment comment = commentTestSupport.builder().build();
        final Long reportId = reportService
                .report(
                        new ReportRequest("COMMENT", comment.getId(), 4, null),
                        new ActiveMemberId(memberTestSupport.builder().build().getId())
                );

        final Report report = reportRepository.findById(reportId).get();
        assertThat(report).extracting("id").isEqualTo(reportId);
    }

    @Test
    void 존재하지_않는_댓글은_신고할_수_없다() {
        assertThatThrownBy(() -> reportService
                .report(
                        new ReportRequest("COMMENT", -2L, 4, ""),
                        new ActiveMemberId(memberTestSupport.builder().build().getId()))
        )
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(COMMENT_ID_NOT_FOUND.getMessage());
    }
}
