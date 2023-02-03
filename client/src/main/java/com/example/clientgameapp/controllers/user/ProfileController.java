package com.example.clientgameapp.controllers.user;

import Protocol.HighLevelMessageManager;
import Protocol.Message.Response;
import Protocol.Message.ResponseValues.ResponseError;
import Protocol.ProtocolVersionException;
import com.example.clientgameapp.DestinationsManager;
import com.example.clientgameapp.storage.GlobalStorage;
import connection.ClientConnectionSingleton;
import exceptions.ClientConnectionException;
import exceptions.ServerException;
import javafx.event.ActionEvent;
import com.example.clientgameapp.controllers.error.ErrorAlert;

import java.io.IOException;
import java.net.Socket;

public class ProfileController {

    private ClientConnectionSingleton connection;
    private Socket socket;

    private DestinationsManager destinationsManager;

    public void initialize() {
        try {
            connection = ClientConnectionSingleton.getInstance();
            socket = connection.getSocketSender();
            destinationsManager = DestinationsManager.getInstance();
        } catch (ClientConnectionException ex) {
            ErrorAlert.show(ex.getMessage());
        }
    }

    public void navigateToRoomList(ActionEvent actionEvent) {
        destinationsManager.navigateRoomListScene();
    }

    public void navigateToRoomCreation(ActionEvent actionEvent) {
        destinationsManager.navigateRoomCreationScene();
    }

    public void logout(ActionEvent actionEvent) {
        new Thread(
                () -> {
                    try {
                        Response logoutMessage = HighLevelMessageManager.logoutUser(socket);
                        if(logoutMessage.type() == Response.Type.RESPONSE_ERROR) {
                            ResponseError error = (ResponseError) logoutMessage.value();
                            throw new ServerException(error.errorMessage());
                        } else {
                            destinationsManager.navigateLoginScene();
                        }
                    } catch (ServerException | ProtocolVersionException  e) {
                        ErrorAlert.show(e.getMessage());
                    }  catch (IOException e) {
                        ErrorAlert.show(e.getMessage());
                        GlobalStorage.getInstance().getMainApp().closeGame();
                    }
                }
        ).start();
    }
}
