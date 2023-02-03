package Protocol.Message.RequestValues;

import Protocol.Message.ResponseValues.User;

public record GameResults(User winner) implements RequestValue {

    @Override
    public String toString() {
        return "GameResults{" +
                "winner=" + winner +
                '}';
    }
}
