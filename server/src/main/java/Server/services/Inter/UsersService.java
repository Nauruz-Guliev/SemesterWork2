package Server.services.Inter;

import Protocol.Message.RequestValues.UserLoginForm;
import Protocol.Message.RequestValues.UserRegistrationForm;
import Protocol.Message.RequestValues.UserUpdateForm;
import Server.DB.exceptions.DBException;
import Server.DB.exceptions.NotFoundException;
import Server.DB.exceptions.NotUniqueException;
import Server.DB.exceptions.NullException;
import Server.models.UserDB;
import Server.models.validators.ValidatorException;
import Server.services.ServiceWithDB;
import Server.services.exceptions.ServiceException;
import Server.services.exceptions.UserAlreadyLoginException;

public interface UsersService extends ServiceWithDB {

    void register(UserRegistrationForm user) throws DBException, ServiceException, NotUniqueException, NullException, ValidatorException;

    UserDB login(UserLoginForm userDB) throws ServiceException, DBException, NullException, NotFoundException, UserAlreadyLoginException, ValidatorException;

    void logout(UserDB userDB) throws ValidatorException;

    void update(UserUpdateForm form, UserDB user) throws DBException, ServiceException, NotUniqueException, NotFoundException, NullException, ValidatorException;

}
