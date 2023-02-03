package connection;


import Protocol.HighLevelMessageManager;
import Protocol.Message.RequestValues.Start;
import Protocol.Message.Response;
import Protocol.ProtocolVersionException;
import exceptions.ClientConnectionException;

import java.io.IOException;
import java.net.Socket;

public class ClientConnectionSingleton {
    private static ClientConnectionSingleton instance;
    private static Socket socketSender;
    private static Socket socketReceiver;
    private static final int PORT = 8888;
    private static boolean isInitialized = false;

    private ClientConnectionSingleton() {
    }

    // static block initialization for exception handling
    static {
        try {
            instance = new ClientConnectionSingleton();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in creating singleton instance");
        }
    }

    public static ClientConnectionSingleton getInstance() throws ClientConnectionException {
        if (!isInitialized) {
            init();
            isInitialized = true;
        }
        return instance;
    }

    private static void init() throws ClientConnectionException {
        try {
            if (socketReceiver == null && socketSender == null) {
                socketSender = new Socket("localhost", PORT);
                socketReceiver = new Socket("localhost", PORT);
                Response response = HighLevelMessageManager.start(null, socketSender);
                String startCode = ((Start) response.value()).code();
                response = HighLevelMessageManager.start(new Start(startCode), socketReceiver);
                if (response.type() == Response.Type.RESPONSE_ERROR) {
                    throw new ClientConnectionException("Unable to connect");
                }
            }
        } catch (ProtocolVersionException | IOException ex) {
            throw new ClientConnectionException("Couldn't connect. Server is closed!");
        }
    }


    public boolean isConnected() {
        return !socketSender.isClosed() && !socketReceiver.isClosed();
    }


    public Socket getSocketSender() {
        return socketSender;

    }

    public Socket getSocketReceiver() {
        return socketReceiver;
    }

}
