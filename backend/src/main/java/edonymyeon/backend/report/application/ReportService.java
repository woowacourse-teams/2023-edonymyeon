package edonymyeon.backend.report.application;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import edonymyeon.backend.report.domain.AbusingType;
import edonymyeon.backend.report.domain.Report;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final PostRepository postRepository;

    private final ReportRepository reportRepository;

    private final MemberRepository memberRepository;

    public Long report(ReportRequest reportRequest, MemberId reporterId) {
        final Post post = postRepository.findById(reportRequest.postId())
                .orElseThrow(() -> new EdonymyeonException(ExceptionInformation.POST_ID_NOT_FOUND));

        final Member reporter = memberRepository.findById(reporterId.id())
                .orElseThrow(() -> new EdonymyeonException(ExceptionInformation.MEMBER_ID_NOT_FOUND));

        final Report report = Report.builder()
                .post(post)
                .reporter(reporter)
                .abusingType(AbusingType.of(reportRequest.reportId()))
                .additionalComment(reportRequest.content())
                .build();

        return reportRepository.save(report).getId();
    }
}
