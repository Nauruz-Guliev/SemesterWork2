package com.example.clientgameapp.controllers.user;

import Protocol.HighLevelMessageManager;
import Protocol.Message.RequestValues.UserLoginForm;
import Protocol.Message.Response;
import Protocol.Message.ResponseValues.ResponseError;
import Protocol.Message.ResponseValues.User;
import Protocol.ProtocolVersionException;

import com.example.clientgameapp.DestinationsManager;
import com.example.clientgameapp.storage.GlobalStorage;
import connection.ClientConnectionSingleton;
import exceptions.ClientConnectionException;
import exceptions.ClientException;
import exceptions.ClientInputException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.example.clientgameapp.controllers.error.ErrorAlert;
import utils.Validator;

import java.io.IOException;
import java.net.Socket;

public class Login {
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldPassword;

    private Stage stage;
    private Scene scene;
    private Parent root;


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


    public void loginUser(ActionEvent actionEvent) {
        new Thread(
                () -> {
                    try {
                        String email = textFieldEmail.getText();
                        String password = textFieldPassword.getText();
                        if (Validator.isValid(email) && Validator.isValid(password)) {
                            System.out.println(email + "  " + password);
                            UserLoginForm loginForm = new UserLoginForm(
                                    email, password
                            );
                            Response userLogin = HighLevelMessageManager.loginUser(
                                    loginForm, socket
                            );
                            if (userLogin.type() == Response.Type.RESPONSE_ERROR) {
                                ResponseError error = (ResponseError) userLogin.value();
                                throw new ClientException(error.errorMessage());
                            } else {
                                User user = (User) userLogin.value();
                                GlobalStorage.getInstance().setUser(user);
                                navigateChoiceScene(actionEvent);
                            }
                        }
                    } catch (ClientInputException | ClientException | ProtocolVersionException e) {
                        ErrorAlert.show(e.getMessage());
                    } catch (IOException e) {
                        ErrorAlert.show(e.getMessage());
                        GlobalStorage.getInstance().getMainApp().closeGame();
                    }
                }
        ).start();
    }

    public void navigateRegistration(ActionEvent actionEvent) throws IOException {
        destinationsManager.navigateRegistrationScene();
    }


    public void navigateChoiceScene(ActionEvent actionEvent) throws IOException {
        destinationsManager.navigateChoiceScene();
    }
}
