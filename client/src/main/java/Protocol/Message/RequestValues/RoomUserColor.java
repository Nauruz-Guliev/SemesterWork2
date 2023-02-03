package Protocol.Message.RequestValues;

import java.awt.*;

public record RoomUserColor(Color color) implements RequestValue {


    @Override
    public String toString() {
        return "RoomUserColor{" +
                "color=" + color +
                '}';
    }
}
