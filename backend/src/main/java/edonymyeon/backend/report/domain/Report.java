package edonymyeon.backend.report.domain;

import edonymyeon.backend.global.domain.TemporalRecord;
import edonymyeon.backend.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Report extends TemporalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private ReportType reportType;

    private Long referenceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private Member reporter;

    @Enumerated
    private AbusingType abusingType;

    private String additionalComment;

    public Report(
            final ReportType reportType,
            final Long referenceId,
            final Member reporter,
            final AbusingType abusingType,
            final String additionalComment
    ) {
        this.reportType = reportType;
        this.referenceId = referenceId;
        this.reporter = reporter;
        this.abusingType = abusingType;
        this.additionalComment = additionalComment;
    }
}
