package Protocol.Message.RequestValues;

import Protocol.Message.models.RoomAccess;

import java.awt.*;

public record RoomInitializationForm(Integer maxCountOfPlayers,
                                     RoomAccess access,
                                     Color playerColor,
                                     GameInitializationForm gameInitializationForm) implements RequestValue {

    @Override
    public String toString() {
        return "RoomInitializationForm{" +
                "maxCountOfPlayers=" + maxCountOfPlayers +
                ", access=" + access +
                ", gameInitializationForm=" + gameInitializationForm +
                '}';
    }
}
