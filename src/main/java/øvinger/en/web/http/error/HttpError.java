package øvinger.en.web.http.error;

import øvinger.en.web.html.HtmlBuilder;
import øvinger.en.web.http.HttpResponse;

// General http-exception for setting up a response to the client
public abstract class HttpError extends IllegalArgumentException {

    private HttpResponse response;

    HttpError(String statusCode, String message) {
        super(statusCode);

        if (message == null) {
            message = "";
        }

        response = buildResponse(statusCode, message);
    }

    /**
     * Builds a response html-page for displaying error-information to the client
     */
    protected HttpResponse buildResponse(String statusCode, String message) {
        HttpResponse response = new HttpResponse();

        response.setStatusLine("HTTP/1.1 " + statusCode);

        HtmlBuilder htmlBuilder = new HtmlBuilder(statusCode);
        htmlBuilder.append("<h1>").append(statusCode).append("</h1>");
        htmlBuilder.append("<p>").append(message).append("<p>");

        byte[] payload = htmlBuilder.complete();

        response.addHeader("Content-Type", "text/html; charset=utf-8");
        response.addHeader("Content-Length", "" + payload.length);
        response.setPayLoad(payload);

        return response;
    }

    public HttpResponse getResponse() {
        return response;
    }
}
