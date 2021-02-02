package øvinger.en.web.http;

import øvinger.en.web.html.HtmlBuilder;
import øvinger.en.web.http.error.ClientError;
import øvinger.en.web.http.error.ServerError;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
        LOGGER.log(Level.INFO, "Listening on " + serverSocket.getLocalPort());

        while (serverSocket.isBound()) {
            try {
                Socket connection = serverSocket.accept();
                LOGGER.log(Level.INFO, "Accepted connection from " + connection.getInetAddress());

                handleConnection(connection);
            }
            catch (IOException e) { // fixme
                LOGGER.log(Level.SEVERE, "Exception while accepting connection: " + e.toString());
            }
        }
        closeQuietly(serverSocket);
    }

    /**
     * Handles the communication-connection between the server/client
     */
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
            sendErrorResponseAndSuppress(response, out);
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception while handling connection: " + e.toString());
        }
        finally {
            closeQuietly(reader);
            closeQuietly(out);
            closeQuietly(connection);
            LOGGER.log(Level.INFO, "Closed connection with " + connection.getInetAddress());
        }
    }

    /**
     * Handles the given request and returns an appropriate response.
     *
     * @throws ServerError if the request method is not implemented
     * @throws ClientError if the requested resource does not exist
     */
    private HttpResponse handleRequest(HttpRequest request) {
        if (!request.getMethodToken().equals("GET")) {
            throw new ServerError("501 Not Implemented", null);
        }

        if (!request.getRequestTarget().substring(1).equals("")) {
            throw new ClientError("404 Not Found", "The requested resource does not exist");
        }

        HttpResponse response = new HttpResponse();
        byte[] payload = generateResource(request);
        response.setStatusLine("HTTP/1.1 200 OK");
        response.addHeader("Content-Type", "text/html; charset=utf-8");
        response.addHeader("Content-Length", payload.length + "");
        response.setPayLoad(payload);

        return response;
    }

    /**
     * Builds a default page to serve, in accordance with the assignment
     */
    private byte[] generateResource(HttpRequest request) {
        StringBuilder builder = new StringBuilder();
        LinkedHashMap<String, String> headers = request.getHeaderTable();

        HtmlBuilder htmlBuilder = new HtmlBuilder("Øving 1");

        htmlBuilder.append("<h1>Velkommen, sjekk ut de header-fieldsa a!</h1>");
        htmlBuilder.append("<ul>");
        headers.forEach((name, value) ->
                builder.append("\t<li>").append(name).append(": ").append(value).append("</li>\n")
        );
        htmlBuilder.append(builder.toString());
        htmlBuilder.append("</ul>");

        return htmlBuilder.complete();
    }


    // Convenience methods --------------------------------------------------------------------------------------------

    private void logAndExit(String message) {
        LOGGER.log(Level.SEVERE, message);
        System.exit(-1);
    }

    private void sendErrorResponseAndSuppress(HttpResponse response, OutputStream out) {
        try {
            response.send(out);
        } catch (IOException e) {
            // Shh...
        }
    }

    public void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
            // Shh...
        }
    }
}