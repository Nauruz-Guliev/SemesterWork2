package Server.services;

import Server.DB.repositories.Impl.UsersRepositoryImpl;
import Server.DB.repositories.RepositoryImpl;
import Server.app.ServerApp;
import Server.models.validators.*;
import Server.services.Impl.RoomsServiceImpl;
import Server.services.Impl.UsersServiceImpl;
import Server.services.Inter.RoomsService;
import Server.services.Inter.UsersService;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

public class ServicesToolKit {


    private final ServiceWithDB service;
    private final UsersService usersService;
    private final RoomsService roomsService;

    public ServicesToolKit(DataSource dataSource) {

        service = new ServiceWithDBImpl(new RepositoryImpl(dataSource));


        Properties properties = new Properties();
        try {
            properties.load(ServerApp.class.getResourceAsStream("/app.properties"));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        EmailValidator emailValidator = new EmailValidator(properties.getProperty("email.regexp"));
        PasswordValidator passwordValidator = new PasswordValidator(
                properties.getProperty("password.regexp"),
                Integer.parseInt(properties.getProperty("password.minLength")),
                Integer.parseInt(properties.getProperty("password.maxLength"))
        );
        NicknameValidator nicknameValidator = new NicknameValidator(
                properties.getProperty("nickname.regexp"),
                Integer.parseInt(properties.getProperty("nickname.minLength")),
                Integer.parseInt(properties.getProperty("nickname.maxLength"))
        );

        usersService = new UsersServiceImpl(
                new UsersRepositoryImpl(dataSource),
                emailValidator,
                passwordValidator,
                nicknameValidator);

        GameInitValidator gameInitValidator = new GameInitValidator(
                Integer.parseInt(properties.getProperty("gameInit.minArmySpeed")),
                Integer.parseInt(properties.getProperty("gameInit.maxArmySpeed")),
                Integer.parseInt(properties.getProperty("gameInit.minArmyGrowthRate")),
                Integer.parseInt(properties.getProperty("gameInit.maxArmyGrowthRate")),
                Integer.parseInt(properties.getProperty("gameInit.minCountOfCities")),
                Integer.parseInt(properties.getProperty("gameInit.maxCountOfCities"))
        );

        RoomInitValidator roomInitValidator = new RoomInitValidator(
                Integer.parseInt(properties.getProperty("roomInit.minCountOfPlayers")),
                Integer.parseInt(properties.getProperty("roomInit.maxCountOfPlayers")),
                gameInitValidator
        );

        roomsService = new RoomsServiceImpl(roomInitValidator);
    }

    public ServiceWithDB getMainService() {
        return service;
    }

    public UsersService getUsersService() {
        return usersService;
    }

    public RoomsService getRoomService() {
        return roomsService;
    }

}
