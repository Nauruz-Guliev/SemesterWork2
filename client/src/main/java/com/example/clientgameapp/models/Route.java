package com.example.clientgameapp.models;

import javafx.scene.control.Button;

import java.util.Objects;

public record Route(Button fromCity, Button toCity) {

    @Override
    public String toString() {
        return "Route{" +
                "fromCityX=" + fromCity.getLayoutX() +
                "fromCityY=" + fromCity.getLayoutY() +
                ", toCityX=" + toCity.getLayoutX() +
                ", toCityY=" + toCity.getLayoutY() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route route)) return false;
        return Math.abs(fromCity.getLayoutY() - route.fromCity.getLayoutY()) < 0.000001 &&
                (Math.abs(fromCity.getLayoutX() - route.fromCity.getLayoutX()) < 0.000001) &&
                (Math.abs(toCity.getLayoutX() - route.toCity.getLayoutX()) < 0.000001) &&
                (Math.abs(toCity.getLayoutY() - route.toCity.getLayoutY()) < 0.000001);

    }

    @Override
    public int hashCode() {
        return Objects.hash(fromCity, toCity);
    }
}
