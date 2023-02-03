package Protocol.Message.ResponseValues;

import Protocol.Message.RequestValues.GameInitializationForm;
import Protocol.Message.models.RoomAccess;

import java.awt.*;
import java.util.List;
import java.util.Map;

public record Room(String code, Integer maxCountOfPlayers, RoomAccess access,
                   List<User> users,
                   Map<User, Color> usersColor,
                   Map<User, Boolean> usersIsReady,
                   GameInitializationForm gameInitializationForm) implements ResponseValue {

    @Override
    public String toString() {
        return "Room{" +
                "code='" + code + '\'' +
                ", maxCountOfPlayers=" + maxCountOfPlayers +
                ", access=" + access +
                ", users=" + users +
                ", usersColor=" + usersColor +
                ", isReady=" + usersIsReady +
                ", gameInitializationForm=" + gameInitializationForm +
                '}';
    }
}
