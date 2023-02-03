package com.example.clientgameapp.controllers.error;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class ErrorAlert {
    public static void show(String errorMessage) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ErrorResponse");
            alert.setContentText(errorMessage);
            alert.showAndWait();
        });

    }
}
