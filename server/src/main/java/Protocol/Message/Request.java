package Protocol.Message;

import Protocol.Message.RequestValues.RequestValue;

public record Request(Type type, RequestValue value) {

    @Override
    public String toString() {
        return "Request{" +
                "type=" + type +
                ", value=" + value +
                '}';
    }

    public enum Type {

    //этап входа
        USER_REGISTER((byte)11), //ничего не возвращает
        USER_LOGIN((byte)12), //возвращает User

    //этап главного лобби
        USER_LOGOUT((byte)21), //ничего не возвращает
        USER_UPDATE((byte)22), //ничего не возвращает
        ROOM_INITIALIZE((byte)23), //возвращает Room
        ROOM_CONNECT((byte)24), //возвращает Room
        GET_OPEN_ROOMS((byte)25), //возвращает List<Room>

    //этап лобби комнаты
        //можно отсылать только если не готов
        ROOM_DISCONNECT((byte)31), //ничего не возвращает
        ROOM_I_AM_READY_TO_START((byte)32), //ничего не возвращает
        ROOM_SET_COLOR((byte)33),

        //можно отсылать только если готов
        ROOM_I_AM_NOT_READY_TO_START((byte)34), //ничего не возвращает
        GAME_START((byte)35), //ничего не возвращает

        //можно отсылать всегда в лобби комнаты
        ROOM_GET((byte)36), //возвращает Room

    //этап игры
        //клиент отсылает серверу
        GAME_DISCONNECT((byte)41), //ничего не возвращает
        GAME_DATA_GET((byte)42), //возвращает Game

        //сервер отсылает клиенту
        GAME_STARTED((byte)43), //ничего не возвращает
        GAME_ENDED((byte)44), //ничего не возвращает
        GAME_ACTION_ARMY_END_MOVE((byte)45), //ничего не возвращает

        //и сервер клиенту, и клиент серверу
        GAME_ACTION_ARMY_START_MOVE((byte)46), //ничего не возвращает

    //можно отправить только в самом начале
        START((byte)51),

    //можно отправить всегда
        EXIT((byte)52); //ничего не возвращает

        final byte value;
        public byte getValue() {
            return value;
        }
        Type(byte value) {
            this.value = value;
        }
    }
}
