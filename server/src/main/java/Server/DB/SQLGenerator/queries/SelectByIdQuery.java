package Server.DB.SQLGenerator.queries;

import java.lang.reflect.Field;
import java.util.List;

public class SelectByIdQuery extends Query {

    private List<Field> uniqueFields;


    public List<Field> getUniqueFields() {
        return uniqueFields;
    }

    public SelectByIdQuery(String query, List<Field> uniqueFields) {
        super(query);
        this.uniqueFields = uniqueFields;
    }
}
