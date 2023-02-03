package Protocol.Message.RequestValues;

import Protocol.Message.ResponseValues.ResponseValue;

public record Start(String code) implements RequestValue, ResponseValue {

    @Override
    public String toString() {
        return "Start{" +
                "code='" + code + '\'' +
                '}';
    }
}
