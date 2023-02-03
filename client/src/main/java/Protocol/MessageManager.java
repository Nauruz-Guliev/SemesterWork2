package Protocol;


import Protocol.Message.Request;
import Protocol.Message.RequestValues.RequestValue;
import Protocol.Message.Response;
import Protocol.Message.ResponseValues.ResponseValue;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MessageManager {

    public final static byte VERSION = 4;


    /**Необходимо отправить ответ после вызова этой функции*/
    public static Request readRequest(Socket socket) throws IOException, ProtocolVersionException {
        InputStream in = socket.getInputStream();
        versionCheck(in.read());

        try {
            return new Request(
                    requestTypes.get((byte)in.read()),
                    (RequestValue) new ObjectInputStream(in).readObject()
            );
        } catch (ClassNotFoundException e) {
            throw new ProtocolVersionException();
        }

    }

    private static final Map<Byte, Request.Type> requestTypes = new HashMap<>();
    static {
        Arrays.stream(Request.Type.values()).
                forEach(type -> requestTypes.put(type.getValue(), type));
    }


    private static void sendRequestWithOutResponse(Request request, Socket socket) throws IOException {
        if (request.type() == null) {
            throw new NullPointerException("Type can't be null");
        }

        OutputStream out = socket.getOutputStream();
        out.write(VERSION);
        out.write(request.type().getValue());
        new ObjectOutputStream(out).writeObject(request.value());
        out.flush();
    }

    public static Response sendRequest(Request request, Socket socket) throws IOException, ProtocolVersionException {
        sendRequestWithOutResponse(request, socket);

        //чтение ответа
        InputStream in = socket.getInputStream();
        versionCheck(in.read());
        try {
            return new Response(
                    responseTypes.get((byte)in.read()),
                    (ResponseValue) new ObjectInputStream(in).readObject()
            );
        } catch (ClassNotFoundException e) {
            throw new ProtocolVersionException(e);
        }
    }

    private static final Map<Byte, Response.Type> responseTypes = new HashMap<>();
    static {
        Arrays.stream(Response.Type.values()).
                forEach(type -> responseTypes.put(type.getValue(), type));
    }



    protected static void sendResponse(Response response, Socket socket) throws IOException {
        if (response.type() == null) {
            throw new NullPointerException("Type can't be null");
        }

        OutputStream out = socket.getOutputStream();
        out.write(VERSION);
        out.write(response.type().getValue());
        new ObjectOutputStream(out).writeObject(response.value());
        out.flush();
    }



    private static void versionCheck(int version) throws IOException, ProtocolVersionException {
        if (version == -1) {
            throw new IOException("Connection lost");
        }
        if (version != VERSION) {
            throw new ProtocolVersionException();
        }
    }


}
