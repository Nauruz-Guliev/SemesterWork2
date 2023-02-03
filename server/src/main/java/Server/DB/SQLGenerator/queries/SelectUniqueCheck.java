package Server.DB.SQLGenerator.queries;

import java.lang.reflect.Field;
import java.util.List;

public class SelectUniqueCheck extends Query {

    private List<Field> uniqueFields;

    public List<Field> getUniqueFields() {
        return uniqueFields;
    }

    public SelectUniqueCheck(String query, List<Field> uniqueFields) {
        super(query);
        this.uniqueFields = uniqueFields;
    }
}
