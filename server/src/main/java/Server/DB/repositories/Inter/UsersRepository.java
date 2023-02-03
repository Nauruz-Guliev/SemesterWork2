package Server.DB.repositories.Inter;

import Server.DB.exceptions.DBException;
import Server.DB.exceptions.NotFoundException;
import Server.DB.exceptions.NullException;
import Server.models.UserDB;
import Server.DB.repositories.Repository;
import Server.services.exceptions.ServiceException;

public interface UsersRepository extends Repository {

    void selectUserByEmailAndPasswordHash(UserDB user) throws NullException, NotFoundException, DBException, ServiceException;
}
