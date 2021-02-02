package øvinger.en.web.http.error;

import øvinger.en.web.http.HttpResponse;

import java.nio.charset.StandardCharsets;

public abstract class HttpError extends RuntimeException {

    private HttpResponse response;

    HttpError(String statusCode, String message) {
        super(statusCode);

        if (message == null) {
            message = "";
        }

        response = buildResponse(statusCode, message);
    }

    protected HttpResponse buildResponse(String statusCode, String message) {
        HttpResponse response = new HttpResponse();

        response.setStatusLine("HTTP/1.1 " + statusCode);

        byte[] payload = String.format("<h1>%s</h1><br><p>%s</p>", statusCode, message).getBytes(StandardCharsets.UTF_8);
        response.addHeader("Content-Type", "text/html; charset=utf-8");
        response.addHeader("Content-Length", "" + payload.length);

        return response;
    }

    public HttpResponse getResponse() {
        return response;
    }
}
