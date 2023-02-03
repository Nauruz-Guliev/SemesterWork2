package Protocol.Message.ResponseValues;


public record ResponseError(String errorMessage) implements ResponseValue {

    @Override
    public String toString() {
        return "ResponseError{"  + errorMessage + '}';
    }
}
