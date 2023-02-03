package com.example.clientgameapp.storage;

import Protocol.Message.models.City;
import com.example.clientgameapp.models.CitiesGameMap;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.HashSet;

public class GameStorage {
    private static final GameStorage instance;
    private static final HashSet<CitiesGameMap> mapList;

    private GameStorage() {

    }

    static {
        try {
            mapList = new HashSet<>();
            instance = new GameStorage();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in creating singleton instance");
        }
    }


    public static GameStorage getInstance() {
        return instance;
    }



    public void addMap(CitiesGameMap map) {
        mapList.add(map);
    }
    public ArrayList<CitiesGameMap> getMaps(){
        return new ArrayList<>(mapList);
    }

}
