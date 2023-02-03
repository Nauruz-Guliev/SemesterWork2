package Protocol.Message.RequestValues;

public record UserRegistrationForm(String email, String password, String nickname) implements RequestValue {

    @Override
    public String toString() {
        return "UserRegistrationForm{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
