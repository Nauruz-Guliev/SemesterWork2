package Server.app;

import java.util.Date;

public class ErrorLogger {

    public static void log(String message) {
        System.out.println(new Date());
        System.out.println(message);
    }
}
