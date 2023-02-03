package utils;

import exceptions.ClientInputException;

import java.awt.*;

public class Converter {

    public static Color convertColor(javafx.scene.paint.Color color) {
        return new Color(
                convertColorNumber(color.getRed()),
                convertColorNumber(color.getGreen()),
                convertColorNumber(color.getBlue())
        );
    }

    public static javafx.scene.paint.Color convertColor(Color color) {
        return new javafx.scene.paint.Color(
                color.getRed()/255.0,
                color.getGreen()/255.0,
                color.getBlue()/255.0, 1
        );
    }

    public static int convertToInt(String text) throws ClientInputException {
        if (text.isEmpty() || text.isBlank()) {
            throw new ClientInputException("Field can not be empty");
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            throw new ClientInputException("Field must be numerical");
        }
    }

    public static String convertToHex(javafx.scene.paint.Color color) {
        return String.format("#%02x%02x%02x", convertColorNumber(color.getRed()), convertColorNumber(color.getGreen()), convertColorNumber(color.getBlue()));
    }

    private static int convertColorNumber(Double num) {
        return (int) (num * 255);
    }
}
