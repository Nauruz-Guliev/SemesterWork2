package Server.models.validators;

import Protocol.Message.RequestValues.GameInitializationForm;

public record GameInitValidator(int minArmySpeed, int maxArmySpeed,
                                int minArmyGrowthRate, int maxArmyGrowthRate,
                                int minCountOfCities, int maxCountOfCities) implements Validator<GameInitializationForm>{


    @Override
    public void check(GameInitializationForm object) throws IllegalArgumentException, ValidatorException {
        if (object == null) {
            throw new ValidatorException("Game initialization form is empty. ");
        }

        if (object.armySpeed() < minArmySpeed || object.armySpeed() > maxArmySpeed) {
            throw new ValidatorException("Minimum " + minArmySpeed + " army speed. Maximum " + maxArmySpeed + " army speed. ");
        }

        if (object.countOfCities() < minCountOfCities || object.countOfCities() > maxCountOfCities) {
            throw new ValidatorException("Minimum " + minCountOfCities + " cities. Maximum " + maxCountOfCities + " cities. ");
        }

        if (object.armyGrowthRate() < minArmyGrowthRate || object.armyGrowthRate() > maxArmyGrowthRate) {
            throw new ValidatorException("Minimum " + minArmyGrowthRate + " army growth rate. Maximum " + maxArmyGrowthRate + " army growth rate. ");
        }

    }
}
