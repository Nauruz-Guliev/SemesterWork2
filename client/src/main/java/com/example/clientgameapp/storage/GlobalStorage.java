package com.example.clientgameapp.storage;

import Protocol.Message.ResponseValues.User;
import com.example.clientgameapp.GameApp;
import com.example.clientgameapp.controllers.lobby.LobbyController;

import java.awt.*;
import java.util.concurrent.ScheduledExecutorService;

public class GlobalStorage {
    private static GlobalStorage instance;
    private String roomId;

    private GameApp gameApp;

    private User user;
    private Color color;

   private LobbyController lobbyController;

    private ScheduledExecutorService scheduler;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private GlobalStorage() {
    }

    static {
        try {
            instance = new GlobalStorage();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in creating singleton instance");
        }
    }

    public static GlobalStorage getInstance() {
        return instance;
    }

    public void nullifyAll() {
        this.color = null;
        this.roomId = null;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    public GameApp getMainApp() {
        return gameApp;
    }

    public void setMainApp(GameApp gameApp) {
        this.gameApp = gameApp;
    }

    public void setScheduler(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }

    public LobbyController getLobbyController() {
        return lobbyController;
    }

    public void setLobbyController(LobbyController lobbyController) {
        this.lobbyController = lobbyController;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}
