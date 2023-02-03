package Server.app;

import Protocol.HighLevelMessageManager;
import Protocol.Message.Request;
import Protocol.Message.RequestValues.RequestValue;
import Protocol.Message.RequestValues.Start;
import Protocol.ProtocolVersionException;
import Server.DB.DataSources.SimpleDataSource;
import Server.services.ServicesToolKit;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class DistributionCenter implements Runnable {

    private final ServerSocket server;

    private ServicesToolKit servicesToolKit;

    public DistributionCenter(ServerSocket server) {
        this.server = server;
        init();
    }


    public void init() {
        Properties properties = new Properties();
        try {
            properties.load(ServerApp.class.getResourceAsStream("/app.properties"));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        DataSource dataSource = new SimpleDataSource(
                properties.getProperty("db.url"),
                properties.getProperty("db.username"),
                properties.getProperty("db.password")
        );

        servicesToolKit = new ServicesToolKit(dataSource);
    }


    private Map<String, Socket> codeToSocket = new HashMap<>();

    @Override
    public void run() {
        try {
            while (true) {
                Socket socket = server.accept();

                new Thread(() -> {
                    try {
                        Request request = HighLevelMessageManager.readRequest(socket);

                        if (request.type() != Request.Type.START) {
                            HighLevelMessageManager.sendResponseError("You need to start. ", socket);
                            return;
                        }

                        Start start = (Start) request.value();

                        if (start == null) {
                            String code = generateCode();
                            codeToSocket.put(code, socket);
                            HighLevelMessageManager.sendResponseSuccess(new Start(code), socket);
                            return;
                        }

                        if (!codeToSocket.containsKey(start.code())) {
                            HighLevelMessageManager.sendResponseError("Wrong code.", socket);
                            return;
                        }

                        HighLevelMessageManager.sendResponseSuccess(null, socket);
                        new Thread(new UserConnectionThread(codeToSocket.get(start.code()), socket, servicesToolKit)).start();
                        codeToSocket.remove(start.code());

                    } catch (IOException | ProtocolVersionException ignored) {}

                }).start();
            }
        } catch (IOException e) {
            ErrorLogger.log(e.getMessage());
        }
    }


    private String generateCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0,8);
        } while (codeToSocket.containsKey(code));
        return code;
    }
}
