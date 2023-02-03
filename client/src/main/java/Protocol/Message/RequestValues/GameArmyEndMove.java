package Protocol.Message.RequestValues;

import Protocol.Message.ResponseValues.User;
import Protocol.Message.models.City;

public record GameArmyEndMove(City city,
                              User user,
                              int armyCount) implements RequestValue {


    @Override
    public String toString() {
        return "CityCapture{" +
                "city=" + city +
                ", user=" + user +
                ", armyCount=" + armyCount +
                '}';
    }
}
