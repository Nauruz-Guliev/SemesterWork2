package Server.models.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record NicknameValidator(String regex, int minLength, int maxLength) implements Validator<String> {

    @Override
    public void check(String object) throws ValidatorException {
        if (object == null || object.equals("")) {
            throw new ValidatorException("Nickname is empty. ");
        }

        if (object.length() < minLength) {
            throw new ValidatorException("The nickname must contain at least " + minLength + " characters.");
        }

        if (object.length() > maxLength) {
            throw new ValidatorException("The nickname must contain no more than " + maxLength + " characters.");
        }

        Pattern pattern2 = Pattern.compile(regex);
        Matcher matcher2 = pattern2.matcher(object);
        if (!matcher2.find()) {
            throw new ValidatorException(
                    """
                            Allowed characters for the nickname:
                                - Capital Latin letters: from A to Z
                                - Lowercase Latin letters: from a to z
                                - Numbers from 0 to 9
                            """);
        }
    }
}
