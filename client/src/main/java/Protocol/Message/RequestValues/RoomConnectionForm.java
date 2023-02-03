package Protocol.Message.RequestValues;

import java.awt.*;

public record RoomConnectionForm(String code, Color playerColor) implements RequestValue {

    @Override
    public String toString() {
        return "RoomConnectionForm{" +
                "code='" + code + '\'' +
                ", color=" + playerColor +
                '}';
    }
}

