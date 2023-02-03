package Server.DB.SQLGenerator.queries;


public abstract class Query {

    private String query;

    public String getQuery() {
        return query;
    }

    public Query(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return query;
    }
}
