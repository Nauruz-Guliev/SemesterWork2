package com.example.clientgameapp.storage.generator;

import Protocol.Message.models.City;
import com.example.clientgameapp.models.CitiesGameMap;
import com.example.clientgameapp.models.Route;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MapGenerator {
    private final List<Button> availableCities;
    private ArrayList<City> cities;
    private ArrayList<Button> utilCityList;

    private ArrayList<Route> routes;

    private CitiesGameMap map;


    private static final int DEFAULT_MAP_WIDTH = 600;
    private static final int DEFAULT_MAP_HEIGHT = 400;

    public MapGenerator(List<Button> availableCities) {
        this.availableCities = availableCities;
    }

    public void generate() {
        utilCityList = new ArrayList<>(availableCities);
        cities = new ArrayList<>();
        routes = new ArrayList<>();
        initCities();
        generateRoutes();
        createCitiesMap();
    }

    private void createCitiesMap() {
        this.map = new CitiesGameMap(routes, cities);
    }

    /**
     * Пути, в которые можно идти
     **/
    private void generateRoutes() {
        generateRoute(1, 2);
        generateRoute(1, 3);
        generateRoute(1, 6);
        generateRoute(2, 3);
        generateRoute(3, 4);
        generateRoute(3, 6);
        generateRoute(4, 5);
        generateRoute(4, 6);
        generateRoute(5, 6);
    }

    /**
     * если можно в одну сторону, можно и обратно
     */
    private void generateRoute(int fromCity, int toCity) {
        routes.add(new Route(utilCityList.get(fromCity - 1), utilCityList.get(toCity - 1)));
        routes.add(new Route(utilCityList.get(toCity - 1), utilCityList.get(fromCity - 1)));
    }


    private void initCities() {
        for (int i = 0; i < utilCityList.size(); i++) {
            cities.add(createCityObject(utilCityList.get(i), i + 1));
        }
    }

    private City createCityObject(Button button, int id) {
        return new City(id, getRelativeXPosition(button), getRelativeYPosition(button));
    }

    public static int getRelativeXPosition(Button button) {
        return (int) ((button.getLayoutX() / DEFAULT_MAP_WIDTH) * 100);
    }

    public static int getRelativeYPosition(Button button) {
        return (int) ((button.getLayoutY() / DEFAULT_MAP_HEIGHT) * 100);
    }

    public CitiesGameMap getMap() {
        return map;
    }
}
