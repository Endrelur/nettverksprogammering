package øvinger.en.web.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * Data-structure for parsing and storing the http-request.
 * NB! Incomplete.
 */
public class HttpRequest {

    private String methodToken;
    private String requestTarget;
    private String protocolVersion;

    // <field-name, field-value>
    private LinkedHashMap<String, String> headers;

    // byte[] payload; Ignored, since we are not dealing with request-bodies in the assignment

    public void parseRequest(BufferedReader reader) throws IOException {
        parseRequestLine(reader);
        parseHeaders(reader);
        // parseBody(reader);
    }

    private void parseRequestLine(BufferedReader reader) throws IOException {
        // RFC 72030 - 3.1.1
        String[] requestLine = reader.readLine().split("\\s+");
        methodToken     = requestLine[0];
        requestTarget   = requestLine[1];
        protocolVersion = requestLine[2];
    }

    private void parseHeaders(BufferedReader reader) throws IOException {
        // RFC 7230 - 3.2
        headers = new LinkedHashMap<>();

        String line = reader.readLine();
        // Breaks on the CRLF before the body section, i.e. the CRLF  is read
        while (line.length() > 0) {
            String[] headerField = line.split(":", 2);
            headers.put(headerField[0], headerField[1].trim());
            line = reader.readLine();
        }
    }

    // private void parseBody(BufferedReader reader) {
        // RFC 7230 - 3     General strategy
        // RFC 7230 - 3.3   Indications: Content-Length and Transfer-Encoding
    // }

    public String getMethodToken() {
        return methodToken;
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public boolean containsFieldName(String fieldName) {
        return headers.containsKey(fieldName);
    }

    public String getFieldValue(String fieldName) {
        return headers.get(fieldName.toLowerCase());
    }

    public String getRequestLine() {
        return String.format("%s %s %s\r\n", methodToken, requestTarget, protocolVersion);
    }

    public String getRequestHeaders() {
        StringBuilder builder = new StringBuilder();
        headers.forEach((key, value) -> builder.append(key).append(": ").append(value).append("\r\n"));
        return builder.append("\r\n").toString();
    }

    public LinkedHashMap<String, String> getHeaderTable() {
        return headers;
    }

    public String toString() {
        return getRequestLine() + getRequestHeaders();
    }
}
