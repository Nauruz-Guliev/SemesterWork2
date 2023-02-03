package Protocol.Message.ResponseValues;


public record User(String nickname) implements ResponseValue {

    @Override
    public String toString() {
        return "User{" +
                "nickname='" + nickname + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return nickname.equals(user.nickname);
    }

    @Override
    public int hashCode() {
        return nickname.hashCode();
    }
}
