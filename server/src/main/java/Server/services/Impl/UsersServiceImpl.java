package Server.services.Impl;


import Protocol.Message.RequestValues.UserLoginForm;
import Protocol.Message.RequestValues.UserRegistrationForm;
import Protocol.Message.RequestValues.UserUpdateForm;
import Server.DB.exceptions.DBException;
import Server.DB.exceptions.NotFoundException;
import Server.DB.exceptions.NotUniqueException;
import Server.DB.exceptions.NullException;
import Server.models.UserDB;
import Server.models.encryptors.PasswordEncryptor;
import Server.models.validators.EmailValidator;
import Server.models.validators.NicknameValidator;
import Server.models.validators.PasswordValidator;
import Server.models.validators.ValidatorException;
import Server.DB.repositories.Inter.UsersRepository;
import Server.services.Inter.UsersService;
import Server.services.exceptions.ServiceException;
import Server.services.ServiceWithDBImpl;
import Server.services.exceptions.UserAlreadyLoginException;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UsersServiceImpl extends ServiceWithDBImpl implements UsersService {

    private final static String userIdFieldName = "id";

    private final UsersRepository usersRepository;
    private final Set<UserDB> activeUsers;

    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;
    private final NicknameValidator nicknameValidator;
    private final PasswordEncryptor passwordEncryptor;


    public UsersServiceImpl(UsersRepository usersRepository,
                            EmailValidator emailValidator,
                            PasswordValidator passwordValidator,
                            NicknameValidator nicknameValidator) {
        super(usersRepository);
        this.usersRepository = usersRepository;
        this.emailValidator = emailValidator;
        this.passwordValidator = passwordValidator;
        this.nicknameValidator = nicknameValidator;
        this.passwordEncryptor = new PasswordEncryptor();

        this.activeUsers = new HashSet<>();
    }


    private final Lock lock = new ReentrantLock();

    @Override
    public void register(UserRegistrationForm form) throws DBException, ServiceException, NotUniqueException, NullException, ValidatorException {
        unNullCheck(form);

        emailValidator.check(form.email());
        passwordValidator.check(form.password());
        nicknameValidator.check(form.nickname());

        UserDB user = encryptPassword(new UserDB(form));
        super.add(user);
    }

    @Override
    public UserDB login(UserLoginForm form) throws ServiceException, DBException, NullException, NotFoundException, UserAlreadyLoginException, ValidatorException {
        unNullCheck(form);

        UserDB user = encryptPassword(new UserDB(form));
        this.usersRepository.selectUserByEmailAndPasswordHash(user);

        lock.lock();
        if (activeUsers.contains(user)) {
            throw new UserAlreadyLoginException();
        }
        activeUsers.add(user);
        lock.unlock();
        return user;
    }

    @Override
    public void logout(UserDB user) throws ValidatorException {
        unNullCheck(user);

        lock.lock();
        activeUsers.remove(user);
        lock.unlock();
    }


    @Override
    public void update(UserUpdateForm form, UserDB user) throws DBException, ServiceException, NotUniqueException, NotFoundException, NullException, ValidatorException {
        unNullCheck(form);
        unNullCheck(user);

        nicknameValidator.check(form.nickname());

        user.setNickname(form.nickname());
        super.change(user, userIdFieldName);
    }



    private UserDB encryptPassword(UserDB user) {
        user.setPasswordHash(passwordEncryptor.encrypt(user.getPassword()));
        user.setPassword(null);
        return user;
    }

    private void unNullCheck(Object object) throws ValidatorException {
        if (object == null) {
            throw new ValidatorException("Not null expected");
        }
    }
}
