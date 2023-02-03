package Protocol.Message.RequestValues;

/**
 * @param countOfCities  кол-во городов
 * @param armySpeed      скорость армии, когда она идет от одного города к другому. усл.ед./с.
 * @param armyGrowthRate скорость роста армии игрока в городе
 */
public record GameInitializationForm(int countOfCities,
                                     int armySpeed,
                                     int armyGrowthRate) implements RequestValue {

    @Override
    public String toString() {
        return "GameInitializationForm{" +
                "countOfCities=" + countOfCities +
                ", armySpeed=" + armySpeed +
                ", armyGrowthRate=" + armyGrowthRate +
                '}';
    }
}
