package TestClient;

import Protocol.HighLevelMessageManager;
import Protocol.Message.Request;
import Protocol.Message.RequestValues.*;
import Protocol.Message.Response;
import Protocol.Message.ResponseValues.Room;
import Protocol.Message.models.City;
import Protocol.Message.models.RoomAccess;
import Protocol.Message.models.Way;
import Protocol.MessageManager;
import Protocol.ProtocolVersionException;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;

public class Test {

    private static String code;

    private static int PORT = 8888;

    private static Socket socket11;
    private static Socket socket12;

    private static Socket socket21;
    private static Socket socket22;





    public static void main(String[] args) throws IOException, ProtocolVersionException, InterruptedException {

        user1();
        user2();

        Response response = HighLevelMessageManager.startGame(
                socket11
        );
        System.out.println(response.type());
        System.out.println(response.value());

        System.out.println("read1");

        Request request = HighLevelMessageManager.readRequest(socket12);
        System.out.println(request.type());
        System.out.println(request.value());
        HighLevelMessageManager.sendResponseSuccess(null,socket12);

        System.out.println("end");

        System.out.println("read2");

        request = HighLevelMessageManager.readRequest(socket22);
        System.out.println(request.type());
        System.out.println(request.value());
        HighLevelMessageManager.sendResponseSuccess(null, socket22);


        Thread.sleep(10000);

        response = HighLevelMessageManager.moveArmyStart(
                new GameArmyStartMove(
                     new Way(
                          new City(1, 1,1),
                          new City(2,1,1)
                     ),
                     15
                ),
                socket11
        );
        System.out.println(response.type());
        System.out.println(response.value());


//        socket21.close();

        request = HighLevelMessageManager.readRequest(socket12);
        System.out.println(request.type());
        System.out.println(request.value());

        request = HighLevelMessageManager.readRequest(socket22);
        System.out.println(request.type());
        System.out.println(request.value());


        request = HighLevelMessageManager.readRequest(socket12);
        System.out.println(request.type());
        System.out.println(request.value());

        request = HighLevelMessageManager.readRequest(socket22);
        System.out.println(request.type());
        System.out.println(request.value());

        System.out.println("---");

        response = HighLevelMessageManager.getGame(socket11);
        System.out.println(response.type());
        System.out.println(response.value());

//        Thread.sleep(100000);

    }

    public static void user1() throws ProtocolVersionException, IOException {
        socket11 = new Socket("localhost", PORT);
        socket12 = new Socket("localhost", PORT);

        Response response = HighLevelMessageManager.start(null, socket11);
        String startCode = ((Start)response.value()).code();
        System.out.println(response.type());
        System.out.println(response.value());

        response = HighLevelMessageManager.start(new Start(startCode), socket12);
        System.out.println(response.type());
        System.out.println(response.value());


        UserLoginForm user = new UserLoginForm("email@mail.ru", "password");

        response = HighLevelMessageManager.loginUser(
                user,
                socket11
        );
        System.out.println(response.type());
        System.out.println(response.value());

        RoomInitializationForm form = new RoomInitializationForm(
                4,
                RoomAccess.PUBLIC,
                Color.RED,
                new GameInitializationForm(10, 20, 3));

        response = HighLevelMessageManager.initializeRoom(form,
                socket11
        );
        code = ((Room)response.value()).code();
        System.out.println(response.type());
        System.out.println(response.value());

        response = HighLevelMessageManager.setUserReadyToStart(
                socket11
        );
        System.out.println(response.type());
        System.out.println(response.value());


    }

    public static void user2() throws ProtocolVersionException, IOException {

        socket21 = new Socket("localhost", PORT);
        socket22 = new Socket("localhost", PORT);

        Response response = HighLevelMessageManager.start(null, socket21);
        String startCode = ((Start)response.value()).code();
        System.out.println(response.type());
        System.out.println(response.value());

        response = HighLevelMessageManager.start(new Start(startCode), socket22);
        System.out.println(response.type());
        System.out.println(response.value());

        UserLoginForm user = new UserLoginForm("email2@mail.ru", "password");

        response = HighLevelMessageManager.loginUser(
                user,
                socket21
        );
        System.out.println(response.type());
        System.out.println(response.value());

        RoomConnectionForm form = new RoomConnectionForm(
                code,
                Color.PINK);

        response = HighLevelMessageManager.connectToRoom(
                form,
                socket21
        );
        System.out.println(response.type());
        System.out.println(response.value());



        response = HighLevelMessageManager.setUserReadyToStart(
                socket21
        );
        System.out.println(response.type());
        System.out.println(response.value());

    }
}
