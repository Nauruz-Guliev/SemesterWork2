package Server.models;

import Protocol.HighLevelMessageManager;
import Protocol.Message.Request;
import Protocol.Message.RequestValues.GameInitializationForm;
import Protocol.Message.RequestValues.RoomInitializationForm;
import Protocol.Message.ResponseValues.Room;
import Protocol.Message.ResponseValues.User;
import Protocol.Message.models.RoomAccess;
import Protocol.ProtocolVersionException;
import Server.models.validators.ValidatorException;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class RoomDB {

    private String code;
    private final Integer maxCountOfPlayers;
    private final RoomAccess access;

    private final List<UserDB> users;
    private final Map<UserDB, Socket> usersSockets;
    private final Map<UserDB, Boolean> usersIsReady;


    private final Map<UserDB, Color> usersColor;
    private final GameInitializationForm gameInitializationForm;

    public RoomDB(RoomInitializationForm form) {
        this.maxCountOfPlayers = form.maxCountOfPlayers();
        this.access = form.access();
        this.gameInitializationForm = form.gameInitializationForm();

        this.users = new ArrayList<>();
        this.usersColor = new HashMap<>();
        this.usersIsReady = new HashMap<>();
        this.usersSockets = new HashMap<>();
    }

    public Room toRoom() {
        Map<User, Color> colorMap = new HashMap<>();
        for (UserDB userDB : users) {
            colorMap.put(userDB.toUser(), usersColor.get(userDB));
        }
        Map<User, Boolean> readyMap = new HashMap<>();
        for (UserDB userDB : users) {
            readyMap.put(userDB.toUser(), usersIsReady.get(userDB));
        }

        return new Room(
                code,
                maxCountOfPlayers,
                access,
                users.stream().map(UserDB::toUser).collect(Collectors.toList()),
                colorMap,
                readyMap,
                gameInitializationForm
                );
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isPublic() {
        return access.equals(RoomAccess.PUBLIC);
    }

    public boolean isGameInProcess() {
        return gameDB != null && gameDB.isGameInProcess();
    }

    private final Lock lock = new ReentrantLock();

    public boolean isFull() {
        lock.lock();
        boolean isFull =  users.size() >= maxCountOfPlayers;
        lock.unlock();
        return isFull;
    }

    public boolean isEmpty() {
        lock.lock();
        boolean isEmpty = users.isEmpty();
        lock.unlock();
        return isEmpty;
    }

    public boolean isReady(UserDB user) {
        lock.lock();
        boolean isReady = usersIsReady.get(user);
        lock.unlock();
        return isReady;
    }

    public boolean isEverybodyReady() {
        lock.lock();
        boolean isEverybodyReady = true;
        for (UserDB user : users) {
            isEverybodyReady = isEverybodyReady && isReady(user);
        }
        lock.unlock();
        return isEverybodyReady;
    }

    public void addUserToRoom(UserDB user, Color color, Socket socket) throws ValidatorException {
        lock.lock();
        if (isFull()) {
            lock.unlock();
            throw new ValidatorException("Room is full. ");
        }
        if (isGameInProcess()) {
            lock.unlock();
            throw new ValidatorException("Game in process. ");
        }
        users.add(user);
        usersIsReady.put(user, false);
        usersSockets.put(user, socket);
        usersColor.put(user, color);
        lock.unlock();
    }

    public void removeUserFromRoom(UserDB user) {
        lock.lock();
        users.remove(user);
        usersIsReady.remove(user);
        usersSockets.remove(user);
        usersColor.remove(user);
        lock.unlock();
    }

    public void setUserIsReady(UserDB user) {
        lock.lock();
        usersIsReady.replace(user, true);
        lock.unlock();
    }

    public void setUserIsNotReady(UserDB user) {
        lock.lock();
        usersIsReady.replace(user, false);
        lock.unlock();
    }

    public void setUserColor(UserDB user, Color color) throws ValidatorException {
        lock.lock();
        if (usersColor.containsValue(color)) {
            lock.unlock();
            throw new ValidatorException("Color is taken. ");
        }
        usersColor.replace(user, color);
        lock.unlock();
    }

    public Lock getRoomLock() {
        return lock;
    }

    public List<Socket> getSockets() {
        return new ArrayList<>(usersSockets.values());
    }


    private GameDB gameDB;

    public void startGame() throws ValidatorException {
        lock.lock();
        if (isGameInProcess()) {
            lock.unlock();
            return;
        }

        if (!isEverybodyReady()) {
            lock.unlock();
            throw new ValidatorException("Not everybody ready");
        }
        if (users.size() < 2) {
            lock.unlock();
            throw new ValidatorException("Not enough players");
        }

        gameDB = new GameDB(gameInitializationForm, users, usersColor);
        gameDB.start();
        lock.unlock();
    }

    public GameDB getGameDB() {
        if (gameDB == null) {
            throw new RuntimeException("Game not in process");
        }
        return gameDB;
    }

    public void endGameDB() {
        gameDB = null;
    }


    @Override
    public String toString() {
        return "RoomDB{" +
                "code='" + code + '\'' +
                ", maxCountOfPlayers=" + maxCountOfPlayers +
                ", access=" + access +
                ", users=" + users +
                ", usersSockets=" + usersSockets +
                ", usersIsReady=" + usersIsReady +
                ", usersColor=" + usersColor +
                ", gameInitializationForm=" + gameInitializationForm +
                ", gameDB=" + gameDB +
                ", lock=" + lock +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoomDB roomDB = (RoomDB) o;

        return Objects.equals(code, roomDB.code);
    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }
}
