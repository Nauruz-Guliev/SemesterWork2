package Protocol.Message.RequestValues;

public record UserLoginForm(String email, String password) implements RequestValue {

    @Override
    public String toString() {
        return "UserLoginForm{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
