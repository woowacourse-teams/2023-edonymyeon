package edonymyeon.backend.post.application.dto.response;

public record PostConsumptionResponse(
        String type,
        Long purchasePrice,
        Integer year,
        Integer month) {

    private static final String EMPTY_CONSUMPTION_TYPE = "NONE";

    public static PostConsumptionResponse none() {
        return new PostConsumptionResponse(
                EMPTY_CONSUMPTION_TYPE,
                0L,
                0,
                0
        );
    }
}
