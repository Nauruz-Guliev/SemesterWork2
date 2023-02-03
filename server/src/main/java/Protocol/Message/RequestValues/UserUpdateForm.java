package Protocol.Message.RequestValues;

public record UserUpdateForm(String nickname) implements RequestValue {

    @Override
    public String toString() {
        return "UserDataUpdateForm{" +
                "nickname='" + nickname + '\'' +
                '}';
    }
}
