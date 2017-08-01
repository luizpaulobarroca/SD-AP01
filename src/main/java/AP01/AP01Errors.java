package AP01;

public class AP01Errors {
    public static String response(String message) {
        String string;

        string = "{ \"message\": \"" + message + "\"}";

        return string;
    }
}
