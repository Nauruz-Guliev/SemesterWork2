package Server.models;


import Protocol.Message.ResponseValues.User;
import Protocol.Message.RequestValues.UserLoginForm;
import Protocol.Message.RequestValues.UserRegistrationForm;
import Server.DB.SQLGenerator.SQLAnnotations.*;

import java.util.Objects;

@Table(name = "users")
public class UserDB {

    @PK
    @Column(name = "id")
    private Long id;

    @Unique
    @Column(name = "email")
    private String email;

    private String password;
    @NotNull
    @Column(name = "password_hash")
    private String passwordHash;

    @Unique
    @Column(name = "nickname")
    private String nickname;

    public UserDB() {
    }

    public UserDB(UserRegistrationForm form) {
        this.email = form.email();
        this.password = form.password();
        this.nickname = form.nickname();
    }

    public UserDB(UserLoginForm form) {
        this.email = form.email();
        this.password = form.password();
    }

    public User toUser() {
        return new User(getNickname());
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    @Override
    public String toString() {
        return "UserDB{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDB userDB = (UserDB) o;

        return Objects.equals(id, userDB.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
