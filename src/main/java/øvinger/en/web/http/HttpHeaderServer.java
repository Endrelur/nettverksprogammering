package øvinger.en.web.http;

import øvinger.en.web.Utils;
import øvinger.en.web.http.error.ClientError;
import øvinger.en.web.http.error.ServerError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpHeaderServer {

    private static final Logger LOGGER = Logger.getLogger(HttpHeaderServer.class.getName());

    private ServerSocket serverSocket;

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            listen();
        }
        catch (IllegalArgumentException e) {
            logAndExit("Invalid port number. A port must be in the range [0, 65535]");
        }
        catch (SecurityException e) {
            logAndExit("Permission required.");
        }
        catch (IOException e) {
            logAndExit(e.toString());
        }
    }

    private void listen() {
        while (serverSocket.isBound()) {
            try {
                Socket connection = serverSocket.accept();
                handleConnection(connection);
            }
            catch (IOException e) { // fixme
                LOGGER.log(Level.SEVERE, "Exception while accepting connection: " + e.toString());
            }
        }
        Utils.closeQuietly(serverSocket);
    }

    private void handleConnection(Socket connection) {
        BufferedReader reader = null;
        OutputStream out = null;

        try {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            out = connection.getOutputStream();

            HttpRequest request = new HttpRequest();
            request.parseRequest(reader);

            HttpResponse response = handleRequest(request);
            response.send(out);
        }
        catch (ServerError | ClientError e) {
            HttpResponse response = e.getResponse();
            sendErrorResponse(response, out);
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception while handling connection: " + e.toString());
        }
        finally {
            Utils.closeQuietly(reader);
            Utils.closeQuietly(out);
            Utils.closeQuietly(connection);
        }
    }

    private HttpResponse handleRequest(HttpRequest request) {
        if (!request.getMethodToken().equals("GET")) {
            throw new ServerError("501 Not Implemented", null);
        }

        if (!request.getRequestTarget().substring(1).equals("")) {
            throw new ClientError("404 Not Found", "The requested resource does not exist");
        }

        HttpResponse response = new HttpResponse();
        response.setStatusLine("HTTP/1.1 200 OK");

        byte[] payload = generateResource(request);
        response.setPayLoad(payload);
        response.addHeader("Content-Type", "text/html; charset=utf-8");
        response.addHeader("Content-Length", payload.length + "");

        return response;
    }

    private byte[] generateResource(HttpRequest request) {
        StringBuilder builder = new StringBuilder();
        LinkedHashMap<String, String> headers = request.getHeaderTable();
        builder.append("<ul>\n");
        headers.forEach((name, value) ->
                builder.append("\t<li>").append(name).append(": ").append(value).append("</li>\n")
        );
        builder.append("</ul>\n");

        String htmlResource =
                "<!DOCTYPE html>"                                                   + "\n" +
                "<html lang=\"en\" dir=\"ltr\">"                                    + "\n" +
                    "<head>"                                                        + "\n" +
                        "<meta charset=\"utf-8\">"                                  + "\n" +
                        "<title>Øving 1</title>"                                    + "\n" +
                    "</head>"                                                       + "\n" +
                    "<body>"                                                        + "\n" +
                        "<h1>Lorem ipsum dolor sit amet!</h1>"                      + "\n" +
                        builder.toString()                                          + "\n" +
                    "</body>"                                                       + "\n" +
                "</html>"                                                               ;

        return htmlResource.getBytes(StandardCharsets.UTF_8);
    }

    private void logAndExit(String message) {
        LOGGER.log(Level.SEVERE, message);
        System.exit(-1);
    }

    private void sendErrorResponse(HttpResponse response, OutputStream out) {
        try {
            response.send(out);
        } catch (IOException e) {
            // Shh...
        }
    }
}