package Server.services.Impl;

import Protocol.Message.RequestValues.RoomConnectionForm;
import Protocol.Message.RequestValues.RoomInitializationForm;
import Server.models.RoomDB;
import Server.models.validators.RoomInitValidator;
import Server.models.validators.ValidatorException;
import Server.services.Inter.RoomsService;

import java.util.*;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class RoomsServiceImpl implements RoomsService {

    private final List<RoomDB> activeRooms = new ArrayList<>();
    private final Set<String> takenCodes = new HashSet<>();

    private final RoomInitValidator roomInitValidator;

    public RoomsServiceImpl(RoomInitValidator roomInitValidator) {
        this.roomInitValidator = roomInitValidator;
    }

    private final Lock lock = new ReentrantLock();

    @Override
    public RoomDB create(RoomInitializationForm form) throws ValidatorException {
        roomInitValidator.check(form);

        RoomDB room = createRoom(form);
        lock.lock();
        activeRooms.add(room);
        lock.unlock();
        return room;
    }

    @Override
    public RoomDB getRoom(RoomConnectionForm form) throws ValidatorException {
        String code = form.code();

        if (code == null) {
            throw new ValidatorException("Code is empty. ");
        }

        lock.lock();
        for (RoomDB room : activeRooms) {
            if (room.getCode().equals(code)) {
                lock.unlock();
                return room;
            }
        }
        lock.unlock();
        throw new ValidatorException("No room found with this code. ");
    }

    @Override
    public void remove(RoomDB room) {
        lock.lock();
        if (room.isEmpty()) {
            takenCodes.remove(room.getCode());
            activeRooms.remove(room);
        }
        lock.unlock();
    }

    @Override
    public List<RoomDB> getOpenRooms() {
        lock.lock();
        List<RoomDB> rooms = activeRooms.stream()
                .filter(room -> room.isPublic()
                        && !room.isFull()
                        && !room.isGameInProcess())
                .collect(Collectors.toList());
        lock.unlock();
        return rooms;
    }


    private RoomDB createRoom(RoomInitializationForm form) {
        RoomDB room = new RoomDB(form);

        String code = generateCode();
        room.setCode(code);
        takenCodes.add(code);

        return room;
    }

    private String generateCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0,8);
        } while (takenCodes.contains(code));
        return code;
    }


}
