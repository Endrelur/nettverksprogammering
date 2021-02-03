package testing.http.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Simple example to demonstrate handling of http-requests/responses
 * > Supports GET-requests
 * > Ignores header-fields when processing requests
 * > Ignores request-bodies completely
 * > Does not handle exceptions
 * > Generally do not handle malformed / bad requests appropriately, but sends some feedback on wrong requests
 * > Handles one request, then closes the connection. So not very efficient!
 */
public class Main {

    /**
     * The directory to serve files from
     */
    private static final String directory = "src/main/resources/web-server";

    /**
     * Creates the server-socket and start handling connection
     */
    public static void main(String[] args) throws Exception {

        ServerSocket socket = new ServerSocket(8080);

        while (socket.isBound()) {
            Socket connection = socket.accept();
            handleConnection(connection);
        }
    }

    /**
     * Handles one request from the client, then closes the connection
     */
    private static void handleConnection(Socket connection) throws Exception {
        System.out.println("\nAccepted connection with " + connection.getInetAddress());

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        BufferedOutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());

        // Process request
        String[] requestLine = parseRequest(reader);
        byte[] response = processRequest(requestLine);

        // Send response
        outputStream.write(response);
        outputStream.flush();

        reader.close();
        outputStream.close();
        connection.close();

        System.out.println("\nClosed connection with " + connection.getInetAddress());
    }

    /**
     * Reads the request-line and request-headers (if present) and prints
     * them to the terminal. A potential request-body is ignored
     * Returns the tokens in the request-line is returned.
     */
    private static String[] parseRequest(BufferedReader reader) throws IOException {

        // Message format: https://tools.ietf.org/html/rfc7230#section-3

        // ========================================== //
        // PARSE REQUEST LINE: RFC 7230 section 3.1.1 //
        // ========================================== //

        String requestLine = reader.readLine();
        System.out.println(requestLine);

        // [method, resource, protocol-version]
        String[] tokens = requestLine.split(" ");

        // =========================================== //
        // PARSE REQUEST HEADERS: RFC 7230 section 3.2 //
        // =========================================== //

        String headerField;
        while (!(headerField = reader.readLine()).equals("")) {
            System.out.println(headerField);
        }

        // ================================== //
        // IGNORES the potential request body //
        // ================================== //

        return tokens;
    }

    private static byte[] processRequest(String[] requestLine) throws IOException {
        String methodToken = requestLine[0];
        String resourceTarget = requestLine[1];

        // Validate that the server can handle the request-method
        // More on request-methods in RFC 7231
        if (!methodToken.equals("GET")) {
            return notImplemented();
        }

        // Change resource to index if root was requested
        if (resourceTarget.equals("/")) {
            resourceTarget = "/index.html";
        }

        // RFC 7231 for more information about resources
        File file = new File(directory + resourceTarget);

        // Validate that the requested resource exist
        if (!file.exists()) {
            file = new File(directory + "/not-found.html");
        }

        // Do some more validation the resource
        else if (file.isDirectory()) {
            // 403 Forbidden, 405 Method Not Allowed, 418 I'm a teapot, ens...
        }

        return buildResponse(file);
    }

    /**
     * Builds the response
     * Check RFC 7230 for formatting information
     * Check RFC 7231 for information header-fields and status-codes
     */
    private static byte[] buildResponse(File file) throws IOException {

        // =========== //
        // Status-line //
        // =========== //

        byte[] statusLine = "HTTP/1.1 200 OK\r\n".getBytes(StandardCharsets.UTF_8);

        // ================ //
        // Response headers //
        // ================ //

        StringBuilder builder = new StringBuilder();

        // Content-Type
        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            // Set in case the content type cannot be guessed. RFC 7231, section 3.1.1.5
            contentType = "application/octet-stream";
        }
        builder.append("Content-Type: ").append(contentType).append("\r\n");

        // Content-Length
        builder.append("Content-Length: ").append(file.length()).append("\r\n");

        // Apply some default headers
        builder.append("Connection: close\r\n");

        // Mark the start of the body.
        // NB! Also needed when no headers are sent.
        builder.append("\r\n");

        byte[] headers = builder.toString().getBytes(StandardCharsets.UTF_8);

        // ================ //
        // Response body    //
        // ================ //

        byte[] payload = Files.readAllBytes(file.toPath());


        // Finish the response by wrapping the different parts in one array
        byte[] response = new byte[statusLine.length + headers.length + payload.length];
        return ByteBuffer.wrap(response).put(statusLine).put(headers).put(payload).array();
    }

    // Just to have something to send if a client sends an unsupported request-method
    private static byte[] notImplemented() {
        return ("HTTP/1.1 501 Not Implemented\r\n" +
                "Content-Length: 0\r\n" +
                "Connection: close\r\n" +
                "\r\n").getBytes(StandardCharsets.UTF_8);
    }
}
