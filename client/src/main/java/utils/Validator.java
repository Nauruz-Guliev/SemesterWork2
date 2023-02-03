package utils;

import exceptions.ClientInputException;

public class Validator {
    public static boolean isValid(String text) throws ClientInputException {
        if (text.isEmpty() || text.isBlank()) {
            throw new ClientInputException("Must fill in all the fields");
        }
        return true;
    }

}
