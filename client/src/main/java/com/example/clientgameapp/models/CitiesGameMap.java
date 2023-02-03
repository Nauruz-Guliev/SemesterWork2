package com.example.clientgameapp.models;

import Protocol.Message.models.City;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public record CitiesGameMap(List<Route> routes, List<City> cities) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CitiesGameMap citiesGameMap)) return false;
        return Objects.equals(routes, citiesGameMap.routes) && Objects.equals(cities, citiesGameMap.cities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routes, cities);
    }
}
