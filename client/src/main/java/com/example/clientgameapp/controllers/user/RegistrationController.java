package com.example.clientgameapp.controllers.user;


import Protocol.Message.RequestValues.UserRegistrationForm;
import Protocol.Message.Response;
import Protocol.Message.ResponseValues.ResponseError;
import Protocol.ProtocolVersionException;
import com.example.clientgameapp.DestinationsManager;
import com.example.clientgameapp.GameApp;
import com.example.clientgameapp.storage.GlobalStorage;
import exceptions.ClientException;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import Protocol.HighLevelMessageManager;
import connection.ClientConnectionSingleton;
import exceptions.ClientConnectionException;
import exceptions.ClientInputException;
import com.example.clientgameapp.controllers.error.ErrorAlert;
import utils.Validator;

import java.io.IOException;
import java.net.Socket;

public class RegistrationController {
    public TextField textFieldEmail;
    public TextField textFieldNickName;
    public TextField textFieldPassword;

    private GameApp gameApp;
    private Scene scene;

    private ClientConnectionSingleton connection;
    private Socket socket;

    private DestinationsManager destinationsManager;

    public void initialize() {
        try {
            gameApp = GlobalStorage.getInstance().getMainApp();
            connection = ClientConnectionSingleton.getInstance();
            socket = connection.getSocketSender();
            destinationsManager = DestinationsManager.getInstance();
        } catch (ClientConnectionException ex) {
            ErrorAlert.show(ex.getMessage());
            gameApp.closeGame();
        }
    }


    public void registerUser(ActionEvent actionEvent) {
        new Thread(
                () -> {
                    try {
                        String nickName = textFieldNickName.getText();
                        String password = textFieldPassword.getText();
                        String email = textFieldEmail.getText();
                        System.out.println(nickName + password + email);
                        if (Validator.isValid(nickName) && Validator.isValid(password) && Validator.isValid(email)) {
                            UserRegistrationForm form = new UserRegistrationForm(
                                    email, password, nickName
                            );
                            Response registerMessage = HighLevelMessageManager.registerUser(form, socket);
                            if (registerMessage.type() == Response.Type.RESPONSE_ERROR) {
                                ResponseError error = (ResponseError) registerMessage.value();
                                throw new ClientException(error.errorMessage());
                            } else {
                                openLoginScene(actionEvent);
                            }
                        }
                    } catch ( ProtocolVersionException  | ClientException | ClientInputException e) {
                        ErrorAlert.show(e.getMessage());
                    } catch (IOException ex) {
                        GlobalStorage.getInstance().getMainApp().closeGame();
                        ErrorAlert.show(ex.getMessage());
                    }
                }
        ).start();
    }


    public void openLoginScene(ActionEvent actionEvent) throws IOException {
        destinationsManager.navigateLoginScene();
    }
}