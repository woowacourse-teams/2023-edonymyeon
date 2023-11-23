package edonymyeon.backend.content.thumbs.domain;

public enum ThumbsType {

    UP, DOWN;

    public ThumbsType getReverseType() {
        if(this == ThumbsType.UP){
            return ThumbsType.DOWN;
        }
        return ThumbsType.UP;
    }
}
