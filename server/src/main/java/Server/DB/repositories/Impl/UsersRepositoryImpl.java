package Server.DB.repositories.Impl;

import Server.DB.exceptions.DBException;
import Server.DB.exceptions.NotFoundException;
import Server.DB.exceptions.NullException;
import Server.models.UserDB;
import Server.DB.repositories.Inter.UsersRepository;
import Server.DB.repositories.RepositoryImpl;
import Server.services.exceptions.ServiceException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersRepositoryImpl extends RepositoryImpl implements UsersRepository {

    public UsersRepositoryImpl(DataSource dataSource) {
        super(dataSource);
    }


    private static final String SELECT_ALL_FROM_USERS_BY_EMAIL_AND_PASSWORD =
            """
            select * 
            from users u 
            where u.email = ? 
              and u.password_hash = ?
            """;
    @Override
    public void selectUserByEmailAndPasswordHash(UserDB user) throws NullException, NotFoundException, DBException, ServiceException {
        if (user.getEmail() == null) {
            throw new NullException("email");
        }
        if (user.getPasswordHash() == null) {
            throw new NullException("password");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_FROM_USERS_BY_EMAIL_AND_PASSWORD)) {

            int i = 1;
            preparedStatement.setString(i++, user.getEmail());
            preparedStatement.setString(i++, user.getPasswordHash());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    objectInsertion(user, resultSet);
                } else {
                    throw new NotFoundException();
                }
                if (resultSet.next()) {
                    throw new DBException("Аномалия: было найдено несколько пользователей при аутентификации. ");
                }
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }
}
