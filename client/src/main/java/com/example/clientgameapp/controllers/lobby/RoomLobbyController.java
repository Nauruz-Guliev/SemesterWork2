package com.example.clientgameapp.controllers.lobby;

import Protocol.HighLevelMessageManager;
import Protocol.Message.Response;
import Protocol.Message.ResponseValues.OpenRoomsList;
import Protocol.Message.ResponseValues.Room;
import Protocol.ProtocolVersionException;
import com.example.clientgameapp.DestinationsManager;
import com.example.clientgameapp.storage.GlobalStorage;
import utils.Converter;
import com.example.clientgameapp.controllers.listViewItems.RoomCell;
import connection.ClientConnectionSingleton;
import exceptions.ClientConnectionException;
import exceptions.ClientException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.example.clientgameapp.controllers.error.ErrorAlert;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class RoomLobbyController {
    public ListView<Room> roomsList;

    private ClientConnectionSingleton connection;

    @FXML
    private ColorPicker gameColorPicker;
    private Socket socket;
    private Color color;

    private DestinationsManager destinationsManager;

    public void initialize() {
        new Thread(
                () -> {
                    try {
                        connection = ClientConnectionSingleton.getInstance();
                        socket = connection.getSocketSender();
                        destinationsManager = DestinationsManager.getInstance();
                        Response message = HighLevelMessageManager.getOpenRooms(socket);
                        OpenRoomsList list = (OpenRoomsList) message.value();
                        List<Room> rooms = list.openRooms();

                        ObservableList<Room> roomList = FXCollections.observableArrayList();
                        roomList.addAll(rooms);
                        roomsList.setItems(roomList);
                        roomsList.setCellFactory(studentListView -> new RoomCell());

                    } catch (ClientConnectionException | ProtocolVersionException ex) {
                        System.out.println(ex.getMessage());
                        ErrorAlert.show(ex.getMessage());
                    } catch (IOException e) {
                        ErrorAlert.show(e.getMessage());
                        GlobalStorage.getInstance().getMainApp().closeGame();
                    }
                }
        ).start();
    }

    public void navigateRoomCreation(ActionEvent actionEvent) {
        destinationsManager.navigateRoomCreationScene();

    }

    public void navigateRoomList(ActionEvent actionEvent) {
        destinationsManager.navigateRoomListScene();
    }

    public void connectToRoom(ActionEvent actionEvent) {
        try {
            if (color == null) {
                throw new ClientException("You need to choose a color!");
            } else {
                Room selectedRoom = roomsList.getSelectionModel().getSelectedItems().get(0);
                if (selectedRoom != null) {
                    Room currentRoom = roomsList.getSelectionModel().getSelectedItems().get(0);
                    GlobalStorage globalStorage = GlobalStorage.getInstance();
                    globalStorage.setRoomId(currentRoom.code());
                    globalStorage.setColor(color);
                    destinationsManager.navigateLobbyScene();
                    System.out.println(roomsList.getSelectionModel().getSelectedItems());
                } else {
                    throw new ClientException("You need to choose a room");
                }
            }
        } catch (ClientException ex) {
            ErrorAlert.show(ex.getMessage());
        }
    }

    public void getColor(ActionEvent actionEvent) {
        javafx.scene.paint.Color originalColor = gameColorPicker.getValue();
        color = Converter.convertColor(originalColor);
    }

}
