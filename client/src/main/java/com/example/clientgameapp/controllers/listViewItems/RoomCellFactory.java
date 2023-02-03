package com.example.clientgameapp.controllers.listViewItems;


import Protocol.Message.ResponseValues.Room;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;


public class RoomCellFactory implements Callback<ListView<Room>, ListCell<Room>> {
    @Override
    public ListCell<Room> call(ListView<Room> roomListView) {
        return new RoomCell();
    }
}