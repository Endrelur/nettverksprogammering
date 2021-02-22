package øvinger.en.web.http.error;

// Used for 4xx http status codes
public class ClientError extends HttpError {

    public ClientError(String statusCode, String message) {
        super(statusCode, message);
    }

}
