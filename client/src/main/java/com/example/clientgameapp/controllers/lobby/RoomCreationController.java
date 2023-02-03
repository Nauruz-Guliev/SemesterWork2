package com.example.clientgameapp.controllers.lobby;

import Protocol.HighLevelMessageManager;
import Protocol.Message.RequestValues.GameInitializationForm;
import Protocol.Message.RequestValues.RoomInitializationForm;
import Protocol.Message.Response;
import Protocol.Message.ResponseValues.ResponseError;
import Protocol.Message.models.RoomAccess;
import Protocol.ProtocolVersionException;
import com.example.clientgameapp.DestinationsManager;
import com.example.clientgameapp.storage.GlobalStorage;
import utils.Converter;
import connection.ClientConnectionSingleton;
import exceptions.ClientConnectionException;
import exceptions.ClientException;
import exceptions.GameException;
import javafx.event.ActionEvent;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import org.controlsfx.control.ToggleSwitch;
import com.example.clientgameapp.controllers.error.ErrorAlert;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;

public class RoomCreationController {
    public ToggleSwitch togglePrivate;

    public ColorPicker gameColorPicker;
    public Spinner spinnerArmySpeed;
    public Spinner spinnerArmyGrowthRate;

    private ClientConnectionSingleton connection;
    private HighLevelMessageManager mManager;
    private Socket socket;

    private Color color;
    private DestinationsManager destinationsManager;


    public void initialize() {
        try {
            connection = ClientConnectionSingleton.getInstance();
            mManager = new HighLevelMessageManager();
            socket = connection.getSocketSender();
            destinationsManager = DestinationsManager.getInstance();
        } catch (ClientConnectionException ex) {
            ErrorAlert.show(ex.getMessage());
        }
    }

    public void createRoom(ActionEvent actionEvent) {
        new Thread(
                () -> {
                    try {
                        int growthRate = (int) spinnerArmyGrowthRate.getValue();
                        int armySpeed = (int) spinnerArmySpeed.getValue();
                        boolean isPrivate = togglePrivate.isSelected();
                        RoomAccess access;
                        if (color == null) {
                            throw new ClientException("You need to choose a color");
                        }
                        GameInitializationForm gameInitializationForm = new GameInitializationForm(
                                6, armySpeed, growthRate
                        );
                        if (isPrivate) {
                            access = RoomAccess.PRIVATE;
                        } else {
                            access = RoomAccess.PUBLIC;
                        }
                        RoomInitializationForm newRoom = new RoomInitializationForm(
                                2, access, color, gameInitializationForm
                        );
                        Response roomInitializedMessage = HighLevelMessageManager.initializeRoom(newRoom, socket);

                        if (roomInitializedMessage.type() == Response.Type.RESPONSE_ERROR) {
                            ResponseError error = (ResponseError) roomInitializedMessage.value();
                            throw new GameException(error.errorMessage());
                        } else {
                            GlobalStorage.getInstance().setColor(color);
                            destinationsManager.navigateLobbyScene();
                        }
                    } catch (ClientException | GameException |ProtocolVersionException| RuntimeException e) {
                        ErrorAlert.show(e.getMessage());
                    } catch (IOException e) {
                        ErrorAlert.show(e.getMessage());
                        GlobalStorage.getInstance().getMainApp().closeGame();
                    }
                }
        ).start();

    }

    public void navigateProfile(ActionEvent actionEvent) {
        destinationsManager.navigateProfileScene();

    }

    public void navigateRoomList(ActionEvent actionEvent) {
        destinationsManager.navigateRoomListScene();
    }

    public void getColor(ActionEvent actionEvent) {
        javafx.scene.paint.Color originalColor = gameColorPicker.getValue();
        color = Converter.convertColor(originalColor);
    }
}
