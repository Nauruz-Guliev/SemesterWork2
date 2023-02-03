package com.example.clientgameapp.controllers.lobby;

import Protocol.HighLevelMessageManager;
import Protocol.Message.Request;
import Protocol.Message.RequestValues.RoomConnectionForm;
import Protocol.Message.Response;
import Protocol.Message.ResponseValues.ResponseError;
import Protocol.Message.ResponseValues.Room;
import Protocol.Message.ResponseValues.User;
import Protocol.ProtocolVersionException;
import com.example.clientgameapp.DestinationsManager;
import com.example.clientgameapp.models.UserModel;
import com.example.clientgameapp.controllers.listViewItems.ClientCell;
import com.example.clientgameapp.storage.GlobalStorage;
import utils.Converter;
import connection.ClientConnectionSingleton;
import exceptions.ClientConnectionException;
import exceptions.ClientException;
import exceptions.GameException;
import exceptions.ServerException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import com.example.clientgameapp.controllers.error.ErrorAlert;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

public class LobbyController {
    public ListView<UserModel> usersList;
    public ColorPicker gameColorPicker;
    private ClientConnectionSingleton connection;
    private HighLevelMessageManager mManager;
    private Socket socketSender;
    private Socket socketReceiver;
    private boolean isWaiting = true;

    private GlobalStorage storage;
    private DestinationsManager destinationsManager;

    private ScheduledExecutorService scheduler;

    private boolean isFirstStart = true;
    private boolean isReady = false;


    public void initialize() {
        try {
            connection = ClientConnectionSingleton.getInstance();
            mManager = new HighLevelMessageManager();
            socketSender = connection.getSocketSender();
            destinationsManager = DestinationsManager.getInstance();
            socketReceiver = connection.getSocketReceiver();
            storage = GlobalStorage.getInstance();
            storage.setLobbyController(this);
            scheduler = GlobalStorage.getInstance().getScheduler();
            if (storage.getColor() != null && storage.getRoomId() != null) {
                initializeExistingRoom();
            } else {
                initializeNewRoom();
            }
        } catch (ClientConnectionException ex) {
            ErrorAlert.show(ex.getMessage());
        }
    }

    private void initializeExistingRoom() {
        new Thread(
                () -> {
                    try {
                        RoomConnectionForm connectionForm = new RoomConnectionForm(storage.getRoomId(), storage.getColor());
                        Response existingRoom = HighLevelMessageManager.connectToRoom(connectionForm, socketSender);
                        if (existingRoom.type() == Response.Type.RESPONSE_ERROR) {
                            ResponseError error = (ResponseError) existingRoom.value();
                            throw new ServerException(error.errorMessage());
                        } else {
                            initializeList(existingRoom);
                        }
                    } catch (ProtocolVersionException | ServerException ex) {
                        ErrorAlert.show(ex.getMessage());
                    } catch (IOException e) {
                        ErrorAlert.show(e.getMessage());
                        GlobalStorage.getInstance().getMainApp().closeGame();
                    }
                }
        ).start();

    }

    private void initializeNewRoom() {
        new Thread(
                () -> {
                    try {
                        Response newRoom = HighLevelMessageManager.getRoom(socketSender);
                        if (newRoom.type() == Response.Type.RESPONSE_ERROR) {
                            ResponseError error = (ResponseError) newRoom.value();
                            throw new GameException(error.errorMessage());
                        } else {
                            initializeList(newRoom);
                        }
                    } catch (GameException | ProtocolVersionException ex) {
                        ErrorAlert.show(ex.getMessage());
                    } catch (IOException e) {
                        ErrorAlert.show(e.getMessage());
                        GlobalStorage.getInstance().getMainApp().closeGame();
                    }
                }
        ).start();
    }

    private void initializeList(Response roomValue) {
        Room currentRoom = (Room) roomValue.value();
        List<UserModel> userModelList = new ArrayList<>();
        int i = 1;
        for (User user : currentRoom.users()) {
            UserModel model = new UserModel(currentRoom.usersIsReady().get(user), currentRoom.usersColor().get(user), i, user);
            userModelList.add(model);
            i++;
        }
        ObservableList<UserModel> list = FXCollections.observableArrayList();
        list.addAll(userModelList);
        usersList.setItems(list);
        usersList.setCellFactory(studentListView -> new ClientCell());
        if (isFirstStart) {
            updateList();
            listenForMessages();
            isFirstStart = false;
        }
    }

    public void setReadyStatus(ActionEvent actionEvent) {
        new Thread(() -> {
            try {
                if (!isReady) {
                    Response readyMessage = HighLevelMessageManager.setUserReadyToStart(socketSender);
                    if (readyMessage.type() == Response.Type.RESPONSE_ERROR) {
                        ResponseError error = (ResponseError) readyMessage.value();
                        throw new ServerException(error.errorMessage());
                    } else {
                        isReady = true;
                    }
                } else {
                    Response readyMessage = HighLevelMessageManager.setUserNotReadyToStart(socketSender);
                    if (readyMessage.type() == Response.Type.RESPONSE_ERROR) {
                        ResponseError error = (ResponseError) readyMessage.value();
                        throw new ServerException(error.errorMessage());
                    } else {
                        isReady = false;
                    }
                }
            } catch (ServerException | ProtocolVersionException e) {
                ErrorAlert.show(e.getMessage());
            } catch (IOException e) {
                ErrorAlert.show(e.getMessage());
                GlobalStorage.getInstance().getMainApp().closeGame();
            }
        }).start();
    }

    public void startGame(ActionEvent actionEvent) {
        new Thread(
                () -> {
                    try {
                        Response gameStart = HighLevelMessageManager.getRoom(socketSender);
                        if (gameStart.type() == Response.Type.RESPONSE_ERROR) {
                            ResponseError error = (ResponseError) gameStart.value();
                            throw new ServerException(error.errorMessage());
                        } else {
                            Response start = HighLevelMessageManager.startGame(socketSender);
                            if (start.type() == Response.Type.RESPONSE_ERROR) {
                                ResponseError error = (ResponseError) start.value();
                                throw new ServerException(error.errorMessage());
                            }
                            Room room = (Room) gameStart.value();
                            for (Object userStatus : room.usersIsReady().values().toArray()) {
                                if (!(boolean) userStatus) {
                                    throw new ClientException("Not all users are ready");
                                }
                            }
                            scheduler.shutdownNow();
                        }
                    } catch (ProtocolVersionException | ServerException | ClientException ex) {
                        ErrorAlert.show(ex.getMessage());
                    } catch (IOException e) {
                        ErrorAlert.show(e.getMessage());
                        GlobalStorage.getInstance().getMainApp().closeGame();
                    }
                }
        ).start();
    }

    public void updateLobby(ActionEvent actionEvent) {
        new Thread(() -> {
            try {
                Response updatedData = HighLevelMessageManager.getRoom(socketSender);
                if (updatedData.type() == Response.Type.RESPONSE_ERROR) {
                    ResponseError error = (ResponseError) updatedData.value();
                    throw new ServerException(error.errorMessage());
                } else {

                    Platform.runLater(() -> {
                        initializeList(updatedData);
                    });
                }

            } catch (ServerException | ProtocolVersionException e) {
                ErrorAlert.show(e.getMessage());
            } catch (IOException e) {
                ErrorAlert.show(e.getMessage());
                GlobalStorage.getInstance().getMainApp().closeGame();
            }
        }).start();
    }


    private void updateList() {

    }


    private void listenForMessages() {
        new Thread(() -> {
            try {
                while (isWaiting) {
                    Thread.sleep(5000L);
                    Request request = HighLevelMessageManager.readRequest(socketReceiver);
                    if (request.type() == Request.Type.GAME_STARTED) {
                        HighLevelMessageManager.sendResponseSuccess(null, socketReceiver);
                        destinationsManager.navigateGameScene();
                        isWaiting = false;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ProtocolVersionException | InterruptedException e) {
                throw new RuntimeException(e);
            }


        }).start();
    }

    public void leaveLobby(ActionEvent actionEvent) {
        new Thread(() -> {
            try {
                Response message = HighLevelMessageManager.disconnectFromRoom(socketSender);
                if (message.type() == Response.Type.RESPONSE_ERROR) {
                    ResponseError error = (ResponseError) message.value();
                    throw new ServerException(error.errorMessage());
                } else {
                    GlobalStorage.getInstance().nullifyAll();
                    GlobalStorage.getInstance().getScheduler().shutdownNow();
                    destinationsManager.navigateChoiceScene();
                }
            } catch (ProtocolVersionException | ServerException e) {
                ErrorAlert.show(e.getMessage());
            } catch (IOException e) {
                ErrorAlert.show(e.getMessage());
                GlobalStorage.getInstance().getMainApp().closeGame();
            }
        }).start();
    }

    public void changeColor(ActionEvent actionEvent) {
        new Thread(
                () -> {
                    try {
                        javafx.scene.paint.Color originalColor = gameColorPicker.getValue();
                        Color color = Converter.convertColor(originalColor);
                        Response message = HighLevelMessageManager.setPlayerNewColor(socketSender, color);
                        if (message.type() == Response.Type.RESPONSE_ERROR) {
                            ResponseError error = (ResponseError) message.value();
                            throw new ServerException(error.errorMessage());
                        }
                    } catch (ProtocolVersionException | ServerException e) {
                        ErrorAlert.show(e.getMessage());
                    } catch (IOException e) {
                        ErrorAlert.show(e.getMessage());
                        GlobalStorage.getInstance().getMainApp().closeGame();
                    }
                }
        ).start();
    }
}
