package øvinger.en.web.http.error;

// Used for 5xx http status codes
public class ServerError extends HttpError {

    public ServerError(String statusCode, String message) {
        super(statusCode, message);
    }
}
