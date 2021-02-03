package testing.http.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Simple example to demonstrate handling of http-requests/responses
 * > Supports GET-requests
 * > Ignores header-fields when processing requests
 * > Ignores request-bodies completely
 * > Does not handle exceptions
 * > Generally do not handle malformed / bad requests appropriately, but sends some feedback on wrong requests
 * > Handles one request, then closes the connection. So not very efficient!
 *
 */
public class Main {

    /**
     * The directory to serve files from
     */
    private static final String directory = "src/main/resources/web-server";

    // For providing the time in the Date-header (see build response)
    private static final Calendar calendar = Calendar.getInstance();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat();

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
        System.out.println(">> Accepted connection with " + connection.getInetAddress() + "\n");

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

        System.out.println("\n>> Closed connection with " + connection.getInetAddress() + "\n\n");
    }

    /**
     * Reads the request-line and request-headers (if present) and prints
     * them to the terminal. A potential request-body is ignored
     * Returns the tokens in the request-line is returned.
     *
     * Message format: https://tools.ietf.org/html/rfc7230#section-3
     */
    private static String[] parseRequest(BufferedReader reader) throws IOException {

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
        // The end of the header section is an empty line
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

        // Get the requested resource
        File file = new File(directory + resourceTarget);

        // Validate that the resource exist
        if (!file.exists()) {
            file = new File(directory + "/not-found.html");
        }

        // Do some more validation, like making sure its
        // not a directory, stopping directory traversal,
        // that the requested method can be applied to the resource, ...
        /*else if (file.isDirectory()) {
            403 Forbidden, 405 Method Not Allowed, 418 I'm a teapot, ...
        }*/

        return buildResponse(file);
    }

    /**
     * Builds the response. A limited set of headers has been selected.
     * Check RFC 7230 for formatting information (it's mostly the same format as request-messages)
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
        builder.append("Content-Type: ").append(guessContentType(file)).append("\r\n");
        builder.append("Content-Length: ").append(file.length()).append("\r\n");
        builder.append("Connection: close\r\n");
        builder.append("Date: ").append(getServerTime()).append("\r\n");

        // Mark the end of the header-section
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

    /**
     * Creates a response for when client request a non-implemented method
     * Just to have something to send if a client sends an unsupported request-method
     */
    private static byte[] notImplemented() {
        return ("HTTP/1.1 501 Not Implemented\r\n" +
                "Content-Length: 0\r\n" +
                "Connection: close\r\n" +
                "\r\n").getBytes(StandardCharsets.UTF_8);
    }


    /**
     * Returns the content-type of a file or a default tag if it cannot be probed
     */
    private static String guessContentType(File file) throws IOException {
        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            // RFC 7231, section 3.1.1.5
            contentType = "application/octet-stream";
        }
        return contentType;
    }

    // "Hippety hoppety, your code is now my property"
    // https://stackoverflow.com/questions/7707555/getting-date-in-http-format-in-java
    private static String getServerTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }
}
