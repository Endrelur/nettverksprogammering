package øvinger.en.web.http.error;

public class ServerError extends HttpError {

    public ServerError(String statusCode, String message) {
        super(statusCode, message);
    }
}
