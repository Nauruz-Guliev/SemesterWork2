package Server.models.validators;

import Protocol.Message.RequestValues.RoomInitializationForm;

public record RoomInitValidator(int minCountOfPlayers, int maxCountOfPlayers, GameInitValidator gameInitValidator) implements Validator<RoomInitializationForm> {

    @Override
    public void check(RoomInitializationForm object) throws IllegalArgumentException, ValidatorException {
        if (object == null) {
            throw new ValidatorException("Room initialization form is empty. ");
        }

        if (object.access() == null) {
            throw new ValidatorException("Set access on room. ");
        }

        if (object.playerColor() == null) {
            throw new ValidatorException("Choose your color in game. ");
        }

        if (object.maxCountOfPlayers() < minCountOfPlayers || object.maxCountOfPlayers() > maxCountOfPlayers) {
            throw new ValidatorException("Minimum " + minCountOfPlayers + " players. Maximum " + maxCountOfPlayers + " players. ");
        }

        gameInitValidator.check(object.gameInitializationForm());
    }
}
