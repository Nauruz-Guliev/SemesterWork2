package Protocol.Message.ResponseValues;

import Protocol.Message.RequestValues.RequestValue;
import Protocol.Message.models.CitiesMap;
import Protocol.Message.models.City;

import java.awt.*;
import java.util.Date;
import java.util.Map;

public record Game(CitiesMap citiesMap,
                   Date startTime,
                   Map<City, Integer> citiesArmies,
                   Map<City, User> usersCities,
                   Map<User, Color> usersColor,

                   int armySpeed,
                   int armyGrowthRate) implements ResponseValue, RequestValue {

    @Override
    public String toString() {
        return "Game{" +
                "citiesMap=" + citiesMap +
                ", startTime=" + startTime +
                ", citiesArmies=" + citiesArmies +
                ", usersCities=" + usersCities +
                ", usersColor=" + usersColor +
                ", armySpeed=" + armySpeed +
                ", armyGrowthRate=" + armyGrowthRate +
                '}';
    }
}
