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

        String htmlResource =
                "<!DOCTYPE html>"                                               + "\n" +
                    "<html lang=\"en\" dir=\"ltr\">"                            + "\n" +
                        "<head>"                                                + "\n" +
                            "<meta charset=\"utf-8\">"                          + "\n" +
                            "<title>" + statusCode + "</title>"                 + "\n" +
                        "</head>"                                               + "\n" +
                        "<body>"                                                + "\n" +
                            "<h1>" + statusCode + "</h1>"                       + "\n" +
                            "<p>" + message + "</p>"                            + "\n" +
                        "</body>"                                               + "\n" +
                    "</html>"                                                  ;

        byte[] payload = htmlResource.getBytes(StandardCharsets.UTF_8);
        response.addHeader("Content-Type", "text/html; charset=utf-8");
        response.addHeader("Content-Length", "" + payload.length);
        response.setPayLoad(payload);

        return response;
    }

    public HttpResponse getResponse() {
        return response;
    }
}
