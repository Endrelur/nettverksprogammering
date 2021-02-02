package øvinger.en.web.http;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Data-structure for building and writing http-responses
 */
public class HttpResponse {

    private String statusLine;
    private final StringBuilder HEADER_BUILDER;
    private byte[] payload;

    public HttpResponse() {
        HEADER_BUILDER = new StringBuilder();
    }


    public void setStatusLine(String statusLine) {
        this.statusLine = (statusLine + "\r\n");
    }

    public void setPayLoad(byte[] payLoad) {
        this.payload = payLoad;
    }

    public void addHeader(String fieldName, String fieldValue) {
        HEADER_BUILDER.append(fieldName.trim())
                      .append(":")
                      .append(fieldValue)
                      .append("\r\n");
    }

    public byte[] getStatusLine() {
        return statusLine.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getHeaders() {
        return HEADER_BUILDER.append("\r\n")
                             .toString()
                             .getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getBody()  {
        if (payload == null) {
            payload = new byte[0];
        }
        return payload;
    }

    /**
     * Writes the http-response [status-line + headers + body]
     * to the given output stream and flushes the stream.
     */
    public void send(OutputStream out) throws IOException {
        out.write(getStatusLine());
        out.write(getHeaders());
        out.write(getBody());
        out.flush();
    }
}
