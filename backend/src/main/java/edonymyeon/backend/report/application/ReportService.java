package edonymyeon.backend.report.application;

import edonymyeon.backend.content.comment.domain.Comment;
import edonymyeon.backend.content.comment.repository.CommentRepository;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.member.profile.application.dto.MemberId;
import edonymyeon.backend.member.profile.domain.Member;
import edonymyeon.backend.member.profile.repository.MemberRepository;
import edonymyeon.backend.content.post.domain.Post;
import edonymyeon.backend.content.post.repository.PostRepository;
import edonymyeon.backend.report.domain.AbusingType;
import edonymyeon.backend.report.domain.Report;
import edonymyeon.backend.report.domain.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static edonymyeon.backend.report.domain.ReportType.POST;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    private final ReportRepository reportRepository;

    private final MemberRepository memberRepository;

    @Transactional
    public Long report(ReportRequest reportRequest, MemberId reporterId) {
        final ReportType reportType = ReportType.from(reportRequest.type());
        final Long referenceId = getReferenceId(reportType, reportRequest.referenceId());
        final Member reporter = memberRepository.findById(reporterId.id())
                .orElseThrow(() -> new EdonymyeonException(ExceptionInformation.MEMBER_ID_NOT_FOUND));

        final Report report = new Report(
                reportType,
                referenceId, reporter,
                AbusingType.of(reportRequest.reportCategoryId()),
                reportRequest.content());

        return reportRepository.save(report).getId();
    }

    private Long getReferenceId(final ReportType type, final Long referenceId) {
        if (type == POST) {
            final Post post = postRepository.findById(referenceId)
                    .orElseThrow(() -> new EdonymyeonException(ExceptionInformation.POST_ID_NOT_FOUND));
            return post.getId();
        }

        final Comment comment = commentRepository.findById(referenceId)
                .orElseThrow(() -> new EdonymyeonException(ExceptionInformation.COMMENT_ID_NOT_FOUND));
        return comment.getId();
    }
}
