package TestClient;

import Protocol.Message.models.City;
import Protocol.Message.models.Way;

import java.util.HashSet;
import java.util.Set;

public class Test2 {
    public static void main(String[] args) {

        Set<Way> set = new HashSet<>();
        set.add(null);
        System.out.println(set.size());
    }
}
