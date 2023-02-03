package Server.DB.repositories;


import Server.DB.SQLGenerator.SQLAnnotations.*;
import Server.DB.SQLGenerator.SQLGenerator;
import Server.DB.SQLGenerator.queries.*;
import Server.DB.exceptions.*;
import Server.services.exceptions.ServiceException;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RepositoryImpl implements Repository {

    protected DataSource dataSource;

    public RepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public final void insert(Object object) throws DBException, NullException, ServiceException, NotUniqueException {
        objectCheck(object);
        nullCheck(object);
        uniqueCheck(object, Optional.empty());

        InsertQuery insertQuery;
        try {
            insertQuery = SQLGenerator.insert(object.getClass());
        } catch (SQLGeneratorException e) {
            throw new DBException(e);
        }
        String query = insertQuery.getQuery();
        List<Field> fields = insertQuery.getFields();
        Optional<Field> PKField = insertQuery.getPKField();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            statementInsertion(object, fields, preparedStatement);


            if (PKField.isPresent()) {
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    setValueTo(object, PKField.get(), resultSet.getLong(PKField.get().getAnnotation(Column.class).name()));
                } else {
                    throw new DBException("Аномалия: вставка " + object.getClass().getName() + " не произошла. ");
                }
                if (resultSet.next()) {
                    throw new DBException("Аномалия: произошло больше 1 вставки " + object.getClass().getName() + ". ");
                }
            } else {
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows == 0) {
                    throw new DBException("Аномалия: вставка " + object.getClass().getName() + " не произошла. ");
                } else if (affectedRows > 1) {
                    throw new DBException("Аномалия: произошло больше 1 вставки " + object.getClass().getName() + ". ");
                }
            }

        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    @Override
    public final void update(Object object, Field uniqueField) throws NotFoundException, DBException, NullException, ServiceException, NotUniqueException {
        allChecks(object, uniqueField);

        UpdateQuery updateQuery;
        try {
            updateQuery = SQLGenerator.update(object.getClass(), uniqueField);
        } catch (SQLGeneratorException e) {
            throw new DBException(e);
        }
        String query = updateQuery.getQuery();
        List<Field> fields = updateQuery.getUniqueFields();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            statementInsertion(object, fields, preparedStatement);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new NotFoundException();
            }
            if (affectedRows != 1) {
                throw new DBException("Аномалия: изменилось " + affectedRows + " строк при изменении " + object.getClass().getName() + ". ");
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    @Override
    public final void delete(Object object, Field uniqueField) throws NotFoundException, DBException, ServiceException, NullException {
        allParamChecks(object, uniqueField);

        DeleteQuery deleteQuery;
        try {
            deleteQuery = SQLGenerator.delete(object.getClass(), uniqueField);
        } catch (SQLGeneratorException e) {
            throw new DBException(e);
        }
        String query = deleteQuery.getQuery();
        List<Field> fieldsId = deleteQuery.getUniqueFields();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            statementInsertion(object, fieldsId, preparedStatement);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new NotFoundException();
            }
            if (affectedRows != 1) {
                throw new DBException("Аномалия: изменилось " + affectedRows + " строк при удалении " + object.getClass().getName() + ". ");
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    @Override
    public final void selectByUniqueField(Object object, Field uniqueField) throws NotFoundException, DBException, ServiceException, NullException {
        allParamChecks(object, uniqueField);

        SelectByIdQuery selectByIdQuery;
        try {
            selectByIdQuery = SQLGenerator.selectByUniqueField(object.getClass(), uniqueField);
        } catch (SQLGeneratorException e) {
            throw new DBException(e);
        }
        String query = selectByIdQuery.getQuery();
        List<Field> fieldsId = selectByIdQuery.getUniqueFields();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            statementInsertion(object, fieldsId, preparedStatement);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    objectInsertion(object, resultSet);
                } else {
                    throw new NotFoundException();
                }
                if (resultSet.next()) {
                    throw new DBException("Аномалия: было найдено несколько " + object.getClass().getName() + ". ");
                }
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    //-------------------------------------------------------


    /** Выполняет все существующие проверки */
    private void allChecks(Object object, Field uniqueField) throws ServiceException, NullException, DBException, NotUniqueException {
        objectCheck(object);
        uniqueFieldCheck(object, uniqueField);
        nullCheck(object);
        uniqueCheck(object, Optional.of(uniqueField));
    }

    /** Выполняет все существующие для входных параметров проверки*/
    private void allParamChecks(Object object, Field uniqueField) throws ServiceException, NullException {
        objectCheck(object);
        uniqueFieldCheck(object, uniqueField);
    }

    /** Проверка, что объект не null */
    private void objectCheck(Object object) throws ServiceException {
        if (object == null) {
            throw new ServiceException("Объект не передан. ");
        }
    }

    /** Проверка уникального поля, что оно не null
                                 и что его содержимое всей его группы не null */
    private void uniqueFieldCheck(Object object, Field uniqueField) throws ServiceException, NullException {
        objectCheck(object);
        if (uniqueField == null) {
            throw new ServiceException("Поле не передано. ");
        }
        if (!uniqueField.isAnnotationPresent(Unique.class) && !uniqueField.isAnnotationPresent(PK.class)) {
            throw new ServiceException("Поле " + uniqueField.getName() + " у класса " + object.getClass().getName() + " не помечено аннотацией " + Unique.class.getName() + " или " + PK.class.getName() + ". ");
        }

        if (uniqueField.isAnnotationPresent(Unique.class)) {
            for (Field field : Arrays.stream(object.getClass().getDeclaredFields())
                    .filter(f -> f.getAnnotation(Column.class) != null
                            && f.getAnnotation(Unique.class) != null
                            && f.getAnnotation(Unique.class).group() == uniqueField.getAnnotation(Unique.class).group())
                    .toList()) {
                if (getValueFrom(object, field) == null) {
                    throw new NullException(field.getName());
                }
            }
        } else {
            if (getValueFrom(object, uniqueField) == null) {
                throw new NullException(uniqueField.getName());
            }
        }
    }

    /** Проверка, что поля, помеченные Unique или NotNull, не должны быть пустыми */
    private void nullCheck(Object object) throws NullException, ServiceException {
        for (Field field : Arrays.stream(object.getClass().getDeclaredFields())
                .filter(f -> f.getAnnotation(Column.class) != null
                        && (f.getAnnotation(NotNull.class) != null
                        || f.getAnnotation(Unique.class) != null))
                .collect(Collectors.toList())) {
            if (getValueFrom(object, field) == null) {
                throw new NullException(field.getName());
            }
        }
    }

    /** Проверка, что в таблице сохранится уникальность при добавлении/изменении объекта */
    private void uniqueCheck(Object object, Optional<Field> uniqueField) throws NotUniqueException, DBException, ServiceException {
        SelectUniqueCheck selectUniqueCheck;
        try {
            Optional<SelectUniqueCheck> selectUniqueCheck2 = SQLGenerator.selectUniqueCheck(object.getClass(), uniqueField);
            if (selectUniqueCheck2.isPresent()) {
                selectUniqueCheck = selectUniqueCheck2.get();
            } else {
                return;
            }
        } catch (SQLGeneratorException e) {
            throw new DBException(e);
        }
        String query = selectUniqueCheck.getQuery();
        List<Field> fields = selectUniqueCheck.getUniqueFields();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            statementInsertion(object, fields, preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                for (Field field : fields) {
                    if (getValueFrom(object,field).equals(resultSet.getObject(field.getAnnotation(Column.class).name()))) {
                        throw new NotUniqueException(field.getName());
                    }
                }
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }



    /** Заполнение объекта данными из бд */
    protected void objectInsertion(Object object, ResultSet resultSet) throws ServiceException, SQLException {
        for (Field field : Arrays.stream(object.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Column.class))
                .collect(Collectors.toList())) {

            if (field.getType().isEnum()) {
                boolean flag = true;
                for (Method method : Arrays.stream(object.getClass().getMethods())
                        .filter(m -> m.isAnnotationPresent(EnumSetter.class))
                        .collect(Collectors.toList())) {
                    if (!flag && method.getAnnotation(EnumSetter.class).fieldName().equals(field.getName())) {
                        throw new ServiceException("Для модели " + object.getClass().getName() + " найдено несколько " + EnumSetter.class.getName() + ". ");
                    }
                    if (method.getAnnotation(EnumSetter.class).fieldName().equals(field.getName())) {
                        String value = resultSet.getString(field.getAnnotation(Column.class).name());
                        try {
                            method.invoke(object, value);
                        } catch (InvocationTargetException | IllegalAccessException e) {
                            throw new ServiceException("Неизвестная для модели " + object.getClass().getName() + " enum " + value);
                        }
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    throw new ServiceException("Для модели" + object.getClass().getName() + " не найден " + EnumSetter.class.getName() + " на поле " + field.getName() + ". ");
                }
                continue;
            }

            String columnName = field.getAnnotation(Column.class).name();
            switch (field.getType().getSimpleName()) {
                case "Integer" -> setValueTo(object, field, resultSet.getInt(columnName));
                case "Long" -> setValueTo(object, field, resultSet.getLong(columnName));
                case "Float" -> setValueTo(object, field, resultSet.getFloat(columnName));
                case "Double" -> setValueTo(object, field, resultSet.getDouble(columnName));
                case "Boolean" -> setValueTo(object, field, resultSet.getBoolean(columnName));
                case "String" -> setValueTo(object, field, resultSet.getString(columnName));
                case "Date" -> setValueTo(object, field, new Date(resultSet.getTimestamp(columnName).getTime()));
                default -> throw new ServiceException("Неизвестный тип " + field.getType().getSimpleName() + " у класса " + object.getClass().getName());
            }
        }
    }

    /** Заполнение запроса информацией из объекта */
    protected void statementInsertion(Object object, List<Field> fields, PreparedStatement preparedStatement) throws ServiceException, SQLException {
        int i = 1;
        for (Field field : fields) {
            Object value = getValueFrom(object, field);

            if (value == null) {
                preparedStatement.setNull(i++, 0);
                continue;
            }

            if (field.getType().isEnum()) {
                preparedStatement.setString(i++, value.toString());
                continue;
            }

            switch (field.getType().getSimpleName()) {
                case "Integer" -> preparedStatement.setInt(i++, (Integer) value);
                case "Long" -> preparedStatement.setLong(i++, (Long) value);
                case "Float" -> preparedStatement.setFloat(i++, (Float) value);
                case "Double" -> preparedStatement.setDouble(i++, (Double) value);
                case "Boolean" -> preparedStatement.setBoolean(i++, (Boolean) value);
                case "String" -> preparedStatement.setString(i++, (String) value);
                case "Date" -> preparedStatement.setTimestamp(i++, new Timestamp(((Date) value).getTime()));
                default -> throw new ServiceException("Неизвестный тип " + field.getType().getSimpleName() + " у класса " + object.getClass().getName());
            }
        }
    }

    /** Получение значения из объекта по полю */
    protected Object getValueFrom(Object object, Field field) throws ServiceException {
        Object value;
        if (field.canAccess(object)) {
            try {
                value = field.get(object);
            } catch (IllegalAccessException e) {
                throw new ServiceException(e);
            }
        } else {
            field.setAccessible(true);
            try {
                value = field.get(object);
            } catch (IllegalAccessException e) {
                throw new ServiceException(e);
            }
            field.setAccessible(false);
        }
        return value;
    }

    /** Установка значения из объекта по полю */
    protected void setValueTo(Object object, Field field, Object value) throws ServiceException {
        if (field.canAccess(object)) {
            try {
                field.set(object, value);
            } catch (IllegalAccessException e) {
                throw new ServiceException(e);
            }
        } else {
            field.setAccessible(true);
            try {
                field.set(object, value);
            } catch (IllegalAccessException e) {
                throw new ServiceException(e);
            }
            field.setAccessible(false);
        }
    }

}
