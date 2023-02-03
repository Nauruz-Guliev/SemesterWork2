package Protocol;

import Protocol.Message.Request;
import Protocol.Message.RequestValues.*;
import Protocol.Message.Response;
import Protocol.Message.ResponseValues.Game;
import Protocol.Message.ResponseValues.ResponseError;
import Protocol.Message.ResponseValues.ResponseValue;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;

import static Protocol.Message.Request.Type.*;
import static Protocol.Message.Response.Type.RESPONSE_ERROR;
import static Protocol.Message.Response.Type.RESPONSE_SUCCESS;

public final class HighLevelMessageManager extends MessageManager {

    /** Если посылать Start с null-кодом, то вернется Start с заполненным кодом (первый сокет).
     * Если посылать Start с заполненным кодом, то вернется пустой ответ (второй сокет)*/
    public static Response start(Start value, Socket socket) throws IOException, ProtocolVersionException {
        return sendRequest(new Request(START, value), socket);
    }

    /**пустой ответ*/
    public static Response registerUser(UserRegistrationForm value, Socket socket) throws IOException, ProtocolVersionException {
        return sendRequest(new Request(USER_REGISTER, value), socket);
    }
    /**Возвращает User*/
    public static Response loginUser(UserLoginForm value, Socket socket) throws IOException, ProtocolVersionException {
        return sendRequest(new Request(USER_LOGIN, value), socket);
    }
    /**пустой ответ*/
    public static Response logoutUser(Socket socket) throws IOException, ProtocolVersionException {
        return sendRequest(new Request(USER_LOGOUT, null), socket);
    }
    /**пустой ответ*/
    public static Response updateUser(UserUpdateForm value, Socket socket) throws IOException, ProtocolVersionException {
        return sendRequest(new Request(USER_UPDATE, value), socket);
    }


    /**Возвращает Room*/
    public static Response initializeRoom(RoomInitializationForm value, Socket socket) throws IOException, ProtocolVersionException {
        return sendRequest(new Request(ROOM_INITIALIZE, value), socket);
    }
    /**Возвращает Room*/
    public static Response connectToRoom(RoomConnectionForm value, Socket socket) throws IOException, ProtocolVersionException {
        return sendRequest(new Request(ROOM_CONNECT, value), socket);
    }
    /**пустой ответ*/
    public static Response disconnectFromRoom(Socket socket) throws IOException, ProtocolVersionException {
        return sendRequest(new Request(ROOM_DISCONNECT, null), socket);
    }
    /**пустой ответ*/
    public static Response setUserReadyToStart(Socket socket) throws IOException, ProtocolVersionException {
        return sendRequest(new Request(ROOM_I_AM_READY_TO_START, null), socket);
    }
    /**пустой ответ*/
    public static Response setUserNotReadyToStart(Socket socket) throws IOException, ProtocolVersionException {
        return sendRequest(new Request(ROOM_I_AM_NOT_READY_TO_START, null), socket);
    }
    /**пустой ответ*/
    public static Response setPlayerNewColor(Socket socket, Color color) throws IOException, ProtocolVersionException {
        return sendRequest(new Request(ROOM_SET_COLOR, new RoomUserColor(color)), socket);
    }
    /**Возвращает Room*/
    public static Response getRoom(Socket socket) throws IOException, ProtocolVersionException {
        return sendRequest(new Request(ROOM_GET, null), socket);
    }
    /**Возвращает OpenRoomList*/
    public static Response getOpenRooms(Socket socket) throws IOException, ProtocolVersionException {
        return sendRequest(new Request(GET_OPEN_ROOMS, null), socket);
    }



    /**пустой ответ*/
    public static Response startGame(Socket socket) throws IOException, ProtocolVersionException {
        return sendRequest(new Request(GAME_START, null), socket);
    }
    /**пустой ответ*/
    public static Response disconnectFromGame(Socket socket) throws IOException, ProtocolVersionException {
        return sendRequest(new Request(GAME_DISCONNECT, null), socket);
    }
    /**пустой ответ*/
    public static Response gameStarted(Game value, Socket socket) throws IOException, ProtocolVersionException {
        return sendRequest(new Request(GAME_STARTED, value), socket);
    }
    /**пустой ответ*/
    public static Response gameEnded(GameResults value, Socket socket) throws IOException, ProtocolVersionException {
        return sendRequest(new Request(GAME_ENDED, value), socket);
    }

    /**пустой ответ*/
    public static Response moveArmyStart(GameArmyStartMove value, Socket socket) throws IOException, ProtocolVersionException {
        return sendRequest(new Request(GAME_ACTION_ARMY_START_MOVE, value), socket);
    }
    /**пустой ответ*/
    public static Response moveArmyEnd(GameArmyEndMove value, Socket socket) throws IOException, ProtocolVersionException {
        return sendRequest(new Request(GAME_ACTION_ARMY_END_MOVE, value), socket);
    }
    /**Возвращает Game*/
    public static Response getGame(Socket socket) throws IOException, ProtocolVersionException {
        return sendRequest(new Request(GAME_DATA_GET, null), socket);
    }


    /**пустой ответ*/
    public static Response exit(Socket socket) throws IOException, ProtocolVersionException {
        return sendRequest(new Request(EXIT, null), socket);
    }


    public static void sendResponseError(String errorMessage, Socket socket) throws IOException {
        sendResponseError(new ResponseError(errorMessage), socket);
    }
    public static void sendResponseError(ResponseError responseError, Socket socket) throws IOException {
        MessageManager.sendResponse(new Response(RESPONSE_ERROR, responseError), socket);
    }

    public static void sendResponseSuccess(ResponseValue responseSuccess, Socket socket) throws IOException {
        MessageManager.sendResponse(new Response(RESPONSE_SUCCESS, responseSuccess), socket);
    }
}
