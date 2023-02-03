package Server.DB.SQLGenerator;


import Server.DB.SQLGenerator.SQLAnnotations.Column;
import Server.DB.SQLGenerator.SQLAnnotations.PK;
import Server.DB.SQLGenerator.SQLAnnotations.Table;
import Server.DB.SQLGenerator.SQLAnnotations.Unique;
import Server.DB.SQLGenerator.queries.*;
import Server.DB.exceptions.SQLGeneratorException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class SQLGenerator {


    public static InsertQuery insert(Class tClass) throws SQLGeneratorException {
        classCheck(tClass);

        StringBuilder query = new StringBuilder();
        query.append("insert into ").append(getTableName(tClass));

        List<Field> fields = getColumnFields(tClass);
        Optional<Field> PKField = getPKField(tClass);
        PKField.ifPresent(fields::remove);

        List<Column> columns = getColumns(fields);

        //вставка столбцов в запрос
        query.append("(");
        appendRepeat(", ", query, columns, 0);
        query.append(") values(");
        appendRepeat("?, ", query, columns.size(), 1);
        query.append(")");

        //вставка returning id
        PKField.ifPresent(field -> query.append(" returning ").append(field.getAnnotation(Column.class).name()));

        return new InsertQuery(query.toString(), fields, PKField);
    }

    public static UpdateQuery update(Class tClass, Field uniqueField) throws SQLGeneratorException {
        allChecks(tClass, uniqueField);

        StringBuilder query = new StringBuilder();
        query.append("update ").append(getTableName(tClass)).append(" set ");

        List<Field> fields = getColumnFields(tClass);
        Optional<Field> PKField = getPKField(tClass);
        PKField.ifPresent(fields::remove);
        List<Field> uniqueFields = getUniqueColumnFields(tClass, uniqueField);

        appendRepeat(" = ?, ", query, getColumns(fields), 4);
        query.append(" where ");
        appendRepeat(" = ? and ", query, getColumns(uniqueFields), 4);

        fields.addAll(uniqueFields);
        return new UpdateQuery(query.toString(), fields);
    }

    public static DeleteQuery delete(Class tClass, Field uniqueField) throws SQLGeneratorException {
        allChecks(tClass, uniqueField);
        StringBuilder query = new StringBuilder();

        List<Field> uniqueFields = getUniqueColumnFields(tClass, uniqueField);

        query.append("delete from ").append(getTableName(tClass)).append(" where ");
        appendRepeat(" = ? and ", query, getColumns(uniqueFields), 4);

        return new DeleteQuery(query.toString(), uniqueFields);
    }

    public static SelectByIdQuery selectByUniqueField(Class tClass, Field uniqueField) throws SQLGeneratorException {
        allChecks(tClass, uniqueField);
        StringBuilder query = new StringBuilder();

        query.append("select * from ").append(getTableName(tClass)).append(" where ");

        List<Field> uniqueFields = getUniqueColumnFields(tClass, uniqueField);

        appendRepeat(" = ? and ", query, getColumns(uniqueFields), 4);

        return new SelectByIdQuery(query.toString(), uniqueFields);
    }

    public static Optional<SelectUniqueCheck> selectUniqueCheck(Class tClass, Optional<Field> uniqueField) throws SQLGeneratorException {
        if (uniqueField.isPresent()) {
            allChecks(tClass, uniqueField.get());
        } else {
            classCheck(tClass);
        }

        StringBuilder query = new StringBuilder();

        query.append("select * from ").append(getTableName(tClass)).append(" where (");;

        List<Field> allUniqueFields = getUniqueColumnFields(tClass);
        if (uniqueField.isPresent()) {
            allUniqueFields.removeAll(getUniqueColumnFields(tClass, uniqueField.get()));
        }

        Set<Integer> groupsIn = new HashSet<>();
        List<Field> uniqueFields = new ArrayList<>();
        for (Field field : allUniqueFields) {
            int group = field.getAnnotation(Unique.class).group();
            if (group == -1) {
                query.append(field.getAnnotation(Column.class).name()).append(" = ? or ");
                uniqueFields.add(field);
                continue;
            }
            if (groupsIn.contains(group)) {
                continue;
            }
            List<Field> uniqueFieldsInOneGroup = getUniqueColumnFields(tClass, field);
            query.append("(");
            appendRepeat(" = ? and ", query, getColumns(uniqueFieldsInOneGroup), 4);
            query.append(") or ");
            groupsIn.add(group);
            uniqueFields.addAll(uniqueFieldsInOneGroup);
        }
        if (!allUniqueFields.isEmpty()) {
            query.delete(query.length() - 4, query.length());
        }
        query.append(")");

        if (uniqueField.isPresent()) {
            List<Field> uniqueFieldGroup = getUniqueColumnFields(tClass, uniqueField.get());
            query.append(" and ");
            appendRepeat(" <> ? and ", query, getColumns(uniqueFieldGroup), 5);
            uniqueFields.addAll(uniqueFieldGroup);
        }

        if (allUniqueFields.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new SelectUniqueCheck(query.toString(), uniqueFields));
    }

    private static void appendRepeat(String syn, StringBuilder query, List<Column> from, int leaveLeft) {
        from.forEach(column -> query.append(column.name()).append(syn));
        query.delete(query.lastIndexOf(syn) + leaveLeft,query.lastIndexOf(syn) + syn.length());
    }

    private static void appendRepeat(String syn, StringBuilder query, int count, int leaveLeft) {
        query.append(syn.repeat(count));
        query.delete(query.lastIndexOf(syn) + leaveLeft,query.lastIndexOf(syn) + syn.length());
    }

    /** Возвращает имя таблицы **/
    private static String getTableName(Class tClass) throws SQLGeneratorException {
        Table table = (Table) tClass.getAnnotation(Table.class);
        if (table == null) {
            throw new SQLGeneratorException("У модели " + tClass.getName() + " не заполнено имя таблицы. ");
        }
        return table.name();
    }

    /** Возвращает все поля, помеченные аннотацией Column **/
    private static List<Field> getColumnFields(Class tClass) throws SQLGeneratorException {
        List<Field> fields = Arrays.stream(tClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .collect(Collectors.toList());

        if (fields.isEmpty()) {
            throw new SQLGeneratorException("У модели " + tClass.getName() + " не найдены столбцы. ");
        }

        return fields;
    }

    /** Для каждого поля возвращает аннотацию Column **/
    private static List<Column> getColumns(List<Field> fields) {
        return fields.stream().map(f -> f.getAnnotation(Column.class)).collect(Collectors.toList());
    }

    /** Возвращает все поля, помеченные аннотациями Column и Unique */
    private static List<Field> getUniqueColumnFields(Class tClass) throws SQLGeneratorException {
        return getColumnFields(tClass).stream()
                .filter(f -> f.isAnnotationPresent(Unique.class))
                .collect(Collectors.toList());
    }

    /** Возвращает все поля, помеченные аннотациями Column и Unique, с той же group() для Unique
     * или само поле, если оно помечено аннотацей PK */
    private static List<Field> getUniqueColumnFields(Class tClass, Field uniqueField) throws SQLGeneratorException {
        List<Field> uniqueFields = new ArrayList<>();

        if (uniqueField.isAnnotationPresent(PK.class)) {
            return Collections.singletonList(uniqueField);
        }

        int group = uniqueField.getAnnotation(Unique.class).group();
        if (group == -1) {
            uniqueFields.add(uniqueField);
        } else {
            for (Field field : getColumnFields(tClass).stream()
                    .filter(f -> f.isAnnotationPresent(Unique.class))
                    .collect(Collectors.toList())) {
                if (field.getAnnotation(Unique.class).group() == group) {
                    uniqueFields.add(field);
                }
            }
        }

        return uniqueFields;
    }

    /** Возвращает поле, помеченные аннотацией Column и PK */
    private static Optional<Field> getPKField(Class tClass) throws SQLGeneratorException {
        List<Field> pkFields = getColumnFields(tClass).stream()
                .filter(f -> f.isAnnotationPresent(PK.class))
                .collect(Collectors.toList());
        if (pkFields.isEmpty()) {
            return Optional.empty();
        } else if (pkFields.size() > 1) {
            throw new SQLGeneratorException("У класса" + tClass.getName() + " найдено несколько полей с аннотацией " + PK.class.getName() + ". ");
        } else {
            return Optional.of(pkFields.get(0));
        }
    }

    /** Выполняет все существующие проверки */
    private static void allChecks(Class tClass, Field uniqueField) throws SQLGeneratorException {
        classCheck(tClass);
        uniqueFieldCheck(tClass, uniqueField);
    }

    /** Проверка, что класс не null*/
    private static void classCheck(Class tClass) throws SQLGeneratorException {
        if (tClass == null) {
            throw new SQLGeneratorException("Класс не передан. ");
        }
    }

    /** Проверка, что уникальное поле не null
                , что уникальное поле принадлежит классу
                и что уникальное поле помечено аннотациями Column и (Unique или PK) */
    private static void uniqueFieldCheck(Class tClass, Field uniqueField) throws SQLGeneratorException {
        if (uniqueField == null) {
            throw new SQLGeneratorException("Поле не передано. ");
        }
        if (!List.of(tClass.getDeclaredFields()).contains(uniqueField) ) {
            throw new SQLGeneratorException("Поле " + uniqueField.getName() + " у класса " + tClass.getName() + " не найдено." );
        }
        if (!uniqueField.isAnnotationPresent(Column.class)) {
            throw new SQLGeneratorException("Поле " + uniqueField.getName() + " у класса " + tClass.getName() + " не помечено аннотацией " + Column.class.getName() + ". ");
        }
        if (!uniqueField.isAnnotationPresent(Unique.class) && !uniqueField.isAnnotationPresent(PK.class)) {
            throw new SQLGeneratorException("Поле " + uniqueField.getName() + " у класса " + tClass.getName() + " не помечено аннотацией " + Unique.class.getName() + " или " + PK.class.getName() + ". ");
        }
    }


}
