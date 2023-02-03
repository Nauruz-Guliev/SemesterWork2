package com.example.clientgameapp.controllers.game;

import Protocol.HighLevelMessageManager;
import Protocol.Message.Request;
import Protocol.Message.RequestValues.GameArmyEndMove;
import Protocol.Message.RequestValues.GameArmyStartMove;
import Protocol.Message.RequestValues.GameResults;
import Protocol.Message.Response;
import Protocol.Message.ResponseValues.Game;
import Protocol.Message.ResponseValues.ResponseError;
import Protocol.Message.ResponseValues.User;
import Protocol.Message.models.City;
import Protocol.Message.models.Way;
import Protocol.ProtocolVersionException;
import com.example.clientgameapp.DestinationsManager;
import com.example.clientgameapp.controllers.error.ErrorAlert;
import com.example.clientgameapp.models.CitiesGameMap;
import com.example.clientgameapp.models.Route;
import com.example.clientgameapp.storage.GameStorage;
import com.example.clientgameapp.storage.GlobalStorage;
import com.example.clientgameapp.storage.generator.MapGenerator;
import connection.ClientConnectionSingleton;
import exceptions.ClientConnectionException;
import exceptions.ServerException;
import javafx.animation.PathTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;
import utils.Converter;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import java.util.List;

public class GameController implements Initializable {
    @FXML
    public Button cityBtnFirst;

    @FXML
    public Button cityBtnSecond;

    @FXML
    public Button cityBtnThird;

    @FXML
    public Button cityBtnFourth;

    @FXML
    public Button cityBtnFifth;

    @FXML
    public Button cityBtnSixth;

    public Pane pane;
    private Map<Button, City> citiesMap;

    public Canvas canvas;

    private CitiesGameMap citiesGameMap;

    private GraphicsContext gc;
    private boolean isFirst = true;
    private Button fromButton;
    private boolean isFinished = true;

    private Color color;

    private int duration = 3;

    private boolean isInitialized = false;

    private double widthMargin;
    private List<Button> allAvailableCities;

    private ClientConnectionSingleton connection;
    private HighLevelMessageManager mManager;

    private GameStorage gameStorage;
    private Socket senderSocket;

    private Socket receiverSocket;

    private Game game;
    private int incrementRate = 2;


    private DestinationsManager destinationsManager;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            connection = ClientConnectionSingleton.getInstance();
            mManager = new HighLevelMessageManager();
            senderSocket = connection.getSocketSender();
            receiverSocket = connection.getSocketReceiver();
            destinationsManager = DestinationsManager.getInstance();
            gameStorage = GameStorage.getInstance();
            citiesMap = new HashMap<>();
            pane.requestFocus();
            if (!isInitialized) {
                initGame();
                isInitialized = true;
            }
        } catch (ClientConnectionException e) {
            ErrorAlert.show(e.getMessage());
        }
    }

    private void initGame() {
        initMap();
        citiesGameMap = gameStorage.getMaps().get(0);
        color = Color.RED;
        getCurrentGame();
        //    setAllNumbersValue(10);
        startIncrementingAll();
        initCitiesMap();
        startGameProcess();
    }

    private void getCurrentGame() {
        new Thread(
                () -> {
                    try {
                        while (isFinished) {
                            Response response = HighLevelMessageManager.getGame(senderSocket);
                            System.out.println(response);
                            if (response.type() == Response.Type.RESPONSE_ERROR) {
                                throw new ServerException(((ResponseError) response.value()).errorMessage());
                            } else {
                                game = (Game) response.value();
                                incrementRate = game.armyGrowthRate();
                                duration = game.armySpeed();
                            }
                            for (City city : game.usersCities().keySet()) {
                                Color color = null;
                                if (game.usersCities().get(city) != null) {
                                    color = Converter.convertColor(game.usersColor().get(game.usersCities().get(city)));
                                } else {
                                    color = Converter.convertColor(java.awt.Color.GRAY);
                                }
                                Button button = getButton(city);
                                setStyle(button, color);

                            }
                            Thread.sleep(5000L);
                        }
                    } catch (IOException e) {
                        ErrorAlert.show(e.getMessage());
                        GlobalStorage.getInstance().getMainApp().closeGame();
                    } catch (ProtocolVersionException | ServerException | InterruptedException e) {
                        ErrorAlert.show(e.getMessage());
                    }

                }
        ).start();
    }

    private void initCitiesMap() {
        citiesMap.put(cityBtnFirst, new City(1, 0, 0));
        citiesMap.put(cityBtnSecond, new City(2, 0, 0));
        citiesMap.put(cityBtnThird, new City(3, 0, 0));
        citiesMap.put(cityBtnFourth, new City(4, 0, 0));
        citiesMap.put(cityBtnFifth, new City(5, 0, 0));
        citiesMap.put(cityBtnSixth, new City(6, 0, 0));
    }

    private void startGameProcess() {
        new Thread(
                () -> {

                    try {
                        while (true) {
                            System.out.println("HERE");
                            System.out.println(receiverSocket);
                            Request request = HighLevelMessageManager.readRequest(receiverSocket);
                            HighLevelMessageManager.sendResponseSuccess(null, receiverSocket);
                            System.out.println(request);
                            if (request.type() == Request.Type.GAME_ACTION_ARMY_START_MOVE) {
                                GameArmyStartMove move = (GameArmyStartMove) request.value();
                                Way way = move.way();
                                System.out.println(way);
                                Button buttonStart = getButton(way.getStart());
                                Button buttonEnd = getButton(way.getEnd());
                                Platform.runLater(() -> {
                                    game.citiesArmies().replace(way.getStart(), game.citiesArmies().get(way.getStart()) - move.armyCount());
                                    buttonStart.setText(String.valueOf(game.citiesArmies().get(way.getStart())));
                                });
                                System.out.println(buttonStart + " " + buttonEnd);
                                City startCity = way.getStart();
                                City endCity = way.getEnd();
                                User owner = game.usersCities().get(endCity);
                                java.awt.Color newColor = game.usersColor().get(owner);
                                System.out.println("buttons" + buttonStart + " " + buttonEnd);
                                Platform.runLater(() -> {
                                    drawBall(buttonStart, buttonEnd, Converter.convertColor(java.awt.Color.GREEN), 4);
                                });
                            } else if (request.type() == Request.Type.GAME_ENDED) {
                                GameResults gameResults = (GameResults) request.value();
                                ErrorAlert.show(gameResults.winner().nickname() + " won!! ");
                                GlobalStorage.getInstance().getMainApp().closeGame();
                                isFinished = false;
                            } else if (request.type() == Request.Type.GAME_ACTION_ARMY_END_MOVE) {
                                GameArmyEndMove gameArmyEndMove = (GameArmyEndMove) request.value();
                                City city = gameArmyEndMove.city();
                                User user = gameArmyEndMove.user();
                                game.citiesArmies().replace(city, gameArmyEndMove.armyCount());
                                game.usersCities().replace(city, user);
                                int armyCount = gameArmyEndMove.armyCount();
                                Button button = getButton(city);
                                try {
                                    Color color = Converter.convertColor(game.usersColor().get(user));
                                    setStyle(button, color);
                                } catch (Exception e) {
                                    setStyle(button, Color.GRAY);
                                }
                                Platform.runLater(() -> {
                                    button.setText(String.valueOf(game.citiesArmies().get(city)));
                                });
                                game.usersCities().replace(city, user);
                            }
                        }
                    } catch (IOException e) {
                        ErrorAlert.show(e.getMessage());
                        GlobalStorage.getInstance().getMainApp().closeGame();
                    } catch (ProtocolVersionException e) {
                        System.out.println(e.getMessage());
                    }

                }
        ).start();
    }

    private void initMap() {
        allAvailableCities = new ArrayList<>();
        allAvailableCities.add(cityBtnFirst);
        allAvailableCities.add(cityBtnSecond);
        allAvailableCities.add(cityBtnThird);
        allAvailableCities.add(cityBtnFourth);
        allAvailableCities.add(cityBtnFifth);
        allAvailableCities.add(cityBtnSixth);
        MapGenerator mapGenerator = new MapGenerator(allAvailableCities);
        mapGenerator.generate();
        gameStorage.addMap(mapGenerator.getMap());
    }

    private void setAllNumbersValue(int value) {
        for (Button button : allAvailableCities) {
            setButtonText(button, value);
        }
    }

    private void startIncrementingAll() {
        new Thread(
                () -> {
                    while (isFinished) {
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException e) {
                            // ErrorAlert.show(e.getMessage());
                        }
                        for (City city : game.citiesArmies().keySet()) {
                            if (game.usersCities().get(city) != null) {
                                game.citiesArmies().replace(city, Math.min(game.citiesArmies().get(city) + game.armyGrowthRate(), 99));
                            }
                        }
                        for (Button button : allAvailableCities) {
                            int value = game.citiesArmies().get(getCity(button));
                            Platform.runLater(() -> {
                                button.setText(String.valueOf(value));
                            });
                        }

                    }
                }
        ).start();
    }

    private void makeMove(Button fromButton, Button toButton) {
        new Thread(
                () -> {
                    try {
                        City cityFrom = getCity(fromButton);
                        City cityTo = getCity(toButton);
                        System.out.println(" " + cityFrom + cityTo);
                        if (cityFrom != null && cityTo != null) {
                            Way way = new Way(cityFrom, cityTo);
                            int armyCount = Integer.parseInt(fromButton.getText()) / 2;
                            GameArmyStartMove move = new GameArmyStartMove(
                                    way, armyCount
                            );
                            Response response = HighLevelMessageManager.moveArmyStart(move, senderSocket);
                            if (response.type() == Response.Type.RESPONSE_ERROR) {
                                ErrorAlert.show(((ResponseError) response.value()).errorMessage());
                            }
                        }

                    } catch (IOException e) {
                        ErrorAlert.show(e.getMessage());
                        GlobalStorage.getInstance().getMainApp().closeGame();
                    } catch (ProtocolVersionException e) {
                        ErrorAlert.show(e.getMessage());
                    }

                }
        ).start();
    }


    private City getCity(Button button) {
        return citiesMap.get(button);
    }

    private Button getButton(City city) {
        if (city.number() == 1) {
            return cityBtnFirst;
        }
        if (city.number() == 2) {
            return cityBtnSecond;
        }
        if (city.number() == 3) {
            return cityBtnThird;
        }
        if (city.number() == 4) {
            return cityBtnFourth;
        }
        if (city.number() == 5) {
            return cityBtnFifth;
        }
        if (city.number() == 6) {
            return cityBtnSixth;
        }
        return null;
    }


    private void drawBall(Button fromButton, Button toButton, Color color, int durationInSeconds) {
        System.out.println(new Route(fromButton, toButton));
        //    if (!citiesGameMap.routes().contains(new Route(fromButton, toButton))) {
        //      return;
        // }
        widthMargin = cityBtnFirst.getWidth() / 2;
        Circle ball = new Circle(-fromButton.getLayoutX() - widthMargin, -fromButton.getLayoutY() - widthMargin, widthMargin / 2);
        ball.fillProperty().set(color);

        pane.getChildren().add(ball);
        PathTransition transition = new PathTransition();
        transition.setNode(ball);
        transition.setDuration(Duration.seconds(durationInSeconds));

        setPositionFixed(ball, fromButton);

        double toX = toButton.getLayoutX() + widthMargin / 2;
        double toY = toButton.getLayoutY() + widthMargin / 2;

        Path path = new Path();
        path.getElements().add(new MoveToAbs(ball));
        path.getElements().add(new LineToAbs(ball, toX, toY));

        transition.setPath(path);
        transition.play();

        ball.setTranslateX(toButton.getLayoutX());
        ball.setTranslateY(toButton.getLayoutY());
        removeBall(transition, durationInSeconds, ball);
    }

    public void btnFirstClicked(ActionEvent actionEvent) {
        handleButtonClick(cityBtnFirst, color, duration);
    }

    public void btnSecondClicked(ActionEvent actionEvent) {
        handleButtonClick(cityBtnSecond, color, duration);
    }

    public void btnThirdClicked(ActionEvent actionEvent) {
        handleButtonClick(cityBtnThird, color, duration);
    }

    public void btnFourthClicked(ActionEvent actionEvent) {
        handleButtonClick(cityBtnFourth, color, duration);

    }

    public void btnFifthClicked(ActionEvent actionEvent) {
        handleButtonClick(cityBtnFifth, color, duration);
    }

    public void btnSixthClicked(ActionEvent actionEvent) {
        handleButtonClick(cityBtnSixth, color, duration);

    }

    private void handleButtonClick(Button clickedButton, Color color, int duration) {
        drawWays();
        if (isFirst) {
            fromButton = clickedButton;
            drawSelectionBorder(fromButton);
            isFirst = false;
        } else {
            if (fromButton != null) {
                clearSelectionBorder(fromButton);
            }
            makeMove(fromButton, clickedButton);
            isFirst = true;
        }
        pane.requestFocus();
    }

    public static class MoveToAbs extends MoveTo {
        public MoveToAbs(Node node) {
            super(node.getLayoutBounds().getWidth() / 2, node.getLayoutBounds().getHeight() / 2);
        }

        public MoveToAbs(Node node, double x, double y) {
            super(x - node.getLayoutX() + node.getLayoutBounds().getWidth() / 2, y - node.getLayoutY() + node.getLayoutBounds().getHeight() / 2);
        }
    }

    private void setPositionFixed(Node node, Button fromButton) {
        node.relocate(fromButton.getTranslateX() - widthMargin, fromButton.getTranslateY() - widthMargin);
    }

    public static class LineToAbs extends LineTo {
        public LineToAbs(Node node, double x, double y) {
            super(x - node.getLayoutX() + node.getLayoutBounds().getWidth() / 2, y - node.getLayoutY() + node.getLayoutBounds().getHeight() / 2);
        }
    }


    private void removeBall(Transition transition, int durationInSeconds, Circle ball) {
        new Thread(() -> {
            try {
                Thread.sleep(durationInSeconds * 1000L);
                transition.stop();
                Platform.runLater(() -> {
                    pane.getChildren().remove(ball);
                });
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }


    private void drawWays() {
        gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.RED);
        widthMargin = cityBtnFirst.getWidth() / 2;
        for (Route route : citiesGameMap.routes()) {
            drawStrokeLine(route.fromCity(), route.toCity());
        }
    }

    private void drawStrokeLine(Button buttonFrom, Button buttonTo) {
        gc.strokeLine(
                buttonFrom.getLayoutX() + widthMargin, buttonFrom.getLayoutY() + widthMargin, buttonTo.getLayoutX() + widthMargin, buttonTo.getLayoutY() + widthMargin
        );
    }

    private void setStyle(Button button, Color color) {
        java.awt.Color newColor = Converter.convertColor(color);
        Platform.runLater(() -> {
            button.setStyle("-fx-background-color: rgb(" + newColor.getRed() + ","
                    + newColor.getGreen() + "," + newColor.getBlue() + ");");
        });

    }


    private void setButtonText(Button button, int number) {
        Platform.runLater(() -> {
            button.setText(String.valueOf(number));
        });
    }

    private void drawSelectionBorder(Button button) {
        Platform.runLater(() -> {
          button.setStyle("-fx-border-width: 2px");
        });
    }

    private void clearSelectionBorder(Button button) {
        Platform.runLater(() -> {
            button.setStyle("-fx-border-width: 0px");
        });
    }


}
