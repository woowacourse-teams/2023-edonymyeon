package edonymyeon.backend.consumption.domain;

import edonymyeon.backend.post.domain.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Consumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // todo: 게시글이 삭제되어도, 소비확정, 구매확정 내역은 남아있어야 가격 계산이 가능하다. 지워지면 안됨.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Post post;

    @Enumerated(value = EnumType.STRING)
    private ConsumptionType consumptionType;

    @Column
    private Long price;

    // todo: 소비 년, 월 저장

    public Consumption(
            final Post post,
            final ConsumptionType consumptionType,
            final Long price
    ) {
        this.post = post;
        this.consumptionType = consumptionType;
        this.price = price;
    }
}
