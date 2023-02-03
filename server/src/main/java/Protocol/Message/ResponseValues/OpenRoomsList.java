package Protocol.Message.ResponseValues;

import java.util.List;

public record OpenRoomsList(List<Room> openRooms) implements ResponseValue {

    @Override
    public String toString() {
        return "OpenRoomsList{"  + openRooms + '}';
    }
}
