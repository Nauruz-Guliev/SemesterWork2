package Protocol.Message.models;

import java.io.Serializable;
import java.util.Set;

public record CitiesMap(Set<City> cities, Set<Way> ways)  implements Serializable {

    @Override
    public String toString() {
        return "CitiesMap{" +
                "cities=" + cities +
                ", ways=" + ways +
                '}';
    }
}
