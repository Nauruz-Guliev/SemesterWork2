package Server.services.Impl;

import Protocol.Message.models.CitiesMap;
import Protocol.Message.models.City;
import Protocol.Message.models.Way;
import Server.models.GameDB;
import Server.models.UserDB;

import java.util.*;

public class MapCitiesGenerator {

    private static final int startArmy = 10;


    public static void generateCitiesMap(GameDB game, int countOfCities, List<UserDB> users) {
        game.setCitiesMap(generateCitiesMap(countOfCities));
        game.setCitiesArmies(generateCitiesArmies(game.getCitiesMap()));
        game.setUsersCities(generateUsersCities(game.getCitiesMap(), users));
    }

    private static CitiesMap generateCitiesMap(int countOfCities) {
        City city1 = new City(1, 25, 2);
        City city2 = new City(2, 5, 42);
        City city3 = new City(3, 25, 82);
        City city4 = new City(4, 65, 82);
        City city5 = new City(5, 85, 42);
        City city6 = new City(6, 65, 2);

        Set<City> cities = new HashSet<>();
        cities.add(city1);
        cities.add(city2);
        cities.add(city3);
        cities.add(city4);
        cities.add(city5);
        cities.add(city6);

        Set<Way> ways = new HashSet<>();
        ways.add(new Way(city1, city2));
        ways.add(new Way(city2, city3));
        ways.add(new Way(city3, city4));
        ways.add(new Way(city4, city5));
        ways.add(new Way(city5, city6));
        ways.add(new Way(city6, city1));
        ways.add(new Way(city1, city3));
        ways.add(new Way(city3, city6));
        ways.add(new Way(city4, city6));

        return new CitiesMap(cities, ways);
    }

    private static Map<City, Integer> generateCitiesArmies(CitiesMap citiesMap) {
        Map<City, Integer> citiesArmies = new HashMap<>();

        for (City city : citiesMap.cities()) {
            citiesArmies.put(city, startArmy);
        }

        return citiesArmies;
    }

    private static Map<City, UserDB> generateUsersCities(CitiesMap citiesMap, List<UserDB> users) {
        Map<City, UserDB> usersCities = new HashMap<>();

        for (City city : citiesMap.cities()) {
            if (city.number() == 1) {
                usersCities.put(city, users.get(0));
            } else if (city.number() == 4) {
                usersCities.put(city, users.get(1));
            } else {
                usersCities.put(city, null);
            }

        }

        return usersCities;
    }
}
