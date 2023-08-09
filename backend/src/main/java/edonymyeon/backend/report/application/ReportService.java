package edonymyeon.backend.report.application;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import edonymyeon.backend.report.domain.AbusingType;
import edonymyeon.backend.report.domain.Report;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final PostRepository postRepository;

    private final ReportRepository reportRepository;

    public Long report(ReportRequest reportRequest) {
        final Post post = postRepository.findById(reportRequest.postId())
                .orElseThrow(() -> new EdonymyeonException(ExceptionInformation.POST_ID_NOT_FOUND));

        final Report report = Report.builder()
                .post(post)
                .abusingType(AbusingType.of(reportRequest.abusingType()))
                .additionalComment(reportRequest.additionalComment())
                .build();

        return reportRepository.save(report).getId();
    }
}
