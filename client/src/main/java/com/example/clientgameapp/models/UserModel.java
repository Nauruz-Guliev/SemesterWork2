package com.example.clientgameapp.models;

import Protocol.Message.ResponseValues.User;

import java.awt.*;
import java.util.Objects;

public class UserModel {
    private final boolean userStatus;
    private final Color userColor;
    private final int index;
    private final User user;

    @Override
    public String toString() {
        return "UserModel{" +
                "userStatus=" + userStatus +
                ", userColor=" + userColor +
                ", index=" + index +
                ", user=" + user +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserModel userModel)) return false;
        return getUserStatus() == userModel.getUserStatus() && getIndex() == userModel.getIndex() && Objects.equals(getUserColor(), userModel.getUserColor()) && Objects.equals(getUser(), userModel.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserStatus(), getUserColor(), getIndex(), getUser());
    }

    public UserModel(Boolean userStatus, Color userColor, int index, User user) {
        this.userStatus = userStatus;
        this.userColor = userColor;
        this.index = index;
        this.user = user;
    }



    public User getUser() {
        return user;
    }

    public Boolean getUserStatus() {
        return userStatus;
    }

    public Color getUserColor() {
        return userColor;
    }

    public int getIndex() {
        return index;
    }

}
