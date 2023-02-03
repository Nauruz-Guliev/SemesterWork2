package Protocol.Message.models;

import java.io.Serializable;

/**
 * @param x от 5 до 95 условных единиц
 * @param y от 5 до 95 условных единиц
 */
public record City(int number, int x, int y)   implements Serializable {

    @Override
    public String toString() {
        return "City{" +
                "number=" + number +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        City city = (City) o;

        return number == city.number;
    }

    @Override
    public int hashCode() {
        return number;
    }
}
