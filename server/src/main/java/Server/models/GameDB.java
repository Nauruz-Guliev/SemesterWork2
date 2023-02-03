package Server.models;

import Protocol.Message.ResponseValues.Game;
import Protocol.Message.RequestValues.GameArmyStartMove;
import Protocol.Message.RequestValues.GameArmyEndMove;
import Protocol.Message.RequestValues.GameInitializationForm;
import Protocol.Message.ResponseValues.User;
import Protocol.Message.models.CitiesMap;
import Protocol.Message.models.City;
import Protocol.Message.models.Way;
import Server.app.UserConnectionThread;
import Server.models.validators.ValidatorException;
import Server.services.Impl.MapCitiesGenerator;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GameDB {

    private CitiesMap citiesMap;

    private Date startTime;

    private Map<UserDB, Color> usersColor;
    private Map<City, Integer> citiesArmies;
    private Map<City, UserDB> usersCities;

    //Скорость армии, когда она идет от одного города к другому. усл.ед./с.
    private final int armySpeed;

    //Скорость роста армии игрока в городе ед./с.
    private final int armyGrowthRate;

    private static final int waitingTimeS = 1;
    private static final int cityMaxSize = 99;


    public GameDB(GameInitializationForm form, List<UserDB> users, Map<UserDB, Color> usersColor) {
        MapCitiesGenerator.generateCitiesMap(this, form.countOfCities(), users);
        this.usersColor = usersColor;
        this.armySpeed = form.armySpeed();
        this.armyGrowthRate = form.armyGrowthRate();
    }

    public Game toGame() {
        Map<City, User> usersCities = new HashMap<>();
        for (City city : this.usersCities.keySet()) {
            UserDB user = this.usersCities.get(city);
            usersCities.put(city, user == null ? null :  user.toUser());
        }

        Map<User, Color> usersColor = new HashMap<>();
        for (UserDB user : this.usersColor.keySet()) {
            usersColor.put(user.toUser(), this.usersColor.get(user));
        }


        return new Game(
                citiesMap,
                startTime,
                citiesArmies,
                usersCities,
                usersColor,
                armySpeed,
                armyGrowthRate
        );
    }


    private boolean isGameInProcess = false;
    public void start() {
        isGameInProcess = true;

        this.startTime = new Date((new Date()).getTime() + waitingTimeS*1000);
        new ArmyAutoIncrement().start();
    }

    public boolean isGameInProcess() {
        return isGameInProcess;
    }

    public CitiesMap getCitiesMap() {
        return citiesMap;
    }

    public void setCitiesMap(CitiesMap citiesMap) {
        this.citiesMap = citiesMap;
    }

    public void setCitiesArmies(Map<City, Integer> citiesArmies) {
        this.citiesArmies = citiesArmies;
    }

    public void setUsersCities(Map<City, UserDB> usersCities) {
        this.usersCities = usersCities;
    }


    private final Lock lock = new ReentrantLock();

    private UserDB winner = null;
    public void disconnectUser(UserDB user, UserConnectionThread userConnectionThread) {
        lock.lock();

        Set<City> cities = usersCities.keySet();
        Iterator<City> i = cities.iterator();
        while (i.hasNext()) {
            City city = i.next();
            UserDB userDB = usersCities.get(city);
            if (userDB != null && userDB.equals(user)) {
                i.remove();
                userConnectionThread.moveArmyEnd(new GameArmyEndMove(
                        city,
                        null,
                        citiesArmies.get(city)
                ));
            }
        }

//        for (City city : usersCities.keySet()) {
//            if (usersCities.get(city).equals(user)) {
//                usersCities.remove(city);
//                userConnectionThread.moveArmyEnd(new GameArmyEndMove(
//                        city,
//                        null,
//                        citiesArmies.get(city)
//                ));
//            }
//        }
        lock.unlock();
    }

    public UserDB getWinner() {
        return winner;
    }

    public void moveArmy(GameArmyStartMove gameArmyStartMove, UserDB user, UserConnectionThread userConnectionThread) throws ValidatorException {
        lock.lock();
        if (new Date().getTime() < startTime.getTime()) {
            lock.unlock();
            throw new ValidatorException("Wait until start. ");
        }

        if (!citiesMap.ways().contains(gameArmyStartMove.way())) {
            lock.unlock();
            throw new ValidatorException("No way found. ");
        }
        City startCity = gameArmyStartMove.way().getStart();
        if (usersCities.get(startCity) == null || !usersCities.get(startCity).equals(user)) {
            lock.unlock();
            throw new ValidatorException("It's not your city. ");
        }
        if (citiesArmies.get(startCity) < gameArmyStartMove.armyCount()) {
            lock.unlock();
            throw new ValidatorException("Not enough army. ");
        }

        citiesArmies.replace(startCity, citiesArmies.get(startCity) - gameArmyStartMove.armyCount());
        lock.unlock();
        new ArmyMove(gameArmyStartMove, user, userConnectionThread, lock).start();
    }


    public boolean isEnd() {
        lock.lock();
        int countOfActiveUsers = new HashSet<>(usersCities.values()).size();
        if (countOfActiveUsers <= 2) {
            winner = usersCities.values().stream().toList().get(0);
            if (winner == null) {
                try {
                    winner = usersCities.values().stream().toList().get(1);
                } catch (Exception e){
                    winner = new UserDB();
                    winner.setNickname("Unknown");
                }
            }
            isGameInProcess = false;
            lock.unlock();
            return true;
        } else {
            lock.unlock();
            return false;
        }
    }

    @Override
    public String toString() {
        return "GameDB{" +
                "citiesMap=" + citiesMap +
                ", startTime=" + startTime +
                ", citiesArmies=" + citiesArmies +
                ", usersCities=" + usersCities +
                ", armySpeed=" + armySpeed +
                ", armyGrowthRate=" + armyGrowthRate +
                '}';
    }


    private class ArmyMove extends Thread {

        private GameArmyStartMove gameArmyStartMove;
        private final UserDB user;
        private final UserConnectionThread userConnectionThread;

        public ArmyMove(GameArmyStartMove gameArmyStartMove, UserDB user, UserConnectionThread userConnectionThread, Lock lock) {
            this.gameArmyStartMove = gameArmyStartMove;
            this.user = user;
            this.userConnectionThread = userConnectionThread;
        }

        @Override
        public void run() {
            Way way = gameArmyStartMove.way();

            for (Way iWay : citiesMap.ways()) {
                if (iWay.equals(way)) {
                    if (way.getStart().equals(iWay.getStart())) {
                        way = iWay;
                    } else {
                        way = new Way(iWay.getEnd(), iWay.getStart());
                    }
                    break;
                }
            }
            gameArmyStartMove = new GameArmyStartMove(
                    way,
                    gameArmyStartMove.armyCount()
            );
            userConnectionThread.moveArmyStart(gameArmyStartMove);
            try {
                Thread.sleep(1000 * ((long) gameArmyStartMove.way().getLength()) / armySpeed);
            } catch (InterruptedException ignored) {}


            City endCity = way.getEnd();

            lock.lock();
            if (usersCities.get(endCity) == null || !usersCities.get(endCity).equals(user)) {
                citiesArmies.replace(endCity, citiesArmies.get(endCity) - gameArmyStartMove.armyCount());
                if (citiesArmies.get(endCity) == 0) {
                    //армии самоуничтожились
                    userConnectionThread.moveArmyEnd(new GameArmyEndMove(endCity, null, citiesArmies.get(endCity) ));
                } else if (citiesArmies.get(endCity) < 0) {
                    //произошел захват города
                    citiesArmies.replace(endCity, Math.abs(citiesArmies.get(endCity)) );
                    usersCities.replace(endCity, user);
                    userConnectionThread.moveArmyEnd(new GameArmyEndMove(endCity, user.toUser(), citiesArmies.get(endCity) ));
                } else {
                    //захват не произошел
                    UserDB newUser = usersCities.get(endCity);
                    userConnectionThread.moveArmyEnd(new GameArmyEndMove(endCity, newUser == null ? null : newUser.toUser(), citiesArmies.get(endCity) ));
                }
            } else {
                //перевод армии в свой город
                citiesArmies.replace(endCity, citiesArmies.get(endCity) + gameArmyStartMove.armyCount());
                userConnectionThread.moveArmyEnd(new GameArmyEndMove(endCity, user.toUser(), citiesArmies.get(endCity) ));
            }
            lock.unlock();
        }
    }


    private class ArmyAutoIncrement extends Thread {

        @Override
        public void run() {
            try {
                Thread.sleep(startTime.getTime() - (new Date()).getTime() );
            } catch (InterruptedException ignored) {}

            while (isGameInProcess) {
                lock.lock();
                for (City city : citiesArmies.keySet()) {
                    if (usersCities.get(city) != null) {
                        citiesArmies.replace(city, Math.min(citiesArmies.get(city) + armyGrowthRate, cityMaxSize));
                    }
                }
                lock.unlock();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

}
