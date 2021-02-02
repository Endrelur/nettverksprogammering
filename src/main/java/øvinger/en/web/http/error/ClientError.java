package øvinger.en.web.http.error;

public class ClientError extends HttpError {

    public ClientError(String statusCode, String message) {
        super(statusCode, message);
    }

}
