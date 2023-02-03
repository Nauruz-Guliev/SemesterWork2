package Server.services.Inter;

import Protocol.Message.RequestValues.RoomConnectionForm;
import Protocol.Message.RequestValues.RoomInitializationForm;
import Server.models.RoomDB;
import Server.models.validators.ValidatorException;

import java.util.List;

public interface RoomsService {


    RoomDB create(RoomInitializationForm form) throws ValidatorException;

    RoomDB getRoom(RoomConnectionForm form) throws ValidatorException;

    void remove(RoomDB room);

    List<RoomDB> getOpenRooms();


}
