module com.example.clientgameapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens com.example.clientgameapp to javafx.fxml;
    exports com.example.clientgameapp;
    exports com.example.clientgameapp.controllers.user;
    exports Protocol.Message.models;
    exports com.example.clientgameapp.storage.generator;
    exports com.example.clientgameapp.models;
    exports com.example.clientgameapp.storage;
    exports Protocol.Message.ResponseValues;
    exports Protocol.Message.RequestValues;
    exports com.example.clientgameapp.controllers.error;
    opens com.example.clientgameapp.controllers.user to javafx.fxml;
    exports com.example.clientgameapp.controllers.lobby;
    opens com.example.clientgameapp.controllers.lobby to javafx.fxml;
    exports com.example.clientgameapp.controllers.game;
    opens com.example.clientgameapp.controllers.game to javafx.fxml;
    exports com.example.clientgameapp.controllers.listViewItems;
    exports utils;
}